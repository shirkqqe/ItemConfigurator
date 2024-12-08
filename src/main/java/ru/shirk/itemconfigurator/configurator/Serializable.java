package ru.shirk.itemconfigurator.configurator;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

public interface Serializable<T> {
    void write(@NonNull ConfigurationSection section) throws CException;

    @NonNull T load(@NonNull ConfigurationSection section) throws CException;
}
