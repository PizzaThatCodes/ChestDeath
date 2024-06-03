package me.pizzalover.chestdeath.listener;

import me.pizzalover.chestdeath.Main;
import me.pizzalover.chestdeath.database.model.chestDataModel;
import me.pizzalover.chestdeath.utils.utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class onDeath implements Listener {

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {

        Player player = event.getEntity();

        String code = utils.generateCode();
        while (Main.getDatabase().findChestDataInformationByID(code) != null) {
            code = utils.generateCode();
        }

        chestDataModel chestData = new chestDataModel(player.getUniqueId(), code, event.getDrops().toArray(new ItemStack[0]));

        Main.getDatabase().createInformation(chestData);
        event.getDrops().clear();
        event.getDrops().add(utils.createChestItem(chestData));

    }

}
