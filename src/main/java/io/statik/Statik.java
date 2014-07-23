package io.statik;

import com.google.common.collect.ImmutableMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Statik {

    private static final int ONE_GIGABYTE = 1024 * 1024 * 1024;
    private static final String STATIK_VERSION = "1.0-SNAPSHOT";
    private static final Pattern VERSION_PATTERN = Pattern.compile("\\(MC: ([^\\)]+)\\)");

    private final Gson gson = new Gson();
    private final Plugin plugin;
    private final boolean enabled;
    private final boolean debug;
    private final UUID uuid;

    private final Map<String, Object> unchanging;

    public Statik(Plugin plugin) {
        this.plugin = plugin;

        // Configuration
        final File configFolder = new File(this.plugin.getDataFolder().getParentFile(), "Statik");

        if (!configFolder.exists()) {
            configFolder.mkdir();
            this.plugin.getLogger().info("Successfully created Statik folder");
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
                this.plugin.getLogger().info("Successfully created config file");
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

        // Set-once information
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        //Plugin Name
        builder.put("pluginName", this.plugin.getDescription().getName());
        //Plugin Version
        builder.put("pluginVersion", this.plugin.getDescription().getVersion());
        //Statik Version
        builder.put("statikVersion", STATIK_VERSION);
        //Java Version
        builder.put("javaVersion", System.getProperty("java.version"));
        //Operating System
        builder.put("systemOS", System.getProperty("os.name"));
        //System Architecture
        builder.put("systemArch", System.getProperty("os.arch"));
        //System Cores
        builder.put("systemCores", Runtime.getRuntime().availableProcessors());
        //System Memory
        builder.put("systemMemory", Runtime.getRuntime().maxMemory());
        //Server GUID
        builder.put("serverUUID", this.uuid);
        //Server Mod
        builder.put("serverMod", this.plugin.getServer().getName());
        //Server Mod Version
        final String mcVersion;
        final Matcher versionMatcher = VERSION_PATTERN.matcher(this.plugin.getServer().getVersion());
        if (versionMatcher.find()) {
            mcVersion = versionMatcher.group(1);
        } else {
            mcVersion = "unknown";
        }
        builder.put("serverMCVersion", mcVersion);
        //Get Auth Mode of Server
        builder.put("serverOnline", this.plugin.getServer().getOnlineMode());

        this.unchanging = builder.build();
    }

    public void start() {
        if (this.isEnabled()) {
            this.plugin.getLogger().info(this.gson.toJson(this.collectData()));
        }
    }

    public void postData() {
        //TODO: Post data to report server using POST and no outside dependancies
    }

    private Map<String, Object> collectData() {
        final Map<String, Object> data = new HashMap<String, Object>();

        data.putAll(this.unchanging);

        //Player Count
        data.put("playerCount", this.plugin.getServer().getOnlinePlayers().length);

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
