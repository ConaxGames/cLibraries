package com.conaxgames.libraries.commands;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.LibraryPlugin;

import java.util.Locale;

public class CommandMessages {

    /**
     * Updates the format of the default ACF chat messages. To use these messages,
     * simply instantiate 'CommandMessages' when registering the PaperCommandManager.
     * @param commandManager - PaperCommandManager
     */
    public CommandMessages(PaperCommandManager commandManager) {
        Locale locale = new Locale("en");

        LibraryPlugin.getInstance().getSettings().acfMessages.forEach((a,s) -> {
            commandManager.getLocales().addMessage(locale, MessageKeys.valueOf(a.name()), s.replace("&", "§"));
        });
//        for (ACFMinecraftMessage message : ACFMinecraftMessage.values()) {
//            Locale locale = new Locale("en");
//            commandManager.getLocales().addMessage(locale, MessageKeys.valueOf(message.name()), message.getMessage().replace("&", "§"));
//        }
    }
}
