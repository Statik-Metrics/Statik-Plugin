package io.statik.demo;

import io.statik.Statik;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getLogger().info("Enabling Statik Test Plugin");
        new Statik(this).start();

        Bukkit.getServer().getOnlinePlayers();
        getServer().getLogger().info("Statik Test Plugin Enabled");
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("Disabling Statik Test Plugin");
        getServer().getLogger().info("Statik Test Plugin Disabled");
    }
}
