package me.pizzalover.chestdeath;

import me.pizzalover.chestdeath.database.database;
import me.pizzalover.chestdeath.listener.onDeath;
import me.pizzalover.chestdeath.listener.onRedeem;
import me.pizzalover.chestdeath.utils.configManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static configManager messages;
    private static configManager config;
    private static database database;

    /**
     * Get the instance of the main class
     * @return the instance of the main class
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * Get the prefix of the plugin
     * @return the prefix of the plugin
     */
    public static String getPrefix() {
        return config.getConfig().getString("prefix");
    }

    /**
     * Get the messages configuration
     * @return the messages configuration
     */
    public static configManager getMessageManager() {
        return messages;
    }

    /**
     * Get the config configuration
     * @return the config configuration
     */
    public static configManager getConfigManager() {
        return config;
    }

    /**
     * Get the database
     * @return the database
     */
    public static database getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        instance = this;

        database = new database();

        if(!database.initializeDatabase()) {
            getLogger().severe("Failed to initialize database!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        messages = new configManager("messages.yml");
        config = new configManager("config.yml");

        if(!messages.getConfigFile().exists()) {
            saveResource("messages.yml", false);
        }

        if(!config.getConfigFile().exists()) {
            saveResource("config.yml", false);
        }

        messages.updateConfig(Arrays.asList());
        messages.saveConfig();
        messages.reloadConfig();

        config.updateConfig(Arrays.asList());
        config.saveConfig();
        config.reloadConfig();

        getServer().getPluginManager().registerEvents(new onDeath(), this);
        getServer().getPluginManager().registerEvents(new onRedeem(), this);

        getLogger().info("ChestDeath has been enabled!");
    }

    @Override
    public void onDisable() {


        getLogger().info("ChestDeath has been disabled!");
    }
}
