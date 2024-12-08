package ru.shirk.itemconfigurator.configurator.inventory;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import ru.shirk.itemconfigurator.ItemConfigurator;
import ru.shirk.itemconfigurator.configurator.CException;
import ru.shirk.itemconfigurator.configurator.Serializable;
import ru.shirk.itemconfigurator.configurator.items.CItem;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CInventory implements Serializable<CInventory> {

    private final @NonNull String key;
    private final @NonNull String name;
    private final int size;
    private final @NonNull Map<Integer, CItem> content;

    public CInventory(@NonNull String key, @NonNull String name, int size, @NonNull Map<Integer, CItem> content) {
        this.key = key;
        this.name = name;
        this.size = size;
        this.content = content;
    }

    public CInventory() {
        this.size = 0;
        this.key = "";
        this.name = "";
        this.content = new HashMap<>();
    }

    public @NonNull Inventory getAsBukkitInventory() {
        final Inventory inventory = Bukkit.createInventory(null, size, name);
        for (Map.Entry<Integer, CItem> entry : content.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getAsItemStack());
        }
        return inventory;
    }

    @Override
    public void write(@NonNull ConfigurationSection section) throws CException {
        if (name.isEmpty() || key.isEmpty()) {
            throw new CException("Cannot invoke 'CInventory.write()' because 'CInventory.key' or 'CInventory.name' is empty.");
        }

        final ConfigurationSection invSection = section.createSection(key);
        invSection.set("name", name);
        invSection.set("size", size);

        final ConfigurationSection contentSection = invSection.createSection("content");
        for (Map.Entry<Integer, CItem> entry : content.entrySet()) {
            final ConfigurationSection currentSection = contentSection.createSection(String.valueOf(entry.getKey()));
            entry.getValue().write(currentSection);
        }
    }

    @Override
    public @NonNull CInventory load(@NonNull ConfigurationSection section) throws CException {
        final String name = section.getString("name");
        final int size = section.getInt("size");
        if (name == null) {
            throw new CException("CInventory cannot be loaded because 'name' is null");
        }

        final ConfigurationSection contentSection = section.getConfigurationSection("content");
        if (contentSection == null) {
            throw new CException("CInventory cannot be loaded because section 'content' is null");
        }

        final Map<Integer, CItem> content = new HashMap<>();
        for (String key : contentSection.getKeys(false)) {
            if (key == null) continue;
            final ConfigurationSection currentSection = contentSection.getConfigurationSection(key);
            if (currentSection == null) continue;
            try {
                content.put(Integer.parseInt(key), new CItem().load(currentSection));
            } catch (NumberFormatException ignored) {
            }
        }

        return new CInventory(section.getName(), name, size, content);
    }
}
