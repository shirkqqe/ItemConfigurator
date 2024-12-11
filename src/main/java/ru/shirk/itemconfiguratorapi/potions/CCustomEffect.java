package ru.shirk.itemconfiguratorapi.potions;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;
import ru.shirk.itemconfiguratorapi.CException;
import ru.shirk.itemconfiguratorapi.Serializable;

@Getter
public class CCustomEffect implements Serializable<CCustomEffect> {

    private final @NonNull String effect;
    private final int duration;
    private final int amplifier;

    public CCustomEffect(@NonNull String effect, int duration, int amplifier) throws CException {
        final PotionEffectType effectType = PotionEffectType.getByName(effect);
        if (effectType == null) {
            throw new CException("CCustomEffect cannot be created because effect is null");
        }
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void write(@NonNull ConfigurationSection section) throws CException {
        if (effect.isEmpty() || duration == 0) {
            throw new CException("Writing an empty effect is not allowed");
        }
        section.set(String.format("%s_%d", effect, amplifier), duration);
    }

    @Override
    public @NonNull CCustomEffect load(@NonNull ConfigurationSection section) throws CException {
        throw new CException("CCustomEffect cannot be loaded!");
    }
}
