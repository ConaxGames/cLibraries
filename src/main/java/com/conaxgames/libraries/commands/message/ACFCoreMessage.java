package com.conaxgames.libraries.commands.message;

import com.conaxgames.libraries.message.CC;

public enum ACFCoreMessage {

    UNKNOWN_COMMAND(CC.translate("&cUnknown command, please type /help.")),
    INVALID_SYNTAX(CC.translate("&cUsage: {command} {syntax}")),
    HELP_PAGE_INFORMATION(CC.translate("&eShowing page &6{page}&e of &6{totalpages}&7 ({results} results)&e.")),
    HELP_NO_RESULTS(CC.translate("&cError: No more results.")),
    HELP_HEADER(CC.translate("&6=== &eShowing help for &f{commandprefix}{command}&6 ===")),
    HELP_FORMAT(CC.translate("&e{commandprefix}{command} &6{parameters}&7 {separator} {description}")),
    HELP_DETAILED_HEADER(CC.translate("&6=== &eShowing detailed help for &f{commandprefix}{command}&6 ===")),
    HELP_DETAILED_COMMAND_FORMAT(CC.translate("&e{commandprefix}{command} &6{parameters}&7 {separator} {description}")),
    HELP_DETAILED_PARAMETER_FORMAT(CC.translate("&e{syntaxorname}: &7{description}")),
    HELP_SEARCH_HEADER(CC.translate("&6=== &eSearch results for &f{commandprefix}{command} {search}&6 ==="));

    private final String message;

    ACFCoreMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
