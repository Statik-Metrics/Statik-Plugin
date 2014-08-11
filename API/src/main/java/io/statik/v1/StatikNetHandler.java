package io.statik.v1;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Handles nets. Do not modify this class.
 */
abstract class StatikNetHandler extends Thread {
    public static final String NOTICE = "DO NOT MODIFY THIS CLASS - MODIFICATIONS MAY RESULT STATIK.IO DELISTING";

    protected class Data {
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

        protected class Plugin {
            protected class PluginData {
                private String name;
                private String value;

                PluginData(String name, String value) {
                    this.name = name;
                    this.value = value;
                }
            }

            List<PluginData> data = new LinkedList<PluginData>();
            String name;
            String version;
        }

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

    /**
     * Creates the net handler!
     *
     * @param serverHash UUID based on the server directory
     */
    StatikNetHandler(UUID serverHash) {
        this.uuid = serverHash;
    }

    @Override
    public final void run() {
        // TODO stuff
    }
}
