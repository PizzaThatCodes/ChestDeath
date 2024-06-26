package me.pizzalover.chestdeath.database.model;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class chestDataModel {

    UUID uuid;
    String id;
    ItemStack[] itemStacks;

    /**
     * Create a new chest data model
     * @param uuid the UUID of the player
     * @param id the ID of the chest
     * @param itemStacks the items of the chest
     */
    public chestDataModel(UUID uuid, String id, ItemStack[] itemStacks) {
        this.uuid = uuid;
        this.id = id;
        this.itemStacks = itemStacks;
    }

    /**
     * Get the UUID of the player
     * @return the UUID of the player
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get the ID of the chest
     * @return the ID of the chest
     */
    public String getId() {
        return id;
    }

    /**
     * Get the inventory of the chest
     * @return the inventory of the chest
     */
    public ItemStack[] getChestItems() {
        return itemStacks;
    }
}
