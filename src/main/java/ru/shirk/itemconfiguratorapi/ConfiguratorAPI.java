package ru.shirk.itemconfiguratorapi;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.shirk.itemconfiguratorapi.inventory.CInventory;
import ru.shirk.itemconfiguratorapi.items.CItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfiguratorAPI implements Configurator {
    @Override
    public void writeItemsToConfiguration(@NonNull ConfigurationSection section, @NonNull ItemStack[] itemStacks) throws CException {
        for (int i = 0; i < itemStacks.length; i++) {
            final ConfigurationSection currentSection = section.createSection(String.valueOf(i));
            final ItemStack itemStack = itemStacks[i];

            CItem.parseCItem(itemStack).write(currentSection);
        }
    }

    @Override
    public void writeItemToConfiguration(@NonNull ItemStack itemStack, @NonNull ConfigurationSection section) throws CException {
        CItem.parseCItem(itemStack).write(section);
    }

    @Override
    public void writeInventoryToConfiguration(@NonNull Inventory inventory, @NonNull String key, @NonNull String title,
                                              @NonNull ConfigurationSection section) throws CException {
        final Map<Integer, CItem> content = new HashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) continue;
            content.put(i, CItem.parseCItem(itemStack));
        }
        final CInventory cInventory = new CInventory(key, title, inventory.getSize(), content);
        cInventory.write(section);
    }

    @Override
    public @NonNull List<CItem> getItemsFromConfiguration(@NonNull ConfigurationSection section) throws CException {
        final List<CItem> items = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            if (key == null) continue;
            final ConfigurationSection currentSection = section.getConfigurationSection(key);
            if (currentSection == null) continue;
            items.add(new CItem().load(currentSection));
        }
        return items;
    }

    @Override
    public @NonNull CItem getItemFromConfiguration(@NonNull ConfigurationSection section, @NonNull String path) throws CException {
        final ConfigurationSection fromPath = section.getConfigurationSection(path);
        if (fromPath == null) {
            throw new CException("Cannot invoke 'CItem.load()' because section along path '" + path + "' is null");
        }
        return new CItem().load(fromPath);
    }

    @Override
    public @NonNull CInventory getInventoryFromConfiguration(@NonNull ConfigurationSection section) throws CException {
        return new CInventory().load(section);
    }

    @Override
    public @NonNull List<CInventory> getInventoryListFromConfiguration(@NonNull ConfigurationSection section) throws CException {
        final List<CInventory> inventories = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            if (key == null) continue;
            final ConfigurationSection invSection = section.getConfigurationSection(key);
            if (invSection == null) continue;
            inventories.add(new CInventory().load(invSection));
        }
        return inventories;
    }
}
