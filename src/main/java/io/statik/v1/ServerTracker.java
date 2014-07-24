package io.statik.v1;

import org.bukkit.Bukkit;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ServerTracker implements StatikTracker {

    private static final Pattern VERSION_PATTERN = Pattern.compile("\\(MC: ([^\\)]+)\\)");

    private final StatikImpl statik;
    private final Map<String, Object> lastMap;

    private String serverHash;

    public ServerTracker(StatikImpl statik) {
        this.statik = statik;
        this.lastMap = new HashMap<String, Object>();

        String pluginFolderPath = this.statik.getPlugins().iterator().next().getDataFolder().getParent();
        try {
            this.serverHash = new String(MessageDigest.getInstance("MD5").digest(pluginFolderPath.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException("Unable to hash, please report this");
        }
    }

    @Override
    public StatikDataMap getStatikData() {
        StatikDataMap result = new StatikDataMap();

        // Get all the dataz
        result.putInt("statikVersion", this.statik.getVersion());
        result.putString("javaVersion", System.getProperty("java.version"));
        result.putString("systemOS", System.getProperty("os.name"));
        result.putString("systemArch", System.getProperty("os.arch"));
        result.putInt("systemCores", Runtime.getRuntime().availableProcessors());
        result.putLong("systemMemory", Runtime.getRuntime().maxMemory());
        result.putString("serverHash", this.serverHash);
        result.putString("serverMod", Bukkit.getName());
        result.putBoolean("serverOnline", Bukkit.getOnlineMode());

        final String mcVersion;
        final Matcher versionMatcher = VERSION_PATTERN.matcher(Bukkit.getVersion());
        if (versionMatcher.find()) {
            mcVersion = versionMatcher.group(1);
        } else {
            mcVersion = "unknown";
        }
        result.putString("serverMCVersion", mcVersion);

        // Filter the result to prevent sending already known information
        StatikDataMap finalResult = result.getFilteredMap(this.lastMap);
        this.lastMap.putAll(result.getMap());
        return finalResult;
    }
}
