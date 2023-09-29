package com.conaxgames.libraries.config.core.model;

import com.conaxgames.libraries.config.core.CoreMenu;
import com.conaxgames.libraries.util.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class CoreButtonProcessor {

    public final CoreMenu menu;
    public final ConfigButtonData data;
    public final Player player;

    // PERMISSION, EMPTY_INVENTORY
    public boolean matches() {
        List<String> conditions = data.getConditions();

        AtomicBoolean metAllConditions = new AtomicBoolean(true);
        conditions.forEach(condition -> {
            String[] split = condition.split(":", 1);

            String type = split[0];
            String value = null;
            if (split.length == 2) {
                value = split[1];
            }

            if (type.equalsIgnoreCase("PERMISSION") && value != null) {
                if (!player.hasPermission(value)) metAllConditions.set(false);
            }

            if (type.equalsIgnoreCase("EMPTY_INVENTORY")) {
                if (!player.getInventory().isEmpty()) metAllConditions.set(false);
            }
        });

        return metAllConditions.get();
    }

    // CLOSE, OPEN:, MESSAGE:, CONSOLE:, PLAYER:/, BROADCAST:/
    public void execute() {
        data.getActions().forEach(action -> {
            if (action.equalsIgnoreCase("CLOSE")) {
                player.closeInventory();
            }

            if (action.equalsIgnoreCase("OPEN:")) {
                String name = applyPlaceholders(player, "OPEN:", action);
                ConfigMenuData data = menu.getShopByName(name);
                if (data != null) {
                    menu.openMenu(player, data);
                } else {
                    player.sendMessage(CC.RED + "The " + CC.YELLOW + name + CC.RED + " menu could not be found...");
                }
            }

            if (action.startsWith("MESSAGE:")) {
                String converted = applyPlaceholders(player, "MESSAGE:", action);
                player.sendMessage(converted);
            }

            if (action.startsWith("BROADCAST:")) {
                String converted = applyPlaceholders(player, "MESSAGE:", action);
                Bukkit.broadcastMessage(converted);
            }

            if (action.startsWith("PLAYER:/")) {
                String converted = applyPlaceholders(player, "PLAYER:/", action);
                player.performCommand(converted);
            }

            if (action.startsWith("CONSOLE:/")) {
                String converted = applyPlaceholders(player, "CONSOLE:/", action);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), converted);
            }
        });
    }

    public String applyPlaceholders(Player player, String prefix, String action) {
        action = action.replaceFirst(prefix, "");
        action = CC.translate(action);
        action = action.replace("%player%", player.getName());
        action = action.replace("%PLAYER%", player.getName());
        action = action.replace("%player_display%", player.getDisplayName());
        action = action.replace("%PLAYER_DISPLAY%", player.getDisplayName());
        // apply papi placeholders
        return action;
    }

}
