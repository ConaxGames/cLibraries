package com.conaxgames.libraries.message;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Clickable {

    private final List<BaseComponent> components = new ArrayList<>();

    public Clickable() {}

    public Clickable(String message) {
        add(message);
    }

    public Clickable(String message, String hover, String command) {
        add(message, hover, command);
    }

    public void add(String message) {
        addRaw(CC.translate(message));
    }

    // Skips '&' translation, for player written text that must not gain color codes.
    public void addRaw(String message) {
        if (message != null) {
            Collections.addAll(components, TextComponent.fromLegacyText(message));
        }
    }

    public void add(String message, String hover, String command) {
        append(message, hover, command == null ? null : new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }

    public void addSuggest(String message, String hover, String command) {
        append(message, hover, command == null ? null : new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
    }

    public void addUrl(String message, String hover, String url) {
        append(message, hover, url == null ? null : new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    }

    public void sendToPlayer(Player player) {
        player.spigot().sendMessage(components.toArray(new BaseComponent[0]));
    }

    // An empty hover or click counts as none, so a blank config value disables it.
    private void append(String message, String hover, ClickEvent click) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(CC.translate(message)));
        if (hover != null && !hover.isEmpty()) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(CC.translate(hover))));
        }
        component.setClickEvent(click == null || click.getValue().isEmpty() ? null : click);
        components.add(component);
    }
}
