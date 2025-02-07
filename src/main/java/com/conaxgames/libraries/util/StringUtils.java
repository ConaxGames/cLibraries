package com.conaxgames.libraries.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class StringUtils {

	public static Pattern UUID_REGEX = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static void log(Level lvl, String msg) {
		Bukkit.getLogger().log(lvl, msg);
	}

	public static void sendMessage(Player p, String msg) {
		p.sendMessage(color(msg));
	}

	public static void sendMessage(UUID uuid, String msg) {
		if(Bukkit.getPlayer(uuid) != null) {
			sendMessage(Bukkit.getPlayer(uuid), msg);
		}
	}

	public static void sendMessage(CommandSender sender, String msg) {
		sender.sendMessage(color(msg));
	}

	// This method joins all lines with a string!
	public static String join(Collection coll, String string) {
		StringBuilder builder = new StringBuilder();

		for(Iterator it = coll.iterator(); it.hasNext(); builder.append((String)it.next())) {
			if (builder.length() != 0) {
				builder.append(string);
			}
		}

		return builder.toString();
	}

	public static String getHealth(Player p) {
		int roundedHearts = ((int) Math.floor(p.getHealth() / 2)) == 0 ? 1 : (int) Math.floor(p.getHealth() / 2);

		return color("&c" + roundedHearts + " &4❤");
	}

}
