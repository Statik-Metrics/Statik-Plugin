package io.statik.v1;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class StatikBase {
    /**
     * The base stat class. Override however you like. Default
     * implementations are provided for convenience. Stats are fairly
     * straightforward. They require a name, which is unique on a per-plugin
     * basis, and a value. The value can either stay the same, or change
     * over time. The value is polled once every 30 minutes when Statik
     * sends its current state to the server. Here are a few example
     * scenarios for Stat usage:
     * <p/>
     * Problem: You wish to record a config value loaded at startup.<br />
     * Solution: Create and track a {@link StatikBase.ConstantStat}
     * with your config value.
     * <p/>
     * Problem: Your plugin has two teams: RED and BLUE. You want to track
     * the currently winning team on the server. <br />
     * Solution 1: Create and track a {@link
     * StatikBase.SettableStat} and set the winning team each time
     * the team changes.<br />
     * Solution 2: Write a subclass overriding Stat. For your {@link
     * #getValue()} method implementation, return the currently winning
     * team name. Create and track an instance of this new class.
     * <p/>
     * Problem: You wish to track usage of the command /meow.
     * <br />
     * Solution: Create and track a {@link
     * StatikBase.IncrementableStat} and
     * {@link StatikBase.IncrementableStat#increment()} it each
     * time the command is called. This class automatically resets the count
     * to 0 when the value is queried, making it perfect for tracking usage
     * over time. Let Statik's website track totals, as your total would
     * otherwise reset to 0 upon restart.
     */
    public abstract class Stat {
        private final String name;

        /**
         * Constructs a lovely stat.
         *
         * @param name name of this stat
         */
        protected Stat(String name) {
            this.name = name;
        }

        /**
         * Gets the stat name.
         *
         * @return name of this stat
         */
        public final String getName() {
            return this.name;
        }

        /**
         * Gets the stat value. May be dynamic.
         *
         * @return value of this stat
         */
        abstract Object getValue();

        /**
         * Gets the value, sanitized for accepted values.
         *
         * @return value of this stat
         */
        private Object getVal() {
            Object value = this.getValue();
            return this.isAcceptedPrimitive(value.getClass()) ? value : value.toString();
        }

        /**
         * Gets if the class is an accepted primitive. Any class not accepted
         * is simply hit with a good ol' fashioned {@link Object#toString()}.
         *
         * @param clazz class to check
         * @return true if the class is acceptable to send directly
         */
        protected final boolean isAcceptedPrimitive(Class<?> clazz) {
            return clazz == Boolean.class || clazz == Integer.class || clazz == Short.class || clazz == Double.class || clazz == Long.class || clazz == Float.class;
        }
    }

    /**
     * A stat which never changes.
     */
    public final class ConstantStat extends Stat {
        private final Object value;

        /**
         * Constructs a constant stat.
         *
         * @param name  name of this stat
         * @param value value of this stat
         */
        public ConstantStat(String name, Object value) {
            super(name);
            this.value = value;
        }

        @Override
        Object getValue() {
            return this.value;
        }
    }

    /**
     * A stat which can be updated to a new value.
     */
    public class SettableStat extends Stat {
        private Object value;

        /**
         * Constructs a settable stat.
         *
         * @param name  name of this stat
         * @param value initial value of this stat
         */
        public SettableStat(String name, Object value) {
            super(name);
            this.value = value;
        }

        /**
         * Sets the value of this stat.
         *
         * @param value new value of this stat
         */
        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        Object getValue() {
            return this.value;
        }
    }

    /**
     * A stat which can be incremented. Resets each time the server gets it.
     */
    public class IncrementableStat extends Stat {
        private int value;

        public IncrementableStat(String name) {
            super(name);
        }

        public synchronized void increment() {
            this.value++;
        }

        public synchronized void increment(int amount) {
            this.value += amount;
        }

        @Override
        synchronized Object getValue() {
            int ret = this.value;
            this.value = 0;
            return ret;
        }
    }

    private final Map<String, Stat> stats = new ConcurrentHashMap<String, Stat>();

    /**
     * Tracks a stat. Stat names are unique per plugin.
     *
     * @param stat stat to track
     * @return null or displaced stat, if a stat existed with the same name
     */
    public final Stat track(Stat stat) {
        return this.stats.put(stat.getName(), stat);
    }

    protected abstract String getPluginName();

    private class StatikFuture implements Future<Map<String, Object>> {
        @Override
        public Map<String, Object> get() throws InterruptedException, ExecutionException {
            final Map<String, Object> map = new HashMap<String, Object>();
            for (Stat stat : StatikBase.this.stats.values()) {
                map.put(stat.getName(), stat.getVal());
            }
            return map;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException("Statik is only misusing Future for convenience");
        }

        @Override
        public boolean isCancelled() {
            throw new UnsupportedOperationException("Statik is only misusing Future for convenience");
        }

        @Override
        public boolean isDone() {
            throw new UnsupportedOperationException("Statik is only misusing Future for convenience");
        }

        @Override
        public Map<String, Object> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            throw new UnsupportedOperationException("Statik is only misusing Future for convenience");
        }
    }

    protected final class StatHandlerAccessor {
        private final Method add;
        private final Object object;
        private final int version;

        protected StatHandlerAccessor(Object object) throws Throwable {
            this.add = object.getClass().getSuperclass().getDeclaredMethod("add", String.class, Future.class);
            this.version = (Integer) object.getClass().getDeclaredField("STATIK_VERSION").get(null);
            this.object = object;
        }

        protected void add() {
            try {
                this.add.invoke(this.object, StatikBase.this.getPluginName(), new StatikFuture());
            } catch (Exception ignored) {
                // If this fails, all is lost anyhow
            }
        }

        protected int getVersion() {
            return this.version;
        }
    }
}
