package me.pizzalover.chestdeath;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    /**
     * Get the instance of the main class
     * @return the instance of the main class
     */
    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;


        getLogger().info("ChestDeath has been enabled!");
    }

    @Override
    public void onDisable() {


        getLogger().info("ChestDeath has been disabled!");
    }
}
