package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

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

    @Getter
    private final List<BoardEntry> entries = new ArrayList<>();
    private final Map<String, BoardTimer> timers = new HashMap<>();
    @Getter
    private final Map<String, String> usedKeys = new HashMap<>();
    @Getter
    private final Scoreboard scoreboard;
    @Getter
    private final Objective objective;

    public Board(Player player, BoardAdapter adapter) {
        ScoreboardManager sm = LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager();
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

    public BoardTimer getTimer(String id) {
        BoardTimer t = timers.get(id);
        if (t == null || t.isExpired()) {
            if (t != null) timers.remove(id);
            return null;
        }
        return t;
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
}
