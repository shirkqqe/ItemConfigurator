package ru.shirk.itemconfiguratorapi;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;
import ru.shirk.itemconfiguratorapi.potions.CCustomEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CPotionMeta implements Serializable<CPotionMeta> {

    private final @Nullable PotionType base;
    private final @Nullable List<CCustomEffect> customEffects;
    private final @Nullable Color color;

    public CPotionMeta(@NonNull PotionType base, @NonNull List<CCustomEffect> customEffects, @NonNull Color color) {
        this.base = base;
        this.customEffects = customEffects;
        this.color = color;
    }

    public CPotionMeta() {
        this.base = null;
        this.customEffects = null;
        this.color = null;
    }

    public static @NonNull CPotionMeta parseCPotionMeta(@NonNull PotionMeta potionMeta) throws CException {
        final List<CCustomEffect> customEffects = new ArrayList<>();
        for (PotionEffect effect : potionMeta.getCustomEffects()) {
            customEffects.add(new CCustomEffect(effect.getType().getName(), effect.getDuration(), effect.getAmplifier()));
        }
        return new CPotionMeta(potionMeta.getBasePotionData().getType(), customEffects, potionMeta.getColor() == null ?
                Color.BLUE : potionMeta.getColor());
    }

    @Override
    public void write(@NonNull ConfigurationSection section) throws CException {
        if (base == null || customEffects == null || color == null) {
            throw new CException("CPotionMeta cannot be writied because one of arguments is null");
        }

        final ConfigurationSection potionMetaSection = section.createSection("potion-meta");
        potionMetaSection.set("base", base.name());
        potionMetaSection.set("color", String.format("%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue()));

        final ConfigurationSection customEffectsSection = potionMetaSection.createSection("custom-effects");
        for (CCustomEffect customEffect : customEffects) {
            customEffect.write(customEffectsSection);
        }
    }

    @Override
    public @NonNull CPotionMeta load(@NonNull ConfigurationSection section) throws CException {
        final String colorString = section.getString("color");
        if (colorString == null) {
            throw new CException("CPotionMeta cannot be loaded because 'color' is null");
        }
        try {
            final PotionType base = PotionType.valueOf(section.getString("base"));

            String[] split = colorString.split(",");
            int r = Integer.parseInt(split[0]);
            int g = Integer.parseInt(split[1]);
            int b = Integer.parseInt(split[2]);
            final Color color = Color.fromRGB(r, g, b);

            final ConfigurationSection customEffectsSection = section.getConfigurationSection("custom-effects");
            if (customEffectsSection == null) return new CPotionMeta(base, new ArrayList<>(), color);

            final List<CCustomEffect> customEffects = new ArrayList<>();
            for (String key : customEffectsSection.getKeys(false)) {
                final String[] sKey = key.split("_");
                customEffects.add(new CCustomEffect(sKey[0], Integer.parseInt(sKey[1]), customEffectsSection.getInt(key)));
            }
            return new CPotionMeta(base, customEffects, color);
        } catch (IllegalArgumentException e) {
            throw new CException("CPotionMeta cannot be loaded because color is invalid");
        }
    }
}
