package com.conaxgames.libraries;

import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.board.BoardManager;
import com.conaxgames.libraries.commands.CommandRegistry;
import com.conaxgames.libraries.debug.LibraryLogger;
import com.conaxgames.libraries.event.impl.LibraryPluginEnableEvent;
import com.conaxgames.libraries.hooks.HookManager;
import com.conaxgames.libraries.inventoryui.UIListener;
import com.conaxgames.libraries.listener.PlayerListener;
import com.conaxgames.libraries.nms.LibNMSManager;
import com.conaxgames.libraries.timer.TimerManager;
import com.conaxgames.libraries.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class LibraryPlugin {

    private static LibraryPlugin instance;
    private JavaPlugin plugin;

    private LibraryLogger libraryLogger;
    private TimerManager timerManager;
    private PaperCommandManager paperCommandManager;
    private CommandRegistry commandRegistry;
    private BoardManager boardManager;
    private HookManager hookManager;
    private Settings settings = null;

    public static LibraryPlugin getInstance() {
        return LibraryPlugin.instance;
    }

    public LibraryPlugin setup(JavaPlugin plugin, String debugPrimary, String debugSecondary) {
        this.plugin = plugin;
        instance = this;

        // determine the server version before we load other utility classes.
        LibNMSManager.getInstance();

        try {
            settings = new Settings();
        } catch (Exception e) {
            this.plugin.getLogger().info("------------------------------------------------------------------");
            this.plugin.getLogger().info(" ");
            this.plugin.getLogger().info("cLibraries settings were unable to load!");
            this.plugin.getLogger().info(" ");
            this.plugin.getLogger().info("------------------------------------------------------------------");
        }

        long start = System.currentTimeMillis();

        this.libraryLogger = new LibraryLogger(plugin, debugPrimary, debugSecondary);
        this.hookManager = new HookManager(this);
        this.timerManager = new TimerManager();
        this.paperCommandManager = new PaperCommandManager(this.plugin);
        this.commandRegistry = new CommandRegistry(paperCommandManager);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this.plugin);
        Bukkit.getPluginManager().registerEvents(new UIListener(), this.plugin);
        Bukkit.getPluginManager().registerEvents(this.hookManager, this.plugin);

        long finish = System.currentTimeMillis();
        if (LibraryPlugin.getInstance().getSettings().debug) {
            this.plugin.getLogger().info("Successfully hooked into " + getHooked().size() + " plugin" + (getHooked().size() == 1 ? "" : "s") + " and loaded utilities in " + (finish - start) + " ms.");
        }
        new LibraryPluginEnableEvent().call();

        return this;
    }

    public List<Plugin> getHooked() {
        List<Plugin> libraryPluginList = new ArrayList<>();
        Plugin[] bukkitPluginList = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : bukkitPluginList) {
            if (plugin.getDescription().getDepend().contains("cLibraries")) {
                libraryPluginList.add(plugin);
            }
        }

        return libraryPluginList;
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

    public TimerManager getTimerManager() {
        return this.timerManager;
    }

    public PaperCommandManager getPaperCommandManager() {
        return this.paperCommandManager;
    }

    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    public BoardManager getBoardManager() {
        return this.boardManager;
    }

    public HookManager getHookManager() {
        return this.hookManager;
    }

    public void setTimerManager(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    public void setPaperCommandManager(PaperCommandManager paperCommandManager) {
        this.paperCommandManager = paperCommandManager;
    }

    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public void setHookManager(HookManager hookManager) {
        this.hookManager = hookManager;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public LibraryLogger getLibraryLogger() {
        return libraryLogger;
    }
}
