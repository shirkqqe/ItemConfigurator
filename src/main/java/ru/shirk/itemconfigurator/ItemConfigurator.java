package ru.shirk.itemconfigurator;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.shirk.itemconfigurator.commands.Commands;
import ru.shirk.itemconfigurator.config.ConfigurationManager;

import java.io.File;

public final class ItemConfigurator extends JavaPlugin {

    @Getter
    private static ItemConfigurator instance;
    @Getter
    private static final ConfigurationManager configurationManager = new ConfigurationManager();

    @Override
    public void onEnable() {
        instance = this;
        loadConfigs();
        this.getCommand("itest").setExecutor(new Commands());
        this.getCommand("invtest").setExecutor(new Commands());
        this.getCommand("testinv").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void loadConfigs() {
        try {
            if (!(new File(getDataFolder(), "test.yml")).exists()) {
                configurationManager.createFile("test.yml");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
