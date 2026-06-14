package com.conaxgames.libraries.util;

import com.conaxgames.libraries.message.CC;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
        var parsed = material.parseItem();
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
        itemStack.editMeta(meta -> meta.setDisplayName(CC.translate(name)));
        return this;
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<String> lore) {
        itemStack.editMeta(meta -> meta.setLore(CC.translate(lore)));
        return this;
    }

    public ItemBuilder appendLore(String... lines) {
        return appendLore(Arrays.asList(lines));
    }

    public ItemBuilder appendLore(List<String> lines) {
        itemStack.editMeta(meta -> {
            var lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<String>();
            lore.addAll(CC.translate(lines));
            meta.setLore(lore);
        });
        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder durability(int damage) {
        if (!itemStack.editMeta(Damageable.class, meta -> meta.setDamage(damage))) {
            itemStack.setDurability((short) damage);
        }
        return this;
    }

    public ItemBuilder enchant(XEnchantment enchantment, int level) {
        var resolved = enchantment.get();
        if (resolved != null) {
            itemStack.editMeta(meta -> meta.addEnchant(resolved, level, true));
        }
        return this;
    }

    public ItemBuilder removeEnchant(XEnchantment enchantment) {
        var resolved = enchantment.get();
        if (resolved != null) {
            itemStack.removeEnchantment(resolved);
        }
        return this;
    }

    public ItemBuilder flags(XItemFlag... flags) {
        itemStack.editMeta(meta -> {
            for (var flag : flags) {
                flag.set(meta);
            }
        });
        return this;
    }

    public ItemBuilder removeFlags(XItemFlag... flags) {
        itemStack.editMeta(meta -> {
            for (var flag : flags) {
                flag.removeFrom(meta);
            }
        });
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        itemStack.editMeta(meta -> {
            var unbreaking = XEnchantment.UNBREAKING.get();
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
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        itemStack.editMeta(meta -> meta.setUnbreakable(unbreakable));
        return this;
    }

    public ItemBuilder modelData(int modelData) {
        itemStack.editMeta(meta -> meta.setCustomModelData(modelData));
        return this;
    }

    public ItemBuilder skull(String name) {
        var offlinePlayer = Bukkit.getOfflinePlayer(name);
        var profile = offlinePlayer.hasPlayedBefore()
                ? Profileable.of(offlinePlayer)
                : Profileable.username(name);
        return skull(profile);
    }

    public ItemBuilder skull(UUID uuid) {
        return skull(Profileable.of(uuid));
    }

    public ItemBuilder skull(OfflinePlayer offlinePlayer) {
        return skull(Profileable.of(offlinePlayer));
    }

    public ItemBuilder skullTexture(String texture) {
        return skull(Profileable.detect(texture));
    }

    private ItemBuilder skull(Profileable profile) {
        if (XMaterial.PLAYER_HEAD.isSimilar(itemStack)) {
            XSkull.of(itemStack).profile(profile).lenient().apply();
        }
        return this;
    }

    public ItemBuilder leatherColor(Color color) {
        itemStack.editMeta(meta -> {
            if (meta instanceof LeatherArmorMeta leather) {
                leather.setColor(color);
            }
        });
        return this;
    }

    public ItemBuilder potionEffect(XPotion type, int durationTicks, int level) {
        itemStack.editMeta(PotionMeta.class, meta -> {
            var effect = type.buildPotionEffect(durationTicks, level);
            if (effect != null) {
                meta.addCustomEffect(effect, true);
            }
        });
        return this;
    }

    public ItemBuilder potionColor(Color color) {
        itemStack.editMeta(PotionMeta.class, meta -> meta.setColor(color));
        return this;
    }

    public ItemBuilder unstackable(boolean unstackable) {
        itemStack.editMeta(meta -> {
            var key = new NamespacedKey("conaxgames", "unstackable");
            var container = meta.getPersistentDataContainer();
            if (unstackable) {
                container.set(key, PersistentDataType.STRING, UUID.randomUUID().toString());
                return;
            }
            container.remove(key);
        });
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}
