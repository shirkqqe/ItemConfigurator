package ru.shirk.itemconfigurator.configurator.items;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import ru.shirk.itemconfigurator.ItemConfigurator;
import ru.shirk.itemconfigurator.configurator.CException;
import ru.shirk.itemconfigurator.configurator.Serializable;
import ru.shirk.itemconfigurator.configurator.items.options.BaseOption;
import ru.shirk.itemconfigurator.configurator.items.options.CustomOption;
import ru.shirk.itemconfigurator.configurator.items.potions.CCustomEffect;
import ru.shirk.itemconfigurator.configurator.items.potions.CPotionMeta;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode @ToString
public class CItem implements Serializable<CItem> {

    // Item Base
    private final @NonNull Material material;
    private final int amount;
    private final @NonNull Map<String, Integer> enchantments;

    // Item Meta
    private final boolean unbreakable;
    private final @Nullable String displayName;
    private final @Nullable List<String> lore;
    private final @NonNull List<ItemFlag> flags;

    // Options
    private final @NonNull List<BaseOption<?>> baseOptions;
    private final @NonNull List<CustomOption<?>> customOptions;

    // Potion
    private final @Nullable CPotionMeta potionMeta;

    public CItem(@NonNull Material material, int amount, @Nullable Map<String, Integer> enchantments, boolean unbreakable,
                 @Nullable String displayName, @Nullable List<String> lore,
                 @NonNull List<ItemFlag> flags, @Nullable CPotionMeta potionMeta,
                 @Nullable List<BaseOption<?>> baseOptions, @Nullable List<CustomOption<?>> customOptions) {
        this.material = material;
        this.amount = amount;
        this.enchantments = enchantments == null ? new HashMap<>() : enchantments;
        this.unbreakable = unbreakable;
        this.displayName = displayName;
        this.lore = lore;
        this.flags = flags;
        this.potionMeta = potionMeta;
        this.baseOptions = baseOptions == null ? new ArrayList<>() : baseOptions;
        this.customOptions = customOptions == null ? new ArrayList<>() : customOptions;
    }

    public CItem() {
        this.material = Material.AIR;
        this.amount = 0;
        this.enchantments = new HashMap<>();
        this.unbreakable = false;
        this.displayName = null;
        this.lore = null;
        this.flags = new ArrayList<>();
        this.potionMeta = null;
        this.baseOptions = new ArrayList<>();
        this.customOptions = new ArrayList<>();
    }

    public @NonNull ItemStack getAsItemStack() {
        final ItemStack itemStack = new ItemStack(material, amount);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setUnbreakable(unbreakable);
        if (displayName != null) {
            itemMeta.setDisplayName(displayName);
        }
        if (lore != null) {
            itemMeta.setLore(lore);

        }
        itemMeta.addItemFlags(flags.toArray(new ItemFlag[0]));

        for (BaseOption<?> baseOption : baseOptions) {
            try {
                baseOption.applyTo(itemMeta.getPersistentDataContainer());
            } catch (CException ignored) {
            }
        }

        if (potionMeta != null) {
            final PotionMeta pm = (PotionMeta) itemMeta;
            if (potionMeta.getBase() != null) {
                pm.setBasePotionData(new PotionData(potionMeta.getBase()));
            }
            if (potionMeta.getCustomEffects() != null) {
                for (CCustomEffect customEffect : potionMeta.getCustomEffects()) {
                    final PotionEffectType effectType = PotionEffectType.getByName(customEffect.getEffect());
                    if (effectType == null) continue;
                    pm.addCustomEffect(new PotionEffect(effectType, customEffect.getDuration(),
                            customEffect.getAmplifier()), true);
                }
            }
            if (potionMeta.getColor() != null) {
                pm.setColor(potionMeta.getColor());
            }
        }

        itemStack.setItemMeta(itemMeta);

        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            final Enchantment enchantment = Enchantment.getByName(entry.getKey());
            if (enchantment == null) {
                continue;
            }
            itemStack.addEnchantment(enchantment, entry.getValue());
            itemStack.addUnsafeEnchantment(enchantment, entry.getValue());
        }

