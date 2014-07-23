package io.statik.v1;

import io.statik.Statik;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TODO JD
 */
final class StatikReportingTask extends BukkitRunnable {

    private static final int DELAY_30_MINUTES = 30 * 60 * 20;

    public StatikReportingTask(Statik statik) {
        // TODO Stuff
        this.runTaskTimerAsynchronously(/* FIXME Need a plugin here */ null, DELAY_30_MINUTES, DELAY_30_MINUTES);
    }

    @Override
    public void run() {
        // TODO Implement method
    }
}
