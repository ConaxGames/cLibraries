package com.conaxgames.libraries.message;

import com.conaxgames.libraries.nms.LibNMSManager;
import com.conaxgames.libraries.nms.LibServerVersion;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
//import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Clickable {

	private List<TextComponent> components = new ArrayList<>();

	public Clickable(String msg) {
		TextComponent message = new TextComponent(msg);

		this.components.add(message);
	}

	public Clickable(String msg, String hoverMsg, String clickString) {
		this.add(msg, hoverMsg, clickString);
	}

	public TextComponent add(String msg, String hoverMsg, String clickString) {
		TextComponent message = new TextComponent(msg);

		if (LibNMSManager.getInstance().getServerVersion().equals(LibServerVersion.v1_8_R3)) {
			if (hoverMsg != null) {
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMsg).create()));
			}
		} else if (LibNMSManager.getInstance().getServerVersion().after(LibServerVersion.v1_16_R3)) {
//			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverMsg)));
		}

		if (clickString != null) {
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
		}

		this.components.add(message);

		return message;
	}

	public void add(String message) {
		this.components.add(new TextComponent(message));
	}

	public void sendToPlayer(Player player) {
		//player.sendMessage(this.asComponents());
		player.spigot().sendMessage(this.asComponents());

		// todo: this method shouldn't cause issues?
//		if (LibNMSManager.getInstance().getServerVersion().equals(LibServerVersion.v1_8_R3)) {
//			player.sendMessage(this.asComponents());
//		} else if (LibNMSManager.getInstance().getServerVersion().after(LibServerVersion.v1_16_R3)) {
//			player.spigot().sendMessage(this.asComponents());
//		}
	}

	public TextComponent[] asComponents() {
		return this.components.toArray(new TextComponent[0]);
	}
}
