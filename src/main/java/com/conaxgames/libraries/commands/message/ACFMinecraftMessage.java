package com.conaxgames.libraries.commands.message;

import com.conaxgames.libraries.util.CC;
import java.util.Locale;

public enum ACFMinecraftMessage {

    INVALID_WORLD(CC.RED + "Error: That world does not exist."),
    YOU_MUST_BE_HOLDING_ITEM(CC.RED + "Error: You must be holding an item in your main hand."),
    PLAYER_IS_VANISHED_CONFIRM(CC.RED + "Warning: {vanished} is vanished. Do not blow their cover!\nTo confirm your action add :confirm to the end of their name.\nEx: {vanished}:confirm"),
    USERNAME_TOO_SHORT(CC.RED + "Error: Username too short, must be at least three characters."),
    IS_NOT_A_VALID_NAME(CC.RED + "Error: " + CC.YELLOW + "{name}" + CC.RED + " is not a valid username."),
    MULTIPLE_PLAYERS_MATCH(CC.RED + "Error: Multiple players matched " + CC.YELLOW + "{search} ({all})" + CC.RED + ", please be more specific."),
    NO_PLAYER_FOUND_SERVER(CC.RED + "No player matching " + CC.YELLOW + "{search}" + CC.RED + " is connected to the server."),
    NO_PLAYER_FOUND_OFFLINE(CC.RED + "No player matching " + CC.YELLOW + "{search}" + CC.RED + " could be found."),
    NO_PLAYER_FOUND(CC.RED + "No player matching " + CC.YELLOW + "{search}" + CC.RED + " could be found."),
    LOCATION_PLEASE_SPECIFY_WORLD(CC.RED + "Error: Please specify world. Example: world:x,y,z."),
    LOCATION_PLEASE_SPECIFY_XYZ(CC.RED + "Error: Please specify the coordinates x, y, and z. Example: world:x,y,z."),
    LOCATION_CONSOLE_NOT_RELATIVE(CC.RED + "Error: Console may not use relative coordinates for location.");

    private final String message;

    private ACFMinecraftMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
