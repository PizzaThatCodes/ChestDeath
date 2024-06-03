package me.pizzalover.chestdeath.listener;

import me.pizzalover.chestdeath.Main;
import me.pizzalover.chestdeath.database.model.chestDataModel;
import me.pizzalover.chestdeath.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class onRedeem implements Listener {

    @EventHandler
    public void onChestRedeem(PlayerInteractEvent event) {

        if(event.getHand() != EquipmentSlot.HAND) return;
        if(event.getItem() == null) return;
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        PersistentDataContainer container = Objects.requireNonNull(event.getItem().getItemMeta()).getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getInstance(), "death_chest_id");

        if(!container.has(key, PersistentDataType.STRING)) return;

        String id = container.get(key, PersistentDataType.STRING);

        chestDataModel chestData = Main.getDatabase().findChestDataInformationByID(id);
        if(chestData == null) return;

        event.setCancelled(true);
        player.getInventory().remove(event.getItem());
        for(ItemStack item : chestData.getChestItems()) {
            if(item == null) continue;
            if(player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), item);
            } else {
                player.getInventory().addItem(item);
            }
        }

        player.sendMessage(
                utils.translate(
                        Objects.requireNonNull(Main.getMessageManager().getConfig().getString("redeem-message"))
                                .replace("%prefix%", Main.getPrefix())
                                .replace("%player%", Objects.requireNonNull(Bukkit.getOfflinePlayer(chestData.getUuid()).getName()))
                ));

        Main.getDatabase().deleteInformation(chestData);

    }

}
