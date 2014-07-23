package io.statik.v1;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

/**
 * TODO JD
 */
final class StatikReportingTask extends BukkitRunnable {

    private static final int DELAY_30_MINUTES = 30 * 60 * 20;

    private final StatikImpl statik;

    public StatikReportingTask(StatikImpl statik, Plugin plugin) {
        this.statik = statik;
        // TODO Stuff?
        this.runTaskTimerAsynchronously(plugin, DELAY_30_MINUTES, DELAY_30_MINUTES);
    }

    @Override
    public void run() {
        Map<String, Object> dataMap = statik.collect();
        // TODO Json da stuff
        // TODO Send da stuff
        // TODO Some DEBUG log?
    }
}
