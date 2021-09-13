package com.conaxgames.libraries.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class WoolUtil {

	private static final ArrayList<ChatColor> woolColors = new ArrayList<>(Arrays.asList(
			ChatColor.WHITE,
			ChatColor.GOLD,
			ChatColor.LIGHT_PURPLE,
			ChatColor.AQUA,
			ChatColor.YELLOW,
			ChatColor.GREEN,
			ChatColor.LIGHT_PURPLE,
			ChatColor.DARK_GRAY,
			ChatColor.GRAY,
			ChatColor.DARK_AQUA,
			ChatColor.DARK_PURPLE,
			ChatColor.BLUE,
			ChatColor.BLACK,
			ChatColor.DARK_GREEN,
			ChatColor.RED,
			ChatColor.BLACK
	));

	private static final ArrayList<String> woolCCs = new ArrayList<>(Arrays.asList(
			CC.WHITE,
			CC.GOLD,
			CC.LIGHT_PURPLE,
			CC.AQUA,
			CC.YELLOW,
			CC.GREEN,
			CC.LIGHT_PURPLE,
			CC.DARK_GRAY,
			CC.GRAY,
			CC.DARK_AQUA,
			CC.DARK_PURPLE,
			CC.BLUE,
			CC.BLACK,
			CC.DARK_GREEN,
			CC.RED,
			CC.BLACK
	));

	public static int convertChatColorToWoolData(ChatColor color) {
		if (color == ChatColor.DARK_RED) color = ChatColor.RED;

		return woolColors.indexOf(color);
	}

	public static int convertCCToWoolData(String color) {
		if (Objects.equals(color, CC.DARK_RED)) {
			color = CC.RED;
		}

		return woolCCs.indexOf(color);
	}
}
