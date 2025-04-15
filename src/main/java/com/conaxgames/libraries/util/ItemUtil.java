package com.conaxgames.libraries.util;

import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.message.ItemNameUtil;
import com.conaxgames.libraries.message.TimeUtil;
import com.cryptomorin.xseries.XItemFlag;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class ItemUtil {

	private ItemUtil() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}

	public static String formatMaterial(Material material) {
		String name = material.toString();
		name = name.replace('_', ' ');
		String result = "" + name.charAt(0);
		for (int i = 1; i < name.length(); i++) {
			if (name.charAt(i - 1) == ' ') {
				result += name.charAt(i);
			} else {
				result += Character.toLowerCase(name.charAt(i));
			}
		}
		return result;
	}

	public static ItemStack createPotion(String name, PotionType type, int level, int duration) {
		ItemStack itemStack = new ItemStack(Material.POTION);
		PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

		if (name != null) {
			meta.setDisplayName(CC.translate(name));
		}

		meta.setLore(Arrays.asList(
				"", CC.GRAY + ItemNameUtil.potionLookup(type.getEffectType()) + " " + FormatUtil.toRoman(level) + " Potion",
				CC.GRAY + "    Duration: " + TimeUtil.millisToRoundedTime(duration * 1000L)));

		meta.addCustomEffect(new PotionEffect(type.getEffectType(), duration * 20, level - 1), false);
		XItemFlag.decorationOnly(meta);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public static ItemStack createPotion(PotionType type, int level, int duration) {
		return createPotion(null, type, level, duration);
	}

	public static ItemStack enchantItem(ItemStack itemStack, ItemEnchant... enchantments) {
		Arrays.asList(enchantments).forEach(enchantment -> itemStack.addUnsafeEnchantment(enchantment.enchantment, enchantment.level));
		return itemStack;
	}

	public static ItemStack createItem(Material material, String name) {
		return createItem(material, name, true);
	}

	public static ItemStack createItem(Material material, String name, boolean colors) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(colors ? CC.translate(name) : name);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack createItem(Material material, String name, int amount) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(CC.translate(name));
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack createItem(Material material, String name, int amount, short damage) {
		ItemStack item = new ItemStack(material, amount, damage);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(CC.translate(name));
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack hideEnchants(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		XItemFlag.HIDE_ENCHANTS.set(meta);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack renameItem(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(CC.translate(name));
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack reloreItem(ItemStack item, String... lores) {
		return reloreItem(ReloreType.OVERWRITE, item, lores);
	}

	public static ItemStack reloreItem(ReloreType type, ItemStack item, String... lores) {
		ItemMeta meta = item.getItemMeta();

		List<String> lore = meta.getLore();
		if (lore == null) {
			lore = new LinkedList<>();
		}

		switch (type) {
			case APPEND:
				lore.addAll(Arrays.asList(lores));
				meta.setLore(translate(lore));
				break;
			case PREPEND:
				List<String> nLore = new LinkedList<>(Arrays.asList(lores));
				nLore.addAll(translate(lore));
				meta.setLore(translate(nLore));
				break;
			case OVERWRITE:
				meta.setLore(Arrays.asList(lores));
				break;
		}

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack addItemFlag(ItemStack item, ItemFlag flag) {
		ItemMeta meta = item.getItemMeta();

		meta.addItemFlags(flag);
		item.setItemMeta(meta);

		return item;
	}

	public static DyeColor getDyeColorByChar(String colorString) {
		switch(ChatColor.getByChar(colorString.charAt(colorString.indexOf(0xA7) + 1))) {
			case DARK_BLUE:
			case BLUE:
				return DyeColor.BLUE;
			case DARK_GREEN:
				return DyeColor.GREEN;
			case GREEN:
				return DyeColor.LIME;
			case DARK_AQUA:
			case AQUA:
				return DyeColor.CYAN;
			case DARK_RED:
			case RED:
				return DyeColor.RED;
			case DARK_PURPLE:
				return DyeColor.PURPLE;
			case LIGHT_PURPLE:
				return DyeColor.PINK;
			case GOLD:
				return DyeColor.ORANGE;
			case GRAY:
			case DARK_GRAY:
				return DyeColor.GRAY;
			case YELLOW:
				return DyeColor.YELLOW;
			case WHITE:
				return DyeColor.WHITE;
			default:
				return DyeColor.BLACK;
		}
	}

	public static int getWoolDmgValue(String colorString) {
		colorString = colorString.replace("&", "§");
		if (ChatColor.getByChar(colorString.charAt(colorString.indexOf(0xA7) + 1)) == null) {
			return 15;
		} else {
			switch (ChatColor.getByChar(colorString.charAt(colorString.indexOf(0xA7) + 1))) {
				case DARK_BLUE:
				case BLUE:
					return 11;
				case DARK_GREEN:
					return 13;
				case GREEN:
					return 5;
				case DARK_AQUA:
					return 9;
				case AQUA:
					return 3;
				case DARK_RED:
				case RED:
					return 14;
				case DARK_PURPLE:
					return 10;
				case LIGHT_PURPLE:
					return 2;
				case GOLD:
					return 1;
				case GRAY:
					return 8;
				case DARK_GRAY:
					return 7;
				case YELLOW:
					return 4;
				case BLACK:
					return 15;
				default:
					return 0;
			}
		}
	}

	public enum ReloreType {
		OVERWRITE,
		PREPEND,
		APPEND
	}

	public static class ItemEnchant {
		private final Enchantment enchantment;
		private final int level;
		public ItemEnchant(Enchantment enchantment, int level) {
			this.enchantment = enchantment;
			this.level = level;
		}
	}

	private static List<String> translate(List<String> text) {
		return text.stream().map(CC::translate).collect(Collectors.toList());
	}

}
