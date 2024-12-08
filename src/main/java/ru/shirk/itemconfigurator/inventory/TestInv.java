package ru.shirk.itemconfigurator.inventory;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import ru.shirk.itemconfigurator.ItemConfigurator;
import ru.shirk.itemconfigurator.config.Configuration;
import ru.shirk.itemconfigurator.configurator.CException;
import ru.shirk.itemconfigurator.configurator.Configurator;
import ru.shirk.itemconfigurator.configurator.ConfiguratorAPI;

public class TestInv implements Listener {

    private Inventory inventory;
    private final Configurator configurator = new ConfiguratorAPI();

    public TestInv(@NonNull Player player, int size, @NonNull String title, @NonNull JavaPlugin plugin) {
        this.inventory = Bukkit.createInventory(player, size, Component.text(title));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        final Configuration configuration = ItemConfigurator.getConfigurationManager().getConfig("test.yml");
        final ConfigurationSection section = configuration.getFile().getConfigurationSection("inventory.testinv");
        if (section == null) {
            player.sendMessage("section is null");
            player.openInventory(inventory);
            return;
        }
        try {
            this.inventory = configurator.getInventoryFromConfiguration(section).getAsBukkitInventory();
        } catch (CException e) {
            player.sendMessage(e.toString());
        }
        player.openInventory(inventory);
    }

    @EventHandler
    private void onCloseInventory(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (event.getInventory() != inventory) return;
        final Configuration configuration = ItemConfigurator.getConfigurationManager().getConfig("test.yml");
        final ConfigurationSection section = configuration.getFile().getConfigurationSection("inventory");
        if (section == null) {
            player.sendMessage("section is null");
            return;
        }
        try {
            configurator.writeInventoryToConfiguration(inventory, "testinv", "Test", section);
            configuration.save();
        } catch (CException e) {
            player.sendMessage(e.toString());
        }
    }
}
