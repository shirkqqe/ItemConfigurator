package ru.shirk.itemconfiguratorapi;

import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

public interface Serializable<T> {
    /**
     * @param section Configuration section for writing.
     * @throws CException If writing fails.
     */
    void write(@NonNull ConfigurationSection section) throws CException;

    /**
     * @param section Configuration section from which you need to get information
     * @return {@code T Object}
     * @throws CException If reading failed or some important section was not found.
     */
    @NonNull T load(@NonNull ConfigurationSection section) throws CException;
}
