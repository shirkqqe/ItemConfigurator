package ru.shirk.itemconfiguratorapi.options;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.shirk.itemconfiguratorapi.CException;
import ru.shirk.itemconfiguratorapi.Serializable;

public class BaseOption<Z> implements Serializable<BaseOption<Z>> {

    @Getter
    private final @NonNull String key;
    private final @NonNull Z option;

    public BaseOption(@NonNull String key, @NonNull Z option) {
        this.key = key;
        this.option = option;
    }

    public @NonNull Z get() {
        return this.option;
    }

    public void applyTo(@NonNull PersistentDataContainer persistentDataContainer) throws CException {
        if (option instanceof String) {
            persistentDataContainer.set(
                    NamespacedKey.minecraft(key),
                    PersistentDataType.STRING,
                    (String) option
            );
        } else if (option instanceof Integer) {
            persistentDataContainer.set(
                    NamespacedKey.minecraft(key),
                    PersistentDataType.INTEGER,
                    (Integer) option
            );
        } else if (option instanceof Double) {
            persistentDataContainer.set(
                    NamespacedKey.minecraft(key),
                    PersistentDataType.DOUBLE,
                    (Double) option
            );
        } else if (option instanceof Short) {
            persistentDataContainer.set(
                    NamespacedKey.minecraft(key),
                    PersistentDataType.SHORT,
                    (Short) option
            );
        } else if (option instanceof Byte) {
            persistentDataContainer.set(
                    NamespacedKey.minecraft(key),
                    PersistentDataType.BYTE,
                    (Byte) option
            );
        } else if (option instanceof Float) {
            persistentDataContainer.set(
                    NamespacedKey.minecraft(key),
                    PersistentDataType.FLOAT,
                    (Float) option
            );
        } else if (option instanceof Long) {
            persistentDataContainer.set(
                    NamespacedKey.minecraft(key),
                    PersistentDataType.LONG,
                    (Long) option
            );
        } else {
            throw new CException("Failed to apply option to PDK.");
        }
    }

    @Override
    public void write(@NonNull ConfigurationSection section) throws CException {
        section.set(key, option);
    }

    @Override
    public @NonNull BaseOption<Z> load(@NonNull ConfigurationSection section) throws CException {
        throw new CException("BaseOption cannot be loaded!");
    }
}
