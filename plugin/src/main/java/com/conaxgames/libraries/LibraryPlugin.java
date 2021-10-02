package com.conaxgames.libraries;

import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.board.BoardManager;
import com.conaxgames.libraries.commands.CommandRegistry;
import com.conaxgames.libraries.event.impl.LibraryPluginEnableEvent;
import com.conaxgames.libraries.hooks.HookManager;
import com.conaxgames.libraries.listener.PlayerListener;
import com.conaxgames.libraries.task.AutoUpdaterTask;
import com.conaxgames.libraries.timer.TimerManager;
import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.License;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryPlugin extends JavaPlugin {

    private static LibraryPlugin instance;
    private TimerManager timerManager;
    private PaperCommandManager paperCommandManager;
    private CommandRegistry commandRegistry;
    private BoardManager boardManager;
    private HookManager hookManager;
    private Settings settings = null;

    public static LibraryPlugin getInstance() {
        return LibraryPlugin.instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        try {
            settings = new Settings();
        } catch (Exception e) {
            getLogger().info("Settings handler is null lol");
        }

        if(!new License(this.getSettings().license, "https://conaxgames.com/license/verify.php", this).register()) return;

        long start = System.currentTimeMillis();
        getLogger().info("Attempting to load utilities...");

        this.hookManager = new HookManager(this);
        this.timerManager = new TimerManager();
        this.paperCommandManager = new PaperCommandManager(this);
        this.commandRegistry = new CommandRegistry(paperCommandManager);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(hookManager, this);

        long finish = System.currentTimeMillis();
        getLogger().info("Successfully hooked into " + getHooked().size() + " plugin" + (getHooked().size() == 1 ? "" : "s") + " and loaded utilities in " + (finish - start) + " ms.");

        new LibraryPluginEnableEvent().call();
    }

    @Override
    public void onDisable() {
        if (this.getSettings().autoupdate) {
            new AutoUpdaterTask().run();
        }
        super.onDisable();
    }

    public List<Plugin> getHooked() {
        List<Plugin> libraryPluginList = new ArrayList<>();

        List<Plugin> bukkitPluginList = new ArrayList<>(Arrays.asList(Bukkit.getPluginManager().getPlugins()));

        for (Plugin plugin : bukkitPluginList) {
            if (plugin.getDescription().getDepend().contains("cLibraries")) {
                libraryPluginList.add(plugin);
            }
        }

        return libraryPluginList;
    }

    public void sendConsoleMessage(String msg, ChatColor color) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(color + "[cLibraries] " + msg));
    }

    public void sendConsoleMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage(CC.translate("[cLibraries] " + msg));
    }

    public Settings getSettings() {
        return settings;
    }

    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;

        long interval = this.boardManager.getAdapter().getInterval();

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.boardManager, 0L, interval);
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

    public void sendDebug(String action, String msg) {
        if (this.settings.debug) {
            Bukkit.getConsoleSender().sendMessage(CC.PRIMARY + "[cSuite Debug] " + CC.GRAY + "Action Code: " + CC.SECONDARY + action + " " + CC.GRAY + "Message: " + CC.SECONDARY + msg);
        }
    }
}
