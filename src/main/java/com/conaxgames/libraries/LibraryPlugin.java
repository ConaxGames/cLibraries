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
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * Core library plugin class that manages the initialization and lifecycles
 * of all library components.
 */
@Getter
public class LibraryPlugin {

    // Singleton instance
    private static LibraryPlugin instance;
    
    // Core properties
    private JavaPlugin plugin;
    private boolean setup;
    private Settings settings;
    
    // Managers
    private LibraryLogger libraryLogger;
    private TimerManager timerManager;
    private PaperCommandManager paperCommandManager;
    private CommandRegistry commandRegistry;
    private BoardManager boardManager;
    private HookManager hookManager;
    private ModuleManager moduleManager;

    /**
     * Gets the singleton instance of the LibraryPlugin.
     * 
     * @return The LibraryPlugin instance
     * @throws IllegalPluginAccessException if the library is not registered
     */
    public static LibraryPlugin getInstance() {
        if (instance == null) {
            throw new IllegalPluginAccessException("cLibraries is not registered.");
        }
        return instance;
    }

    /**
     * Initializes the LibraryPlugin with the provided plugin instance and configuration.
     * This should only be called once during plugin initialization.
     *
     * @param plugin The JavaPlugin instance
     * @param debugPrimary The primary debug configuration
     * @param debugSecondary The secondary debug configuration
     * @param moduleCommandAlias The command alias for module commands
     * @param moduleCommandPerm The permission for module commands
     * @return This LibraryPlugin instance for chaining
     */
    public LibraryPlugin onEnable(JavaPlugin plugin, String debugPrimary, String debugSecondary, 
                                 String moduleCommandAlias, String moduleCommandPerm) {
        // Prevent multiple initialization
        if (this.setup) {
            logMultipleInitializationWarning();
            return this;
        }

        // Set instance and plugin
        instance = this;
        this.plugin = plugin;

        // Initialize settings
        initializeSettings();
        
        // Initialize core components
        this.libraryLogger = new LibraryLogger(plugin, debugPrimary, debugSecondary);
        this.paperCommandManager = new PaperCommandManager(this.plugin);
        this.commandRegistry = new CommandRegistry(this, paperCommandManager);
        this.hookManager = new HookManager(this);
        this.timerManager = new TimerManager();
        this.moduleManager = new ModuleManager(this, moduleCommandAlias, moduleCommandPerm);

        // Register event listeners
        registerEventListeners();

        // Complete initialization
        new LibraryPluginEnableEvent().call();
        this.setup = true;
        logSuccessfulInitialization();
        
        return this;
    }

    /**
     * Disables the plugin and all modules.
     * 
     * @return This LibraryPlugin instance for chaining
     */
    public LibraryPlugin onDisable() {
        // Shutdown board manager if it exists
        if (this.boardManager != null) {
            this.boardManager.shutdown();
        }
        
        this.moduleManager.disableAllModules();
        return this;
    }

    /**
     * Sets the board manager and schedules its task.
     * 
     * @param boardManager The board manager instance
     */
    	public void setBoardManager(BoardManager boardManager) {
		this.boardManager = boardManager;
		long interval = this.boardManager.getAdapter().getInterval();
		// Run synchronously to avoid thread safety issues with scoreboard modifications
		this.plugin.getServer().getScheduler().runTaskTimer(
			this.plugin, this.boardManager, 0L, interval
		);
	}

    // Private helper methods

    private void initializeSettings() {
        try {
            settings = new Settings();
        } catch (Exception e) {
            Bukkit.getLogger().info(" ");
            Bukkit.getLogger().info("cLibraries settings were unable to load!");
            Bukkit.getLogger().info(" ");
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
