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
import com.conaxgames.libraries.util.scheduler.Scheduler;
import com.conaxgames.libraries.util.scheduler.BukkitScheduler;
import com.conaxgames.libraries.util.scheduler.FoliaScheduler;
import com.google.common.base.Joiner;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;

/**
 * Main library plugin class for cLibraries framework.
 * Provides scheduling, commands, modules, timers, and hooks.
 */
@Getter
public class LibraryPlugin {

    private static LibraryPlugin instance;
    private JavaPlugin plugin;
    private boolean setup;
    private Settings settings;
    private LibraryLogger libraryLogger;
    private TimerManager timerManager;
    private PaperCommandManager paperCommandManager;
    private CommandRegistry commandRegistry;
    private BoardManager boardManager;
    private HookManager hookManager;
    private ModuleManager moduleManager;
    private Scheduler scheduler;

    /**
     * Gets the library instance.
     * @return LibraryPlugin instance
     * @throws IllegalPluginAccessException if not initialized
     */
    public static LibraryPlugin getInstance() {
        if (instance == null) {
            throw new IllegalPluginAccessException("cLibraries is not registered.");
        }
        return instance;
    }

    /**
     * Initializes the library.
     * @param plugin Main plugin instance
     * @param debugPrimary Primary debug key
     * @param debugSecondary Secondary debug key
     * @param moduleCommandAlias Module command alias
     * @param moduleCommandPerm Module command permission
     * @return This instance for chaining
     */
    public LibraryPlugin onEnable(JavaPlugin plugin, String debugPrimary, String debugSecondary, 
                                 String moduleCommandAlias, String moduleCommandPerm) {
        if (this.setup) {
            logMultipleInitializationWarning();
            return this;
        }

        instance = this;
        this.plugin = plugin;
        initializeSettings();
        
        this.libraryLogger = new LibraryLogger(plugin, debugPrimary, debugSecondary);
        this.paperCommandManager = new PaperCommandManager(this.plugin);
        this.commandRegistry = new CommandRegistry(this, paperCommandManager);
        this.hookManager = new HookManager(this);
        this.timerManager = new TimerManager();
        this.moduleManager = new ModuleManager(this, moduleCommandAlias, moduleCommandPerm);
        
        initializeScheduler();
        registerEventListeners();

        new LibraryPluginEnableEvent().call();
        this.setup = true;
        logSuccessfulInitialization();
        return this;
    }

    /**
     * Shuts down the library.
     * @return This instance for chaining
     */
    public LibraryPlugin onDisable() {
        this.moduleManager.disableAllModules();
        return this;
    }

    /**
     * Sets the board manager and starts its update task.
     * @param boardManager Board manager instance
     */
    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;
        long interval = this.boardManager.getAdapter().getInterval();
        this.scheduler.runTaskTimer(this.plugin, this.boardManager, 0L, interval);
    }

    private void initializeSettings() {
        try {
            settings = new Settings();
        } catch (Exception e) {
            Bukkit.getLogger().info(" ");
            Bukkit.getLogger().info("cLibraries settings were unable to load!");
            Bukkit.getLogger().info(" ");
        }
    }

    private void initializeScheduler() {
        boolean foliaFound = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            foliaFound = true;
        } catch (ClassNotFoundException ignored) {
        }
        
        if (foliaFound) {
            this.scheduler = new FoliaScheduler(this);
        } else {
            this.scheduler = new BukkitScheduler(this);
        }
    }

    private void registerEventListeners() {
        Arrays.asList(
            new PlayerListener(this),
            this.hookManager
        ).forEach(listener -> 
            Bukkit.getPluginManager().registerEvents(listener, this.plugin)
        );
    }

    private void logMultipleInitializationWarning() {
        String authors = Joiner.on(", ").join(this.plugin.getDescription().getAuthors());
        libraryLogger.toConsole(
            "cLibraries",
            Arrays.asList(
                " ",
                "cLibraries is already setup!", this.plugin.getName() + " has called onEnable twice!",
                "Please nag " + authors + " to fix this!",
                " "
            )
        );
    }

    private void logSuccessfulInitialization() {
        String authors = Joiner.on(", ").join(this.plugin.getDescription().getAuthors());
        this.libraryLogger.toConsole(
            "cLibraries",
            Arrays.asList(
                " ",
                "cLibraries instance has been setup for " + this.plugin.getName() + " (" + authors + ")",
                " "
            )
        );
    }
}
