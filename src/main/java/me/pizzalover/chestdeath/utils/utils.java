package me.pizzalover.chestdeath.utils;

import jakarta.xml.bind.DatatypeConverter;
import me.pizzalover.chestdeath.Main;
import me.pizzalover.chestdeath.database.model.chestDataModel;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class utils {

    /**
     * Translate a string with color codes to have colored text (hex supported)
     * @param message returns a colored string using & (hex supported)
     */
    public static String translate(String message) {
        // TODO: check if server version is 1.16 or above
        try {
            Class.forName("net.md_5.bungee.api.ChatColor").getMethod("of", String.class);
            message = message.replaceAll("&#",  "#");
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, net.md_5.bungee.api.ChatColor.valueOf(color) + "");
                matcher = pattern.matcher(message);
            }
        } catch (Exception e) {
            // Server version is below 1.16
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
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
        assert chestMeta != null;
        chestMeta.setDisplayName(ChatColor.GOLD + player.getName() + "'s Death Chest");

        // Store the custom data in the lore
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "Death Chest ID: " + chestData.getId());
        chestMeta.setLore(lore);

        chest.setItemMeta(chestMeta);
        return chest;
    }

    /**
     * Get the Death Chest ID from a chest item
     * @param chest the chest item
     * @return the Death Chest ID
     */
    public static String getDeathChestId(ItemStack chest) {
        if (chest == null || chest.getType() != Material.CHEST) {
            return null;
        }

        ItemMeta chestMeta = chest.getItemMeta();
        if (chestMeta == null || !chestMeta.hasLore()) {
            return null;
        }

        List<String> lore = chestMeta.getLore();
        if (lore == null || lore.isEmpty()) {
            return null;
        }

        // Assuming the Death Chest ID is stored in the first line of the lore
        String idLine = lore.get(0);
        if (idLine.startsWith(ChatColor.DARK_GRAY + "Death Chest ID: ")) {
            return idLine.substring((ChatColor.DARK_GRAY + "Death Chest ID: ").length());
        }

        return null;
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

        return buffer.toString();
    }

}
