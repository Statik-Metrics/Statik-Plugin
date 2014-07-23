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
    public Map<String, Object> getStatikData() {
        Map<String, Object> result = new HashMap<String, Object>();

        //Statik Version
        if (!this.lastMap.containsKey("statikVersion")) {
            result.put("statikVersion", this.statik.getVersion());
        }

        //Java Version
        if (!this.lastMap.containsKey("javaVersion")) {
            result.put("javaVersion", System.getProperty("java.version"));
        }

        //Operating System
        if (!this.lastMap.containsKey("systemOS")) {
            result.put("systemOS", System.getProperty("os.name"));
        }

        //System Architecture
        if (!this.lastMap.containsKey("systemArch")) {
            result.put("systemArch", System.getProperty("os.arch"));
        }

        //System Cores
        if (!this.lastMap.containsKey("systemCores")) {
            result.put("systemCores", Runtime.getRuntime().availableProcessors());
        }

        //System Memory
        if (!this.lastMap.containsKey("systemMemory")) {
            result.put("systemMemory", Runtime.getRuntime().maxMemory());
        }

        //Server Hash
        result.put("serverHash", this.serverHash);

        //Server Mod
        if (!this.lastMap.containsKey("serverMod")) {
            result.put("serverMod", Bukkit.getName());
        }

        //Server Mod Version
        if (!this.lastMap.containsKey("serverMCVersion")) {
            final String mcVersion;
            final Matcher versionMatcher = VERSION_PATTERN.matcher(Bukkit.getVersion());
            if (versionMatcher.find()) {
                mcVersion = versionMatcher.group(1);
            } else {
                mcVersion = "unknown";
            }
            result.put("serverMCVersion", mcVersion);
        }

        //Get Auth Mode of Server
        result.put("serverOnline", Bukkit.getOnlineMode());

        this.lastMap.putAll(result);
        return result;
    }
}
