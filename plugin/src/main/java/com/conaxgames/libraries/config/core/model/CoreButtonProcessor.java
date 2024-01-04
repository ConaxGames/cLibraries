package com.conaxgames.libraries.config.core.model;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.config.core.CoreMenu;
import com.conaxgames.libraries.hooks.HookType;
import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.Formatter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class CoreButtonProcessor {

    public final CoreMenu menu;
    public final ConfigButtonData data;
    public final Player player;

    // PERMISSION, EMPTY_INVENTORY
    public CoreProcessorDenial conditions() {
        List<String> conditions = data.getConditions();

        AtomicReference<CoreProcessorDenial> denial = new AtomicReference<>(null);
        conditions.forEach(condition -> {
            String[] split = condition.split(" ");
            String type = split[0];

            switch (type.toUpperCase()) {
                case "STRING_CONTAINS": {
                    if (split.length == 3) {
                        String input = applyPlaceholders(player, null, split[1]);
                        String value = split[2];
                        if (input != null && value != null && input.contains(value))
                            denial.set(new CoreProcessorDenial(CC.RED + input + " did not contain " + value + "..."));
                    }
                    break;
                }

                case "STRING_EQUALS": {
                    if (split.length == 3) {
                        String input = applyPlaceholders(player, null, split[1]);
                        String value = split[2];
                        if (input != null && input.equals(value))
                            denial.set(new CoreProcessorDenial(CC.RED + input + " did not equal " + value + "..."));
                    }
                    break;
                }

                case "STRING_EQUALS_IGNORECASE": {
                    if (split.length == 3) {
                        String input = applyPlaceholders(player, null, split[1]);
                        String value = split[2];
                        if (input != null && input.equalsIgnoreCase(value))
                            denial.set(new CoreProcessorDenial(CC.RED + input + " did not equal " + value + "..."));
                    }
                    break;
                }

                case "STRING_DOES_NOT_CONTAIN": {
                    if (split.length == 3) {
                        String input = applyPlaceholders(player, null, split[1]);
                        String value = split[2];
                        if (input != null && value != null && !input.contains(value))
                            denial.set(new CoreProcessorDenial(CC.RED + input + " contained " + value + "..."));
                    }
                    break;
                }

                case "STRING_DOES_NOT_EQUAL": {
                    if (split.length == 3) {
                        String input = applyPlaceholders(player, null, split[1]);
                        String value = split[2];
                        if (input != null && !input.equals(value))
                            denial.set(new CoreProcessorDenial(CC.RED + input + " equalled " + value + "..."));
                    }
                    break;
                }

                case "STRING_DOES_NOT_EQUAL_IGNORECASE": {
                    if (split.length == 3) {
                        String input = applyPlaceholders(player, null, split[1]);
                        String value = split[2];
                        if (input != null && !input.equalsIgnoreCase(value))
                            denial.set(new CoreProcessorDenial(CC.RED + input + " equalled " + value + "..."));
                    }
                    break;
                }

                case "LOGIC": {
                    if (split.length == 4) {
                        String inputValue = parsePAPI(player, split[1]);
                        String operator = split[2];
                        String requiredValue = split[3];

                        double in;
                        try {
                            in = Double.parseDouble(inputValue);
                        }
                        catch (NumberFormatException exception) {
                            denial.set(new CoreProcessorDenial(CC.YELLOW + inputValue + CC.RED + " is not a valid number..."));
                            break;
                        }

                        double res;
                        try {
                            res = Double.parseDouble(requiredValue);
                        }
                        catch (NumberFormatException exception) {
                            denial.set(new CoreProcessorDenial(CC.YELLOW + requiredValue + CC.RED + " is not a valid number..."));
                            break;
                        }

                        String niceInputValue = Formatter.commaFormatInteger((int) in);
                        String niceRequiredValue = Formatter.commaFormatInteger((int) res);

                        switch (operator) {
                            case "GREATER_THAN": {
                                if (in > res) denial.set(new CoreProcessorDenial(CC.RED + "You have " + CC.YELLOW + niceInputValue + "/" + niceRequiredValue + CC.RED + "..."));
                            }
                            case "GREATER_THAN_EQUAL_TO": {
                                if (in >= res) denial.set(new CoreProcessorDenial(CC.RED + "You have " + CC.YELLOW + niceInputValue + "/" + niceRequiredValue + CC.RED + "..."));
                            }
                            case "EQUAL_TO": {
                                if  (in == res) denial.set(new CoreProcessorDenial(CC.RED + "You need " + CC.YELLOW + niceRequiredValue + " but have " + niceInputValue + CC.RED + "..."));
                            }
                            case "NOT_EQUAL_TO": {
                                if  (in != res) denial.set(new CoreProcessorDenial(CC.RED + "You need " + CC.YELLOW + niceRequiredValue + " but have " + niceInputValue + CC.RED + "..."));
                            }
                            case "LESS_THAN_EQUAL_TO": {
                                if  (in <= res) denial.set(new CoreProcessorDenial(CC.RED + "You have " + CC.YELLOW + niceInputValue + "/" + niceRequiredValue + CC.RED + "..."));
                            }
                            case "LESS_THAN": {
                                if  (in < res) denial.set(new CoreProcessorDenial(CC.RED + "You have " + CC.YELLOW + niceInputValue + "/" + niceRequiredValue + CC.RED + "..."));
                            }
                        }

                    }
                    break;
                }

                case "PERMISSION": {
                    if (split.length == 2) {
                        String permissionValue = split[1];
                        if (permissionValue != null && !player.hasPermission(permissionValue))
                            denial.set(new CoreProcessorDenial(CC.RED + "You required the " + CC.YELLOW + permissionValue + CC.RED + " permission..."));
                    }
                    break;
                }

                case "EMPTY_INVENTORY": {
                    if (type.equalsIgnoreCase("EMPTY_INVENTORY")) {
                        if (!player.getInventory().isEmpty())
                            denial.set(new CoreProcessorDenial(CC.RED + "You need an empty inventory to use this..."));
                    }
                    break;
                }
            }
        });

        return denial.get();
    }

    // CLOSE, OPEN:, MESSAGE:, CONSOLE:, PLAYER:/, BROADCAST:/
    public void execute() {
        data.getActions().forEach(action -> {
            if (action.equalsIgnoreCase("CLOSE")) {
                player.closeInventory();
            }

            if (action.startsWith("OPEN:")) {
                String name = applyPlaceholders(player, "OPEN:", action);
                ConfigMenuData data = menu.getMenuByName(name.trim());
                if (data != null) {
                    menu.openMenu(player, data);
                } else {
                    player.sendMessage(CC.RED + "The " + CC.YELLOW + name + CC.RED + " menu could not be found...");
                    player.sendMessage(CC.RED + "Available menus: " + CC.YELLOW + menu.getMenuNames());
                }
            }

            if (action.startsWith("MESSAGE:")) {
                String converted = applyPlaceholders(player, "MESSAGE:", action);
                player.sendMessage(converted);
            }

            if (action.startsWith("BROADCAST:")) {
                String converted = applyPlaceholders(player, "BROADCAST:", action);
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
        if (prefix != null) {
            action = action.replaceFirst(prefix, "");
        }

        action = parsePAPI(player, action);
        action = CC.translate(action);
        action = action.replace("%player%", player.getName());
        action = action.replace("%PLAYER%", player.getName());
        action = action.replace("%player_display%", player.getDisplayName());
        action = action.replace("%PLAYER_DISPLAY%", player.getDisplayName());

        return action;
    }

    public String parsePAPI(Player player, String string) {
        if (LibraryPlugin.getInstance().getHookManager().isHooked(HookType.PLACEHOLDERAPI)) {
            return PlaceholderAPI.setPlaceholders(player, string.trim());
        }
        return string;
    }
}