        return itemStack;
    }

    public static @NonNull CItem parseCItem(@NonNull ItemStack itemStack) throws CException {
        final ItemMeta itemMeta = itemStack.getItemMeta();

        final Map<String, Integer> enchantments = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            enchantments.put(entry.getKey().getName(), entry.getValue());
        }

        if (itemMeta != null) {
            final List<BaseOption<?>> baseOptions = new ArrayList<>();
            final PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            for (NamespacedKey key : itemMeta.getPersistentDataContainer().getKeys()) {
                if (container.has(key, PersistentDataType.STRING)) {
                    baseOptions.add(new BaseOption<>(key.getKey(), Objects.requireNonNull(container
                            .get(key, PersistentDataType.STRING))));
                } else if (container.has(key, PersistentDataType.INTEGER)) {
                    baseOptions.add(new BaseOption<>(key.getKey(), Objects.requireNonNull(container
                            .get(key, PersistentDataType.INTEGER))));
                } else if (container.has(key, PersistentDataType.SHORT)) {
                    baseOptions.add(new BaseOption<>(key.getKey(), Objects.requireNonNull(container
                            .get(key, PersistentDataType.SHORT))));
                } else if (container.has(key, PersistentDataType.LONG)) {
                    baseOptions.add(new BaseOption<>(key.getKey(), Objects.requireNonNull(container
                            .get(key, PersistentDataType.LONG))));
                } else if (container.has(key, PersistentDataType.DOUBLE)) {
                    baseOptions.add(new BaseOption<>(key.getKey(), Objects.requireNonNull(container
                            .get(key, PersistentDataType.DOUBLE))));
                } else if (container.has(key, PersistentDataType.BYTE)) {
                    baseOptions.add(new BaseOption<>(key.getKey(), Objects.requireNonNull(container
                            .get(key, PersistentDataType.BYTE))));
                } else if (container.has(key, PersistentDataType.FLOAT)) {
                    baseOptions.add(new BaseOption<>(key.getKey(), Objects.requireNonNull(container
                            .get(key, PersistentDataType.FLOAT))));
                }
            }
            return new CItem(itemStack.getType(), itemStack.getAmount(), enchantments, itemMeta.isUnbreakable(), itemMeta.getDisplayName(),
                    itemMeta.getLore(), itemMeta.getItemFlags().stream().toList(), itemMeta instanceof PotionMeta potionMeta
                    ? CPotionMeta.parseCPotionMeta(potionMeta) : null, baseOptions, null);
        }

        return new CItem(itemStack.getType(), itemStack.getAmount(), enchantments, false, null,
                null, new ArrayList<>(), null, null, null);
    }

    @Override
    public void write(@NonNull ConfigurationSection section) throws CException {
        section.set("material", material.name());
        section.set("amount", amount);
        section.set("enchantments", enchantments);

        final ConfigurationSection metaSection = section.createSection("item-meta");
        if (displayName != null) {
            metaSection.set("display-name", displayName);
        }
        if (lore != null) {
            metaSection.set("lore", lore);
        }
        metaSection.set("flags", flags.isEmpty() ? new ArrayList<>(List.of())
                : new ArrayList<>(flags.stream().map(ItemFlag::name).toList()));
        metaSection.set("unbreakable", unbreakable);

        final ConfigurationSection baseOptionsSection = section.createSection("base-options");
        for (BaseOption<?> baseOption : baseOptions) {
            baseOption.write(baseOptionsSection);
        }

        final ConfigurationSection customOptionsSection = section.createSection("custom-options");
        for (CustomOption<?> customOption : customOptions) {
            customOption.write(customOptionsSection);
        }

        if (potionMeta == null) {
            return;
        }
        potionMeta.write(section.createSection("potion-meta"));
    }

    @Override
    public @NonNull CItem load(@NonNull ConfigurationSection section) throws CException {
        final String material = section.getString("material");
        final int amount = section.getInt("amount");
        if (material == null) {
            throw new CException("CItem cannot be loaded because 'material' is null");
        }

        final Map<String, Integer> enchantments = new HashMap<>();
        final ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
        if (enchantmentsSection != null) {
            for (String key : enchantmentsSection.getKeys(false)) {
                if (key == null) continue;
                int value = enchantmentsSection.getInt(key);
                enchantments.put(key, value);
            }
        }

        String displayName = null;
        List<String> lore = null;
        List<ItemFlag> itemFlags = null;
        CPotionMeta potionMeta = null;
        List<BaseOption<?>> baseOptions = null;
        List<CustomOption<?>> customOptions = null;
        boolean unbreakable = false;

        final ConfigurationSection metaSection = section.getConfigurationSection("item-meta");
        final ConfigurationSection potionMetaSection = section.getConfigurationSection("potion-meta");
        final ConfigurationSection baseOptionsSection = section.getConfigurationSection("base-options");
        final ConfigurationSection customOptionsSection = section.getConfigurationSection("custom-options");

        if (metaSection != null) {
            displayName = metaSection.getString("display-name");
            lore = metaSection.getStringList("lore");
            itemFlags = metaSection.getStringList("flags").stream().map(ItemFlag::valueOf)
                    .collect(Collectors.toList());
            unbreakable = metaSection.getBoolean("unbreakable");
        }

        if (potionMetaSection != null) {
            potionMeta = new CPotionMeta().load(potionMetaSection);
        }

        if (baseOptionsSection != null) {
            baseOptions = new ArrayList<>();
            for (String key : baseOptionsSection.getKeys(false)) {
                if (key == null) continue;
                Object value = baseOptionsSection.get(key);
                if (value == null) continue;
                baseOptions.add(new BaseOption<>(key, value));
            }
        }

        if (customOptionsSection != null) {
            customOptions = new ArrayList<>();
            for (String key : customOptionsSection.getKeys(false)) {
                if (key == null) continue;
                Object value = customOptionsSection.get(key);
                if (value == null) continue;
                customOptions.add(new CustomOption<>(key, value));
            }
        }

        final Material mat = Material.getMaterial(material);
        if (mat == null) {
            throw new CException("CItem cannot be loaded because 'material' is null");
        }

        return new CItem(mat, amount, enchantments, unbreakable, displayName, lore, itemFlags == null ? new ArrayList<>() : itemFlags,
                potionMeta, baseOptions, customOptions);
    }
}
