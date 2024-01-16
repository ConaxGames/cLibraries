package com.conaxgames.libraries.debug;

import com.conaxgames.libraries.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class LibraryLogger {

    private final String pluginPrefix;
    private final String primary;
    private final String secondary;
    private final String padding = "  ";

    public LibraryLogger(JavaPlugin javaPlugin, String primary, String secondary) {
        this.pluginPrefix = javaPlugin.getName();
        this.primary = primary;
        this.secondary = secondary;
    }

    public void toConsole(String action, String message, Throwable... throwables) {
        toConsole(action, Collections.singletonList(message), throwables);
    }

    public void toConsole(String action, List<String> message, Throwable... throwables) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        if (message.size() == 1) {
            console.sendMessage(CC.translate(this.primary + "[" + pluginPrefix + "] " + CC.GRAY + action + ": " + CC.SECONDARY + message.get(0)));
        } else {
            console.sendMessage(CC.translate(this.primary + "[" + pluginPrefix + "] " + CC.GRAY + action + ": "));
            message.forEach(line -> console.sendMessage(CC.translate(this.primary + padding + "| " + this.secondary + line)));
        }

        for (int i = 0; i < throwables.length; i++) {
            Throwable thr = throwables[i];
            if (i != 0) console.sendMessage(" ");
            console.sendMessage(CC.translate(this.primary + "------------------------------------------------------------------"));
            console.sendMessage(CC.translate(this.secondary + padding + "| Stacktrace: " + thr.getClass().getName()));
            if (thr.getMessage() != null) {
                thr.printStackTrace();
                for (String stackLine : thr.getMessage().split("\n")) {
                    console.sendMessage(CC.translate(this.primary + padding + "| " + CC.GRAY + stackLine));
                }
            } else {
                thr.printStackTrace();
            }
            console.sendMessage(this.primary + "------------------------------------------------------------------");
        }
    }

}
