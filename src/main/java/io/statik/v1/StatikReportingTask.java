package io.statik.v1;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
        StatikDataMap dataMap = statik.collect();
        // TODO Json da stuff
        // TODO Send da stuff
        // TODO Some DEBUG log?
    }

    public String post(final String json) throws IOException {
        // TODO Do we compress data?
        URL url = new URL(null); // FIXME Should be statik.STATIK_ENDPOINT
        URLConnection connection = url.openConnection();
        byte[] data = json.getBytes();

        // Headers
        connection.addRequestProperty("User-Agent", ""); // TODO What do we put here?
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("Content-Length", String.valueOf(data.length));
        connection.addRequestProperty("Accept", "application/json");

        connection.setDoOutput(true);

        // Write the data
        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.flush();

        // Read the response
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = br.readLine();

        // close resources
        os.close();
        br.close();

        return response;
    }
}
