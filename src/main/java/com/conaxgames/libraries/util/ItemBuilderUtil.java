package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemBuilderUtil {

    private final ItemStack is;

    public ItemBuilderUtil(Material m) {
        this(m, 1);
    }

    public ItemBuilderUtil(ItemStack is) {
        this.is = is;
    }

    public ItemBuilderUtil(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    public ItemBuilderUtil(Material m, int amount, byte durability) {
        is = new ItemStack(m, amount, durability);
    }

    private static List<String> copyLore(ItemMeta im) {
        List<String> lore = im.getLore();
        return lore == null ? new ArrayList<>() : new ArrayList<>(lore);
    }

    @Override
    public ItemBuilderUtil clone() {
        return new ItemBuilderUtil(is.clone());
    }

    public ItemBuilderUtil setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    public ItemBuilderUtil setName(String name) {
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return this;
        }
        im.setDisplayName(CC.translate(name));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil addUnsafeEnchantment(Enchantment ench, int level) {
        is.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilderUtil addUnsafeEnchantmentIf(boolean condition, Enchantment ench, int level) {
        if (condition) {
            return addUnsafeEnchantment(ench, level);
        }
        return this;
    }

    public ItemBuilderUtil removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    public ItemBuilderUtil setSkullOwner(String name) {
        if (!isPlayerHead()) {
            return this;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        Profileable profile = offlinePlayer.hasPlayedBefore()
                ? Profileable.of(offlinePlayer)
                : Profileable.username(name);
        XSkull.of(is).profile(profile).lenient().apply();
        return this;
    }

    public ItemBuilderUtil setSkullOwner(OfflinePlayer offlinePlayer) {
        if (!isPlayerHead()) {
            return this;
        }
        XSkull.of(is).profile(Profileable.of(offlinePlayer)).lenient().apply();
        return this;
    }

    public ItemBuilderUtil setSkullOwner(UUID uuid) {
        if (!isPlayerHead()) {
            return this;
        }
        XSkull.of(is).profile(Profileable.of(uuid)).lenient().apply();
        return this;
    }

    public ItemBuilderUtil setSkullProfile(String texture) {
        if (!isPlayerHead()) {
            return this;
        }
        XSkull.of(is).profile(Profileable.detect(texture)).lenient().apply();
        return this;
    }

    public ItemBuilderUtil addEnchant(Enchantment ench, int level) {
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return this;
        }
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilderUtil setInfinityDurability() {
        is.setDurability(Short.MAX_VALUE);
        return this;
    }

    public ItemBuilderUtil setLore(String... lore) {
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return this;
        }
        im.setLore(CC.translate(lore));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil setLore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return this;
        }
        im.setLore(CC.translate(lore));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return this;
        }
        List<String> lore = copyLore(im);
        if (!lore.remove(line)) {
            return this;
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return this;
        }
        List<String> lore = copyLore(im);
        if (index < 0 || index >= lore.size()) {
            return this;
        }
        lore.remove(index);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil addLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return this;
        }
        List<String> lore = copyLore(im);
        lore.add(CC.translate(line));
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil addLoreLineIf(boolean condition, String line) {
        if (condition) {
            return addLoreLine(line);
        }
        return this;
    }

    public ItemBuilderUtil addLoreLineList(List<String> lines) {
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return this;
        }
        List<String> lore = copyLore(im);
        lore.addAll(CC.translate(lines));
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil addLoreLine(String line, int pos) {
        ItemMeta im = is.getItemMeta();
        if (im == null) {
            return this;
        }
        List<String> lore = copyLore(im);
        if (pos < 0 || pos >= lore.size()) {
            return this;
        }
        lore.set(pos, CC.translate(line));
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilderUtil setDyeColor(DyeColor color) {
        is.setDurability(VersioningChecker.getInstance().isServerVersionBefore("1.16.5")
                ? color.getWoolData()
                : color.getDyeData());
        return this;
    }

    public ItemBuilderUtil setLeatherArmorColor(Color color) {
        ItemMeta meta = is.getItemMeta();
        if (!(meta instanceof LeatherArmorMeta im)) {
            return this;
        }
        im.setColor(color);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil setUnbreakable() {
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            is.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilderUtil setFlags(ItemFlag... flags) {
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(flags);
            is.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilderUtil removeFlags(ItemFlag... flags) {
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            meta.removeItemFlags(flags);
            is.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilderUtil hideAttributes() {
        return setFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    public ItemBuilderUtil hideEnchants() {
        return setFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemBuilderUtil showAttributes() {
        return removeFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    public ItemBuilderUtil setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilderUtil setCustomModelData(int modelData) {
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(modelData);
            is.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilderUtil setGlow() {
        return setGlow(true);
    }

    public ItemBuilderUtil setGlow(boolean glow) {
        ItemMeta meta = is.getItemMeta();
        if (meta == null) {
            return this;
        }
        Enchantment unbreaking = XEnchantment.UNBREAKING.get();
        if (unbreaking == null) {
            return this;
        }
        if (glow) {
            meta.addEnchant(unbreaking, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeEnchant(unbreaking);
            if (!meta.hasEnchants()) {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilderUtil setUnstackable() {
        ItemMeta meta = is.getItemMeta();
        if (meta == null) {
            return this;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey("conaxgames", "unstackable");
        container.set(key, PersistentDataType.STRING, UUID.randomUUID().toString());
        is.setItemMeta(meta);
        return this;
    }

    public ItemStack toItemStack() {
        return is;
    }

    public ItemStack build() {
        return is;
    }

    private boolean isPlayerHead() {
        return is.getType() == XMaterial.PLAYER_HEAD.get();
    }
}
