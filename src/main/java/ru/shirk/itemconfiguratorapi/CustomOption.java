package ru.shirk.itemconfiguratorapi;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

public class CustomOption<Z> implements Serializable<CustomOption<Z>> {

    @Getter
    private final @NonNull String key;
    private final @NonNull Z option;

    public CustomOption(@NonNull String key, @NonNull Z option) {
        this.key = key;
        this.option = option;
    }

    public @NonNull Z get() {
        return this.option;
    }

    @Override
    public void write(@NonNull ConfigurationSection section) throws CException {
        section.set(key, option);
    }

    @Override
    public @NonNull CustomOption<Z> load(@NonNull ConfigurationSection section) throws CException {
        throw new CException("CustomOption cannot be loaded!");
    }
}
