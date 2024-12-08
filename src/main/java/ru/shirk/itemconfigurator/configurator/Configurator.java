package ru.shirk.itemconfigurator.configurator;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.shirk.itemconfigurator.configurator.inventory.CInventory;
import ru.shirk.itemconfigurator.configurator.items.CItem;

import java.util.List;

public interface Configurator {
    /**
     * @param section Configuration section where you need to write items.
     * @param itemStacks Items that need to be saved to the configuration.
     * @throws CException If writing fails.
     */
    void writeItemsToConfiguration(@NonNull ConfigurationSection section, @NonNull ItemStack[] itemStacks) throws CException;

    /**
     * @param itemStack Item to be written to the configuration.
     * @param section Configuration section where you need to write item.
     * @throws CException If writing fails.
     */
    void writeItemToConfiguration(@NonNull ItemStack itemStack, @NonNull ConfigurationSection section) throws CException;

    /**
     * @param inventory Inventory that needs to be saved to the configuration.
     * @param key Key for saving inventory.
     * @param title Inventory title
     * @param section Configuration section where you need to write item.
     * @throws CException If writing fails.
     */
    void writeInventoryToConfiguration(@NonNull Inventory inventory, @NonNull String key, @NonNull String title, @NonNull ConfigurationSection section) throws CException;

    /**
     * @param section Configuration section from which to obtain items.
     * @return Resulting list of items.
     * @throws CException If reading failed or some important section was not found.
     */
    @NonNull
    List<CItem> getItemsFromConfiguration(@NonNull ConfigurationSection section) throws CException;

    /**
     * @param section Configuration section from which to obtain item.
     * @param path Path to get the item.
     * @return CItem object.
     * @throws CException If reading failed or some important section was not found.
     */
    @NonNull
    CItem getItemFromConfiguration(@NonNull ConfigurationSection section, @NonNull String path) throws CException;

    /**
     * @param section Configuration section from which to get the inventory.
     * @return CInventory object.
     * @throws CException If reading failed or some important section was not found.
     */
    @NonNull
    CInventory getInventoryFromConfiguration(@NonNull ConfigurationSection section) throws CException;

    /**
     * @param section Configuration section from which you can get a list of inventories.
     * @return Resulting list of inventories.
     * @throws CException If reading failed or some important section was not found.
     */
    @NonNull
    List<CInventory> getInventoryListFromConfiguration(@NonNull ConfigurationSection section) throws CException;
}
