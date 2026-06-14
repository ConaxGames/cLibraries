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

    public Clickable() {}

    public Clickable(String msg) {
        add(msg);
    }

    public Clickable(String msg, String hoverMsg, String clickString) {
        add(msg, hoverMsg, clickString);
    }

    public TextComponent add(String msg, String hoverMsg, String clickString) {
        return append(msg, hoverMsg,
                clickString == null ? null : new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
    }

    public TextComponent addUrl(String msg, String hoverMsg, String url) {
        return append(msg, hoverMsg,
                url == null ? null : new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    }

    public void add(String message) {
        for (var part : TextComponent.fromLegacyText(message)) {
            components.add((TextComponent) part);
        }
    }

    public void sendToPlayer(Player player) {
        player.spigot().sendMessage(asComponents());
    }

    public TextComponent[] asComponents() {
        return components.toArray(TextComponent[]::new);
    }

    private TextComponent append(String msg, String hoverMsg, ClickEvent clickEvent) {
        var parts = TextComponent.fromLegacyText(msg);
        var root = (TextComponent) parts[0];
        for (int i = 1; i < parts.length; i++) {
            root.addExtra(parts[i]);
        }
        if (hoverMsg != null) {
            root.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverMsg)));
        }
        if (clickEvent != null) {
            root.setClickEvent(clickEvent);
        }
        components.add(root);
        return root;
    }
}
