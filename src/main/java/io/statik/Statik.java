package io.statik;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.UUID;

public class Statik {

    private static final int ONE_GIGABYTE = 1024 * 1024 * 1024;
    private static final String STATIK_VERSION = "1.0-SNAPSHOT";

    private final Plugin plugin;
    private final boolean enabled;
    private final boolean debug;
    private final UUID uuid;

    public Statik(Plugin plugin) {
        this.plugin = plugin;

        // Configuration
        final File configFolder = new File(plugin.getDataFolder().getParentFile(), "Statik");

        if (!configFolder.exists()) {
            configFolder.mkdir();
            plugin.getLogger().info("Successfully created Statik folder");
        }

        final File configFile = new File(configFolder, "config.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                config.set("opt-out", false);
                config.set("debug", false);
                config.set("server-id", UUID.randomUUID().toString());

                config.save(configFile);
                plugin.getLogger().info("Successfully created config file");
            } catch (Exception ignored) {
            }
        }

        final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        this.debug = config.getBoolean("debug");
        this.enabled = !config.getBoolean("opt-out");
        final String uuidString = config.getString("server-id");
        if (uuidString.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
            this.uuid = UUID.fromString(uuidString);
        } else {
            this.uuid = UUID.randomUUID();
            config.set("server-id", this.uuid.toString());
            try {
                config.save(configFile);
            } catch (Exception ignored) {
            }
        }
    }

    public void start() {
        if (!(isEnabled())) {
            plugin.getLogger().info(collectData().toJSONString());
        }
    }

    public void postData() {
        //TODO: Post data to report server using POST and no outside dependancies
    }

    private JSONObject collectData() {
        JSONObject data = new JSONObject();

        //Plugin Name
        data.put("pluginName", plugin.getDescription().getName());
        //Plugin Version
        data.put("pluginVersion", plugin.getDescription().getVersion());

        //Statik Version
        data.put("statikVersion", STATIK_VERSION);

        //Java Version
        data.put("javaVersion", System.getProperty("java.version"));
        //Operating System
        data.put("systemOS", System.getProperty("os.name"));
        //System Architecture
        data.put("systemArch", System.getProperty("os.arch"));
        //System Cores
        data.put("systemCores", Runtime.getRuntime().availableProcessors());
        //System Memory
        double mem = (double) Runtime.getRuntime().maxMemory() / ONE_GIGABYTE;
        String memory = null;
        if (mem < 1) {
            memory = "< 1";
        } else {
            memory = mem + "";
        }
        data.put("systemMemory", memory);

        //Server GUID
        data.put("serverUUID", getUUID());
        //Server Mod
        data.put("serverMod", Bukkit.getVersion().split("-")[1]);
        //Server Mod Version
        data.put("serverMCVersion", Bukkit.getVersion().split("MC: ")[1].replace(")", ""));
        //Get Auth Mode of Server
        data.put("serverOnline", Bukkit.getServer().getOnlineMode() + "");
        //Player Count
        data.put("playerCount", Bukkit.getOnlinePlayers().length);

        return data;
    }

    /**
     * Checks if the server owner has opted out of stat collection.
     *
     * @return false if the server owner has opted out
     */
    private boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Checks if the server owner has enabled debugging.
     *
     * @return true if debugging is enabled
     */
    private boolean isDebugEnabled() {
        return this.debug;
    }

    /**
     * Retrieves the server's unique identifier.
     *
     * @return unique identifier
     */
    private UUID getUUID() {
        return this.uuid;
    }

}
