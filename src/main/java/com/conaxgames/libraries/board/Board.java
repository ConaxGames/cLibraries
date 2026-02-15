package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

    private static final String OBJECTIVE_NAME = "sb";
    private static final String[] KEYS;
    static {
        ChatColor[] colors = ChatColor.values();
        KEYS = new String[colors.length];
        for (int i = 0; i < colors.length; i++) {
            KEYS[i] = colors[i].toString() + ChatColor.WHITE;
        }
    }

    private final BoardAdapter adapter;
    private final Player player;
    private final List<BoardEntry> entries = new ArrayList<>();
    private final Map<String, BoardTimer> timers = new HashMap<>();
    private final Map<String, String> usedKeys = new HashMap<>();
    private Scoreboard scoreboard;
    private Objective objective;

    public Board(Player player, BoardAdapter adapter) {
        this.adapter = adapter;
        this.player = player;
        org.bukkit.scoreboard.ScoreboardManager sm = LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager();
        this.scoreboard = player.getScoreboard().equals(sm.getMainScoreboard())
            ? sm.getNewScoreboard()
            : player.getScoreboard();
        Component title = LegacyComponentSerializer.legacySection().deserialize(adapter.getTitle(player));
        this.objective = this.scoreboard.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public String getNewKey(BoardEntry entry) {
        String text = entry.getText();
        String suffix = text.length() > 16 ? ChatColor.getLastColors(text.substring(0, 16)) : "";
        for (String base : KEYS) {
            String key = base + suffix;
            if (!usedKeys.containsKey(key)) {
                usedKeys.put(key, text);
                return key;
            }
        }
        throw new IllegalStateException("No free board entry keys");
    }

    public List<String> getBoardEntriesFormatted() {
        List<String> out = new ArrayList<>(entries.size());
        for (BoardEntry e : entries) out.add(e.getText());
        return out;
    }

    public BoardEntry getByPosition(int position) {
        return position >= 0 && position < entries.size() ? entries.get(position) : null;
    }

    public BoardTimer getTimer(String id) {
        BoardTimer t = timers.get(id);
        if (t == null || t.isExpired()) {
            if (t != null) timers.remove(id);
            return null;
        }
        return t;
    }

    public Map<String, BoardTimer> getTimers() {
        timers.entrySet().removeIf(e -> e.getValue().isExpired());
        return new HashMap<>(timers);
    }

    public void addTimer(BoardTimer timer) {
        timers.put(timer.getId(), timer);
    }

    public void removeTimer(String id) {
        timers.remove(id);
    }

    public void clearAllEntries() {
        for (BoardEntry e : entries) e.remove();
        entries.clear();
        usedKeys.clear();
    }

    public BoardAdapter getAdapter() { return adapter; }
    public Player getPlayer() { return player; }
    public List<BoardEntry> getEntries() { return entries; }
    public Map<String, String> getUsedKeys() { return usedKeys; }
    public Scoreboard getScoreboard() { return scoreboard; }
    public Objective getObjective() { return objective; }
}
