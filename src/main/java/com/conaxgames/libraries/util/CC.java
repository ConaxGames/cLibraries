package com.conaxgames.libraries.util;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for Minecraft chat color and formatting operations.
 * Provides static access to color codes, formatting options, and color translation methods.
 * 
 * <p>Technical Details:
 * <ul>
 *   <li>Implements Bukkit's ChatColor system with additional formatting combinations</li>
 *   <li>Supports hex color codes via &#RRGGBB format using BungeeCord's ChatColor API</li>
 *   <li>Provides theme-based color constants (PRIMARY, SECONDARY, TERTIARY)</li>
 *   <li>Includes combined formatting constants (e.g., B_RED, I_BLUE)</li>
 *   <li>Thread-safe implementation with immutable constants</li>
 * </ul>
 * </p>
 * 
 * <p>Implementation Notes:
 * <ul>
 *   <li>All color constants are pre-computed ChatColor.toString() values</li>
 *   <li>Hex color translation uses regex pattern matching</li>
 *   <li>List translation operations utilize Java 8+ Stream API</li>
 *   <li>Color code translation supports both '&' and hex '&#' formats</li>
 * </ul>
 * </p>
 */
public final class CC {

	// Hex color pattern for efficient reuse
	private static final Pattern HEX_PATTERN = Pattern.compile("&#[A-Fa-f0-9]{6}");

	public static final String U = ChatColor.UNDERLINE.toString();
	public static final String BLUE = ChatColor.BLUE.toString();
	public static final String AQUA = ChatColor.AQUA.toString();
	public static final String YELLOW = ChatColor.YELLOW.toString();
	public static final String RED = ChatColor.RED.toString();
	public static final String GRAY = ChatColor.GRAY.toString();
	public static final String GOLD = ChatColor.GOLD.toString();
	public static final String GREEN = ChatColor.GREEN.toString();
	public static final String WHITE = ChatColor.WHITE.toString();
	public static final String BLACK = ChatColor.BLACK.toString();
	public static final String BOLD = ChatColor.BOLD.toString();
	public static final String ITALIC = ChatColor.ITALIC.toString();
	public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
	public static final String RESET = ChatColor.RESET.toString();
	public static final String MAGIC = ChatColor.MAGIC.toString();
	public static final String OBFUSCATED = MAGIC;
	public static final String B = BOLD;
	public static final String M = MAGIC;
	public static final String O = MAGIC;
	public static final String I = ITALIC;
	public static final String S = STRIKE_THROUGH;
	public static final String R = RESET;
	public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
	public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
	public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
	public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
	public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
	public static final String DARK_RED = ChatColor.DARK_RED.toString();
	public static final String D_BLUE = DARK_BLUE;
	public static final String D_AQUA = DARK_AQUA;
	public static final String D_GRAY = DARK_GRAY;
	public static final String D_GREEN = DARK_GREEN;
	public static final String D_PURPLE = DARK_PURPLE;
	public static final String D_RED = DARK_RED;
	public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
	public static final String L_PURPLE = LIGHT_PURPLE;
	public static final String PINK = L_PURPLE;

	public static final String B_LIGHT_PURPLE =  LIGHT_PURPLE + B;
	public static final String B_BLUE =  BLUE + B;
	public static final String B_AQUA =  AQUA + B;
	public static final String B_YELLOW =  YELLOW + B;
	public static final String B_RED =  RED + B ;
	public static final String B_GRAY =  GRAY + B;
	public static final String B_GOLD =  GOLD + B;
	public static final String B_GREEN =  GREEN + B;
	public static final String B_WHITE =  WHITE + B;
	public static final String B_BLACK =  BLACK + B;
	public static final String BD_BLUE =  D_BLUE + B;
	public static final String BD_AQUA =  D_AQUA + B;
	public static final String BD_DARKAQUA =  D_AQUA + B;
	public static final String BD_GRAY =  D_GRAY + B;
	public static final String BD_GREEN =  D_GREEN + B;
	public static final String BD_PURPLE =  D_PURPLE + B;
	public static final String BD_RED =  D_RED + B;
	public static final String BL_PURPLE =  L_PURPLE + B;
	public static final String B_DARK_RED =  DARK_RED + B;
	public static final String B_DARK_GREEN =  DARK_GREEN + B;
	public static final String B_DARK_PURPLE =  DARK_PURPLE + B;
	public static final String B_DARK_AQUA =  DARK_AQUA + B;

	public static final String I_BLUE = BLUE + I;
	public static final String I_AQUA = AQUA + I;
	public static final String I_YELLOW = YELLOW + I;
	public static final String I_RED = RED + I;
	public static final String I_GRAY = GRAY + I;
	public static final String I_GOLD = GOLD + I;
	public static final String I_GREEN = GREEN + I;
	public static final String I_WHITE = WHITE + I;
	public static final String I_BLACK = BLACK + I;
	public static final String ID_RED = D_RED + I;
	public static final String ID_BLUE = D_BLUE + I;
	public static final String ID_AQUA = D_AQUA + I;
	public static final String ID_GRAY = D_GRAY + I;
	public static final String ID_GREEN = D_GREEN + I;
	public static final String ID_PURPLE = D_PURPLE + I;
	public static final String IL_PURPLE = L_PURPLE + I;
	public static final String VAPE = "§8 §8 §1 §3 §3 §7 §8 §r";
	public static final String BLANK_LINE = VAPE;
	public static final String BL = BLANK_LINE;
	public static final String U_GREEN = U + GREEN;
	public static final String U_GRAY = U + GRAY;
	public static final String U_WHITE = U + WHITE;
	public static final String U_GOLD = U + GOLD;
	public static final String U_YELLOW = U + YELLOW;
	public static final String U_LIGHT_PURPLE = U + LIGHT_PURPLE;
	public static final String U_AQUA = U + AQUA;
	public static final String U_DARK_AQUA = U + DARK_AQUA;

	public static String PRIMARY = YELLOW;
	public static String SECONDARY = GOLD;
	public static String TERTIARY = GRAY;
	public static String B_PRIMARY = PRIMARY + B;
	public static String B_SECONDARY = SECONDARY + B;
	public static String B_TERTIARY = TERTIARY + B;

	/**
	 * Translates color codes in a string, including hex colors.
	 * Supports both standard Minecraft color codes (&) and hex colors (&#RRGGBB).
	 *
	 * @param string The text to translate
	 * @return The translated text with all color codes applied
	 */
	public static String translate(String string) {
		String translatedHex = translateHex(string);
		return ChatColor.translateAlternateColorCodes('&', translatedHex);
	}

	/**
	 * Translates hex color codes in the format &#RRGGBB to their corresponding color codes.
	 * This is a private helper method used by the public translate method.
	 *
	 * @param message The text containing hex color codes
	 * @return The text with hex colors translated
	 */
	private static String translateHex(String message) {
		Matcher hexMatcher = HEX_PATTERN.matcher(message);
		while (hexMatcher.find()) {
			String hexColor = hexMatcher.group().substring(2);
			message = message.replace(hexMatcher.group(), net.md_5.bungee.api.ChatColor.of("#" + hexColor).toString());
		}

		return message;
	}

	/**
	 * Translates color codes in a list of strings.
	 *
	 * @param text List of strings to translate
	 * @return List of translated strings
	 */
	public static List<String> translate(List<String> text) {
		return text.stream().map(CC::translate).collect(Collectors.toList());
	}

	/**
	 * Translates color codes in multiple strings.
	 *
	 * @param text Variable number of strings to translate
	 * @return List of translated strings
	 */
	public static List<String> translate(String... text) {
		return translate(Arrays.asList(text));
	}

}
