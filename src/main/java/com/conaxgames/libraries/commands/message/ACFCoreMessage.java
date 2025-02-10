package com.conaxgames.libraries.commands.message;

import com.conaxgames.libraries.util.CC;

import java.util.Locale;

public enum ACFCoreMessage {

    UNKNOWN_COMMAND(CC.RED + "Unknown command, please type /help."),
    INVALID_SYNTAX(CC.RED + "Usage: {command} {syntax}"),
    HELP_PAGE_INFORMATION(CC.PRIMARY + "Showing page " + CC.SECONDARY + "{page}" + CC.PRIMARY + " of " + CC.SECONDARY + "{totalpages}" + CC.GRAY + " ({results} results)" + CC.PRIMARY + "."),
    HELP_NO_RESULTS(CC.RED + "Error: No more results."),
    HELP_HEADER(CC.SECONDARY + "=== " + CC.PRIMARY + "Showing help for " + CC.WHITE + "{commandprefix}{command}" + CC.SECONDARY + " ==="),
    HELP_FORMAT(CC.PRIMARY + "{commandprefix}{command} " + CC.SECONDARY + "{parameters}" + CC.GRAY + " {separator} {description}"),
    HELP_DETAILED_HEADER(CC.SECONDARY + "=== " + CC.PRIMARY + "Showing detailed help for " + CC.WHITE + "{commandprefix}{command}" + CC.SECONDARY + " ==="),
    HELP_DETAILED_COMMAND_FORMAT(CC.PRIMARY + "{commandprefix}{command} " + CC.SECONDARY + "{parameters}" + CC.GRAY + " {separator} {description}"),
    HELP_DETAILED_PARAMETER_FORMAT(CC.PRIMARY + "{syntaxorname}: " + CC.GRAY + "{description}"),
    HELP_SEARCH_HEADER(CC.SECONDARY + "=== " + CC.PRIMARY + "Search results for " + CC.WHITE + "{commandprefix}{command} {search}" + CC.SECONDARY + " ===");

    private final String message;

    ACFCoreMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
