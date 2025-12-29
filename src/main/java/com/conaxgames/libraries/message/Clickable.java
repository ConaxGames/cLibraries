package com.conaxgames.libraries.message;

import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class Clickable {

	private List<TextComponent> components = new ArrayList<>();

	public Clickable(String msg) {
		this.add(msg);
	}

	public Clickable(String msg, String hoverMsg, String clickString) {
		this.add(msg, hoverMsg, clickString);
	}

	public TextComponent add(String msg, String hoverMsg, String clickString) {
		BaseComponent[] baseComponents = TextComponent.fromLegacyText(msg);
		TextComponent message;

		if (baseComponents.length == 0) {
			message = new TextComponent(msg);
		} else {
			message = (TextComponent) baseComponents[0];
			for (int i = 1; i < baseComponents.length; i++) {
				message.addExtra(baseComponents[i]);
			}
		}

		if (hoverMsg != null) {
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverMsg)));
		}

		if (clickString != null) {
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
		}

		this.components.add(message);

		return message;
	}

	public void add(String message) {
		BaseComponent[] baseComponents = TextComponent.fromLegacyText(message);
		for (BaseComponent component : baseComponents) {
			this.components.add((TextComponent) component);
		}
	}

	public void sendToPlayer(Player player) {
		player.spigot().sendMessage(this.asComponents());
	}

	public TextComponent[] asComponents() {
		return this.components.toArray(new TextComponent[0]);
	}
}
