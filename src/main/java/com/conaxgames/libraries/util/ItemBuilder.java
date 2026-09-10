package com.conaxgames.libraries.util;

import com.conaxgames.libraries.message.CC;
import com.conaxgames.libraries.message.FormatUtil;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XItemFlag;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Fluent, cross-version {@link ItemStack} builder backed by XSeries.
 *
 * <pre>{@code
 * ItemStack item = ItemBuilder.of(XMaterial.DIAMOND_SWORD)
 *         .name("&bExcalibur")
 *         .lore("&7Legendary blade")
 *         .enchant(XEnchantment.SHARPNESS, 5)
 *         .unbreakable(true)
 *         .build();
 * }</pre>
 */
public final class ItemBuilder {

    private final ItemStack itemStack;

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static ItemBuilder of(XMaterial material) {
        return of(material, 1);
    }

    public static ItemBuilder of(XMaterial material, int amount) {
        ItemStack parsed = material.parseItem();
        if (parsed == null) {
            throw new IllegalArgumentException("Unsupported material: " + material);
        }
        parsed.setAmount(amount);
        return new ItemBuilder(parsed);
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
    }

    public ItemBuilder name(String name) {
        return meta(meta -> meta.setDisplayName(CC.translate(name)));
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<String> lore) {
        List<String> wrapped = new ArrayList<>(lore.size());
        for (String line : lore) {
            wrapped.addAll(FormatUtil.wordWrap(line == null ? "" : line));
        }
        return meta(meta -> meta.setLore(CC.translate(wrapped)));
    }

    public ItemBuilder appendLore(String... lines) {
        return appendLore(Arrays.asList(lines));
    }

    public ItemBuilder appendLore(List<String> lines) {
        return meta(meta -> {
            List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<String>();
            for (String line : lines) {
                lore.addAll(CC.translate(FormatUtil.wordWrap(line == null ? "" : line)));
            }
            meta.setLore(lore);
        });
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder durability(int damage) {
        // Damageable only exists from 1.13, older servers keep damage on the stack itself.
        if (VersioningChecker.supports("1.13")) {
            return meta(Damageable.class, meta -> meta.setDamage(damage));
        }
        itemStack.setDurability((short) damage);
        return this;
    }

    public ItemBuilder enchant(XEnchantment enchantment, int level) {
        Enchantment resolved = enchantment.get();
        return resolved == null ? this : meta(meta -> meta.addEnchant(resolved, level, true));
    }

    public ItemBuilder removeEnchant(XEnchantment enchantment) {
        Enchantment resolved = enchantment.get();
        if (resolved != null) {
            itemStack.removeEnchantment(resolved);
        }
        return this;
    }

    public ItemBuilder flags(XItemFlag... flags) {
        return meta(meta -> {
            for (XItemFlag flag : flags) {
                flag.set(meta);
            }
        });
    }

    public ItemBuilder removeFlags(XItemFlag... flags) {
        return meta(meta -> {
            for (XItemFlag flag : flags) {
                flag.removeFrom(meta);
            }
        });
    }

    public ItemBuilder glow(boolean glow) {
        return meta(meta -> {
            Enchantment unbreaking = XEnchantment.UNBREAKING.get();
            if (glow) {
                if (unbreaking != null) {
                    meta.addEnchant(unbreaking, 1, true);
                }
                XItemFlag.HIDE_ENCHANTS.set(meta);
                return;
            }
            if (unbreaking != null) {
                meta.removeEnchant(unbreaking);
            }
            if (!meta.hasEnchants()) {
                XItemFlag.HIDE_ENCHANTS.removeFrom(meta);
            }
        });
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        // ItemMeta#setUnbreakable only exists from 1.11.
        return VersioningChecker.supports("1.11") ? meta(meta -> meta.setUnbreakable(unbreakable)) : this;
    }

    public ItemBuilder modelData(int modelData) {
        // Custom model data only exists from 1.14.
        return VersioningChecker.supports("1.14") ? meta(meta -> meta.setCustomModelData(modelData)) : this;
    }

    public ItemBuilder skull(String name) {
        Player online = Bukkit.getPlayerExact(name);
        return skull(online != null ? profileOf(online) : Profileable.detect(name));
    }

    public ItemBuilder skull(UUID uuid) {
        Player online = Bukkit.getPlayer(uuid);
        return skull(online != null ? profileOf(online) : Profileable.of(uuid));
    }

    public ItemBuilder skull(OfflinePlayer offlinePlayer) {
        Player online = offlinePlayer.getPlayer();
        return skull(online != null ? profileOf(online) : Profileable.of(offlinePlayer));
    }

    public ItemBuilder skullTexture(String texture) {
        return skull(Profileable.detect(texture));
    }

    private static Profileable profileOf(Player player) {
        // Paper exposes the live profile (so custom skins are kept) from 1.12.2, older servers look the player up.
        if (!VersioningChecker.supports("1.12.2")) {
            return Profileable.of(player);
        }
        return player.getPlayerProfile().getProperties().stream()
                .filter(property -> property.getName().equals("textures"))
                .findFirst()
                .map(property -> Profileable.detect(property.getValue()))
                .orElseGet(() -> Profileable.of(player));
    }

    private ItemBuilder skull(Profileable profile) {
        if (XMaterial.PLAYER_HEAD.isSimilar(itemStack)) {
            XSkull.of(itemStack).profile(profile).lenient().apply();
        }
        return this;
    }

    public ItemBuilder leatherColor(Color color) {
        return meta(LeatherArmorMeta.class, meta -> meta.setColor(color));
    }

    public ItemBuilder potionEffect(XPotion type, int durationTicks, int level) {
        return meta(PotionMeta.class, meta -> {
            PotionEffect effect = type.buildPotionEffect(durationTicks, level);
            if (effect != null) {
                meta.addCustomEffect(effect, true);
            }
        });
    }

    public ItemBuilder potionColor(Color color) {
        // PotionMeta#setColor only exists from 1.11.
        return VersioningChecker.supports("1.11") ? meta(PotionMeta.class, meta -> meta.setColor(color)) : this;
    }

    public ItemBuilder fireworkPower(int power) {
        return meta(FireworkMeta.class, meta -> meta.setPower(power));
    }

    public ItemBuilder unstackable(boolean unstackable) {
        // Persistent data containers only exist from 1.14.
        if (!VersioningChecker.supports("1.14")) {
            return this;
        }
        return meta(meta -> {
            NamespacedKey key = new NamespacedKey("conaxgames", "unstackable");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (unstackable) {
                container.set(key, PersistentDataType.STRING, UUID.randomUUID().toString());
                return;
            }
            container.remove(key);
        });
    }

    public ItemStack build() {
        return itemStack;
    }

    // Bukkit's get/set meta round trip works on every version, unlike Paper's ItemStack#editMeta (1.17+).
    private ItemBuilder meta(Consumer<ItemMeta> edit) {
        return meta(ItemMeta.class, edit);
    }

    private <M extends ItemMeta> ItemBuilder meta(Class<M> type, Consumer<M> edit) {
        ItemMeta meta = itemStack.getItemMeta();
        if (type.isInstance(meta)) {
            edit.accept(type.cast(meta));
            itemStack.setItemMeta(meta);
        }
        return this;
    }
}
