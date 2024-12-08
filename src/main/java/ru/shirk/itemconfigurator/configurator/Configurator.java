package ru.shirk.itemconfigurator.configurator;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.shirk.itemconfigurator.configurator.inventory.CInventory;
import ru.shirk.itemconfigurator.configurator.items.CItem;

import java.util.List;

public interface Configurator {
    void writeItemsToConfiguration(@NonNull ConfigurationSection section, @NonNull ItemStack[] itemStacks) throws CException;

    void writeItemToConfiguration(@NonNull ItemStack itemStack, @NonNull ConfigurationSection section) throws CException;

    void writeInventoryToConfiguration(@NonNull Inventory inventory, @NonNull String key, @NonNull String title, @NonNull ConfigurationSection section) throws CException;

    @NonNull
    List<CItem> getItemsFromConfiguration(@NonNull ConfigurationSection section) throws CException;

    @NonNull
    CItem getItemFromConfiguration(@NonNull ConfigurationSection section, @NonNull String path) throws CException;

    @NonNull
    CInventory getInventoryFromConfiguration(@NonNull ConfigurationSection section) throws CException;

    @NonNull
    List<CInventory> getInventoryListFromConfiguration(@NonNull ConfigurationSection section) throws CException;
}
