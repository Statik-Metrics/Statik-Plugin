package io.statik.v1;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * Handles nets. Do not modify this class.
 */
abstract class StatikNetHandler extends Thread {
    public static final String NOTICE = "DO NOT MODIFY THIS CLASS - MODIFICATIONS MAY RESULT STATIK.IO DELISTING";

    // TODO turn this entire mess into getters/setters
    protected static class Data {
        /**
         * Self-sufficient class.
         */
        private class Server {
            private class OS {
                String arch = System.getProperty("os.arch");
                String name = System.getProperty("os.name");
                String version = System.getProperty("os.version");
            }

            private int cores = Runtime.getRuntime().availableProcessors();
            private String java = System.getProperty("java.version");
            private long memory = Runtime.getRuntime().totalMemory();
            private OS os = new OS();
        }

        private static class Plugin {
            private static class PluginData {
                private String name;
                private Object value;

                PluginData(String name, Object value) {
                    this.name = name;
                    this.value = value;
                }
            }

            List<PluginData> data = new LinkedList<PluginData>();
            String name;
            String version;
        }

        /**
         * Requires some loving from the implementation.
         */
        protected class Minecraft {
            protected class Mod {
                String name;
                String version;
            }

            Mod mod = new Mod();
            boolean online_mode;
            int players;
            String version;
        }

        Minecraft minecraft = new Minecraft();
        List<Plugin> plugins = new LinkedList<Plugin>();
        Server server = new Server();
    }

    /**
     * Statik version, do not touch.
     */
    public static final int STATIK_VERSION = 1;

    private final UUID uuid;

    private final Map<String, Future<Map<String, Object>>> trackedPlugins = new ConcurrentHashMap<String, Future<Map<String, Object>>>();

    /**
     * Creates the net handler!
     *
     * @param serverHash UUID based on the server directory
     */
    StatikNetHandler(UUID serverHash) {
        this.uuid = serverHash;
    }

    // Dev note: Never change existing method, only add new methods
    void add(String name, Future<Map<String, Object>> dataFuture) {
        this.trackedPlugins.put(name, dataFuture);
    }

    @Override
    public final void run() {
        while (!this.isInterrupted()) {
            Data data = new Data();
            this.update(data.minecraft);
            for (Map.Entry<String, Future<Map<String, Object>>> entry : this.trackedPlugins.entrySet()) {
                Map<String, Object> map;
                try {
                    map = entry.getValue().get();
                } catch (Exception ignored) { // If this happens, all is lost
                    continue;
                }
                Data.Plugin plugin = new Data.Plugin();
                plugin.name = entry.getKey();
                // TODO plugin.version =
                for (Map.Entry<String, Object> dataEntry : map.entrySet()) {
                    plugin.data.add(new Data.Plugin.PluginData(dataEntry.getKey(), dataEntry.getValue()));
                }
            }

            String currentState = this.toJson(data);

            Socket socket = null;
            DataOutputStream output = null;
            DataInputStream input = null;
            int delay = 60000; // Default wait 60 seconds before trying after a failure
            try {
                /*
                This looks like a great place to document the protocol for the moment

                Client -> Server
                    INT             Statik Version
                    UUID            Server's directory as a UUID

                Server -> Client
                    BYTE            Status: 00000000 for 'go ahead, send data', anything else for 'DO NOT SEND'
                    SHORT           Seconds to wait before next send
                    STRING (opt)    If status was 'DO NOT SEND', a String message that describes why

                Client -> Server
                    STRING          JSON data yay!
                 */

                // We get signal
                socket = new Socket("report.statik.io", 33333);
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());

                // Send the statik version and the server dir hash
                output.writeInt(STATIK_VERSION);
                output.writeLong(this.uuid.getMostSignificantBits());
                output.writeLong(this.uuid.getLeastSignificantBits());
                // Ensure it's actually sent before continuing
                output.flush();

                // Get le boolean
                boolean ready = input.readBoolean();
                // Get how long to wait before next attempt, in seconds
                delay = input.readUnsignedShort() * 1000;
                if (!ready) {
                    // If not ready, tell why
                    String error = input.readUTF();
                    // TODO output this message
                } else {
                    // Send it!
                    output.writeUTF(currentState);
                    output.flush();
                }
            } catch (Exception e) {
                // TODO log the fail would be a good idea
            } finally {
                this.close(output);
                this.close(input);
                this.close(socket);
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable ignored) {
            }
        }
    }

    protected abstract String toJson(Object object);

    protected abstract void update(Data.Minecraft data);
}
