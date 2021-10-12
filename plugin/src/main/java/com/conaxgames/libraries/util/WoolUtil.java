package com.conaxgames.libraries.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class WoolUtil {

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
		switch (color) {
			case BLACK:
				return 15;
			case DARK_BLUE:
			case BLUE:
				return 11;
			case DARK_GREEN:
				return 13;
			case DARK_AQUA:
				return 9;
			case DARK_RED:
			case RED:
				return 14;
			case DARK_PURPLE:
				return 10;
			case GOLD:
				return 1;
			case GRAY:
				return 8;
			case DARK_GRAY:
				return 7;
			case GREEN:
				return 5;
			case AQUA:
				return 3;
			case LIGHT_PURPLE:
				return 2;
			case YELLOW:
				return 4;
			default:
				return 0;
		}
	}

	public static int convertCCToWoolData(String color) {
		if (Objects.equals(color, CC.DARK_RED)) {
			color = CC.RED;
		}

		return woolCCs.indexOf(color);
	}
}
