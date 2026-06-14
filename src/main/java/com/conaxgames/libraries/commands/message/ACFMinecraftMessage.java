package com.conaxgames.libraries.commands.message;

import com.conaxgames.libraries.message.CC;

public enum ACFMinecraftMessage {

    INVALID_WORLD(CC.translate("&cError: That world does not exist.")),
    YOU_MUST_BE_HOLDING_ITEM(CC.translate("&cError: You must be holding an item in your main hand.")),
    PLAYER_IS_VANISHED_CONFIRM(CC.translate("&cWarning: {vanished} is vanished. Do not blow their cover!\nTo confirm your action add :confirm to the end of their name.\nEx: {vanished}:confirm")),
    USERNAME_TOO_SHORT(CC.translate("&cError: Username too short, must be at least three characters.")),
    IS_NOT_A_VALID_NAME(CC.translate("&cError: &e{name}&c is not a valid username.")),
    MULTIPLE_PLAYERS_MATCH(CC.translate("&cError: Multiple players matched &e{search} ({all})&c, please be more specific.")),
    NO_PLAYER_FOUND_SERVER(CC.translate("&cNo player matching &e{search}&c is connected to the server.")),
    NO_PLAYER_FOUND_OFFLINE(CC.translate("&cNo player matching &e{search}&c could be found.")),
    NO_PLAYER_FOUND(CC.translate("&cNo player matching &e{search}&c could be found.")),
    LOCATION_PLEASE_SPECIFY_WORLD(CC.translate("&cError: Please specify world. Example: world:x,y,z.")),
    LOCATION_PLEASE_SPECIFY_XYZ(CC.translate("&cError: Please specify the coordinates x, y, and z. Example: world:x,y,z.")),
    LOCATION_CONSOLE_NOT_RELATIVE(CC.translate("&cError: Console may not use relative coordinates for location."));

    private final String message;

    ACFMinecraftMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
