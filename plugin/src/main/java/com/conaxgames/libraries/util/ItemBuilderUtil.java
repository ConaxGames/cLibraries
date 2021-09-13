package com.conaxgames.libraries.util;

import com.conaxgames.libraries.nms.LibNMSManager;
import com.conaxgames.libraries.nms.LibServerVersion;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilderUtil {

    private ItemStack is;

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

    @Override
    public ItemBuilderUtil clone() {
        return new ItemBuilderUtil(is);
    }

    public ItemBuilderUtil setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    public ItemBuilderUtil setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil addUnsafeEnchantment(Enchantment ench, int level) {
        is.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilderUtil addUnsafeEnchantmentIf(Boolean bool, Enchantment ench, int level) {
        if (bool) {
            is.addUnsafeEnchantment(ench, level);
        }
        return this;
    }

    public ItemBuilderUtil removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    public ItemBuilderUtil setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner(owner);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    public ItemBuilderUtil addEnchant(Enchantment ench, int level) {
        ItemMeta im = is.getItemMeta();
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
        im.setLore(Arrays.asList(lore));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil setLore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<String>(im.getLore());
        if (!lore.contains(line))
            return this;
        lore.remove(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<String>(im.getLore());
        if (index < 0 || index > lore.size())
            return this;
        lore.remove(index);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil addLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore())
            lore = new ArrayList<String>(im.getLore());
        lore.add(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil addLoreLineIf(Boolean bool, String line) {
        if (bool) {
            ItemMeta im = is.getItemMeta();
            List<String> lore = new ArrayList<String>();
            if (im.hasLore())
                lore = new ArrayList<String>(im.getLore());
            lore.add(line);
            im.setLore(lore);
            is.setItemMeta(im);
            return this;
        }
        return this;
    }

    public ItemBuilderUtil addLoreLineList(List<String> line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore())
            lore = new ArrayList<String>(im.getLore());
        lore.addAll(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilderUtil addLoreLine(String line, int pos) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<String>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilderUtil setDyeColor(DyeColor color) {
        this.is.setDurability(LibNMSManager.getInstance().getServerVersion().before(LibServerVersion.v1_16_R3) ? color.getData() : color.getDyeData());
        return this;
    }

    @Deprecated
    public ItemBuilderUtil setWoolColor(DyeColor color) {
        if (!is.getType().equals(Material.WOOL))
            return this;
        this.is.setDurability(LibNMSManager.getInstance().getServerVersion().before(LibServerVersion.v1_16_R3) ? color.getData() : color.getDyeData());
        return this;
    }

    public ItemBuilderUtil setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
            im.setColor(color);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    public ItemStack toItemStack() {
        return is;
    }
}