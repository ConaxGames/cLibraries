package com.conaxgames.libraries.message;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Clickable {

	private final List<TextComponent> components = new ArrayList<>();

	public Clickable() {
	}

	public Clickable(String msg) {
		this.add(msg);
	}

	public Clickable(String msg, String hoverMsg, String clickString) {
		this.add(msg, hoverMsg, clickString);
	}

	public TextComponent add(String msg, String hoverMsg, String clickString) {
		return append(msg, hoverMsg, clickString == null ? null : new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
	}

	public TextComponent addUrl(String msg, String hoverMsg, String url) {
		return append(msg, hoverMsg, url == null ? null : new ClickEvent(ClickEvent.Action.OPEN_URL, url));
	}

	private TextComponent append(String msg, String hoverMsg, ClickEvent clickEvent) {
		TextComponent message = mergeLegacyText(msg);
		if (hoverMsg != null) {
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverMsg)));
		}
		if (clickEvent != null) {
			message.setClickEvent(clickEvent);
		}
		this.components.add(message);
		return message;
	}

	public void add(String message) {
		for (BaseComponent part : TextComponent.fromLegacyText(message)) {
			this.components.add((TextComponent) part);
		}
	}

	public void sendToPlayer(Player player) {
		player.spigot().sendMessage(this.asComponents());
	}

	public TextComponent[] asComponents() {
		return this.components.toArray(new TextComponent[0]);
	}

	private static TextComponent mergeLegacyText(String msg) {
		BaseComponent[] parts = TextComponent.fromLegacyText(msg);
		if (parts.length == 0) {
			return new TextComponent(msg);
		}
		TextComponent root = (TextComponent) parts[0];
		for (int i = 1; i < parts.length; i++) {
			root.addExtra(parts[i]);
		}
		return root;
	}
}
