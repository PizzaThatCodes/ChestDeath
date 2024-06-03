package me.pizzalover.chestdeath.utils;

import jakarta.xml.bind.DatatypeConverter;
import me.pizzalover.chestdeath.Main;
import me.pizzalover.chestdeath.database.model.chestDataModel;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class utils {

    /**
     * Translate a string with color codes to have colored text (hex supported)
     * @param message returns a colored string using & (hex supported)
     * @return
     */
    public static String translate(String message) {
        // TODO: check if server version is 1.16 or above
        try {
            Method method = Class.forName("net.md_5.bungee.api.ChatColor").getMethod("of", String.class);
            message = message.replaceAll("&#",  "#");
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        } catch (Exception e) {
            // Server version is below 1.16
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Really janky setup to run a console command without it being logged into console
     * @param command the command to run
     * @param world the world to run the command in
     */
    public static void runConsoleCommand(String command, World world) {
        boolean commandFeedback = world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK);
        boolean commandBlock = world.getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT);
        world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        Entity entity = world.spawnEntity(new Location(world, 0, -320, 0), EntityType.COMMAND_BLOCK_MINECART);
        Main.getInstance().getServer().dispatchCommand(entity, command);
        world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, commandFeedback);
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, commandBlock);
        entity.remove();
    }

    /**
     * Encode an item to base64
     * @param itemStack the itemStack
     * @return the base64 string
     */
    public static String encodeItem(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemStack);
        return DatatypeConverter.printBase64Binary(config.saveToString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decode an item from base64
     * @param string base64 string
     * @return the itemStack
     */
    public static ItemStack decodeItem(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(DatatypeConverter.parseBase64Binary(string), StandardCharsets.UTF_8));
            return config.getItemStack("i", null);
        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Error decoding item from base64");
            return null;
        }
    }

    /**
     * Create a chest item
     * @param chestData the chest data
     * @return the chest item
     */
    public static ItemStack createChestItem(chestDataModel chestData) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(chestData.getUuid());
        ItemStack chest = new ItemStack(Material.CHEST, 1);
        ItemMeta chestMeta = chest.getItemMeta();
        chestMeta.setDisplayName(ChatColor.GOLD + player.getName() + "'s Death Chest");

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "death_chest_id");
        chestMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, chestData.getId());

        chest.setItemMeta(chestMeta);
        return chest;
    }

    /**
     * Generate a random code
     * @return the random code
     */
    public static String generateCode() {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return generatedString;
    }

}
