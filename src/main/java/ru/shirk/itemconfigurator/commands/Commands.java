package ru.shirk.itemconfigurator.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.shirk.itemconfigurator.ItemConfigurator;
import ru.shirk.itemconfigurator.config.Configuration;
import ru.shirk.itemconfigurator.configurator.CException;
import ru.shirk.itemconfigurator.configurator.Configurator;
import ru.shirk.itemconfigurator.configurator.ConfiguratorAPI;
import ru.shirk.itemconfigurator.configurator.items.CItem;
import ru.shirk.itemconfigurator.inventory.TestInv;

import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    private final Configurator configurator = new ConfiguratorAPI();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final Configuration configuration = ItemConfigurator.getConfigurationManager().getConfig("test.yml");
        switch (command.getName().toLowerCase()) {
            case "itest" -> {
                if (!(sender instanceof Player player)) return true;
                final ItemStack itemStack = player.getInventory().getItemInMainHand();
                final ConfigurationSection section = configuration.getFile().getConfigurationSection("items.item1");
                if (section == null) {
                    sender.sendMessage("section is null");
                    return true;
                }
                try {
                    configurator.writeItemToConfiguration(itemStack, section);
                    configuration.save();
                } catch (CException e) {
                    sender.sendMessage(e.toString());
                }
            }
            case "invtest" -> {
                if (!(sender instanceof Player player)) return true;
                final Inventory inventory = Bukkit.createInventory(player, 54, "Test inventory");
                final ConfigurationSection section = configuration.getFile().getConfigurationSection("items");
                if (section == null) {
                    sender.sendMessage("section is null");
                    return true;
                }
                try {
                    for (CItem item : configurator.getItemsFromConfiguration(section)) {
                        inventory.addItem(item.getAsItemStack());
                        player.sendMessage(item.getCustomOptions().toString());
                    }
                    player.openInventory(inventory);
                } catch (CException e) {
                    sender.sendMessage(e.toString());
                }
            }
            case "testinv" -> {
                if (!(sender instanceof Player player)) return true;
                new TestInv(player, 54, "Test", ItemConfigurator.getInstance());
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return List.of();
    }
}
