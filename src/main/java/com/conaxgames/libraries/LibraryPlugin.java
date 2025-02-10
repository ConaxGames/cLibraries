package com.conaxgames.libraries;

import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.board.BoardManager;
import com.conaxgames.libraries.commands.CommandRegistry;
import com.conaxgames.libraries.debug.LibraryLogger;
import com.conaxgames.libraries.event.impl.LibraryPluginEnableEvent;
import com.conaxgames.libraries.hooks.HookManager;
import com.conaxgames.libraries.listener.PlayerListener;
import com.conaxgames.libraries.module.ModuleManager;
import com.conaxgames.libraries.timer.TimerManager;
import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
@Setter
public class LibraryPlugin {

    @Getter @Setter
    private static LibraryPlugin instance;

    /*
     * The plugin can only be setup once due to managers and listeners being registered.
     * This can be called
     */
    private boolean setup;

    private JavaPlugin plugin;

    private LibraryLogger libraryLogger;
    private TimerManager timerManager;
    private PaperCommandManager paperCommandManager;
    private CommandRegistry commandRegistry;
    private BoardManager boardManager;
    private HookManager hookManager;
    private ModuleManager moduleManager;
    private Settings settings = null;

    public static LibraryPlugin getInstance() {
        if (instance == null) {
            throw new IllegalPluginAccessException("cLibraries is not registered.");
        }
        return instance;
    }

    public LibraryPlugin onEnable(JavaPlugin plugin, String debugPrimary, String debugSecondary, String moduleCommandAlias, String moduleCommandPerm) {
        if (this.setup) {
            String authors = Joiner.on(", ").join(this.plugin.getDescription().getAuthors());
            libraryLogger.toConsole(
                    "cLibraries",
                    Arrays.asList(
                            " ",
                            "cLibraries is already setup!", this.plugin.getName() + " has called onEnable twice!",
                            "Please nag " + authors + " to fix this!",
                            " ")
            );
            return this;
        }

        instance = this;
        this.plugin = plugin;

        try {
            settings = new Settings();
        } catch (Exception e) {
            Bukkit.getLogger().info(" ");
            Bukkit.getLogger().info("cLibraries settings were unable to load!");
            Bukkit.getLogger().info(" ");
        }

        this.libraryLogger = new LibraryLogger(plugin, debugPrimary, debugSecondary);

        this.paperCommandManager = new PaperCommandManager(this.plugin);
        this.commandRegistry = new CommandRegistry(this, paperCommandManager);

        this.hookManager = new HookManager(this);
        this.timerManager = new TimerManager();
        this.moduleManager = new ModuleManager(this, moduleCommandAlias, moduleCommandPerm);

        Arrays.asList(
                new PlayerListener(this),
                this.hookManager
        ).forEach(l -> Bukkit.getPluginManager().registerEvents(l, this.plugin));

        new LibraryPluginEnableEvent().call();
        this.setup = true;

        String authors = Joiner.on(", ").join(this.plugin.getDescription().getAuthors());
        this.libraryLogger.toConsole("cLibraries",
                Arrays.asList(
                        " ",
                        "cLibraries instance has been setup for " + this.plugin.getName() + " (" + authors + ")",
                        " ")
        );
        return this;
    }

    public LibraryPlugin onDisable() {
        this.moduleManager.disableAllModules();
        return this;
    }

    public Settings getSettings() {
        return settings;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;
        long interval = this.boardManager.getAdapter().getInterval();
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this.boardManager, 0L, interval);
    }

}
