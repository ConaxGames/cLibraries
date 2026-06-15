package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.message.CC;
import com.conaxgames.libraries.util.VersioningChecker;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
final class Board {

    static final int SEGMENT_MAX = VersioningChecker.getInstance().isServerVersionBefore("1.13") ? 16 : 64;
    static final int TITLE_MAX = VersioningChecker.getInstance().isServerVersionBefore("1.18") ? 32 : 1024;

    private static final String[] ENTRY_KEYS;

    static {
        var codes = "0123456789abcdefklmor";
        ENTRY_KEYS = new String[codes.length()];
        for (int i = 0; i < codes.length(); i++) {
            ENTRY_KEYS[i] = "\u00a7" + codes.charAt(i) + "\u00a7f";
        }
    }

    private final List<BoardEntry> entries = new ArrayList<>();
    private final Scoreboard scoreboard;
    private final Objective objective;
    private String lastTitle;

    Board(Player player) {
        var scoreboardManager = LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager();
        this.scoreboard = player.getScoreboard().equals(scoreboardManager.getMainScoreboard())
                ? scoreboardManager.getNewScoreboard()
                : player.getScoreboard();

        var existing = scoreboard.getObjective("sb");
        if (existing != null) {
            existing.unregister();
        }
        this.objective = scoreboard.registerNewObjective("sb", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    static String entryKey(int index) {
        return ENTRY_KEYS[index];
    }

    Scoreboard scoreboard() {
        return scoreboard;
    }

    void updateTitle(String raw) {
        var translated = CC.translate(raw != null ? raw : "");
        var clipped = translated.length() <= TITLE_MAX ? translated : translated.substring(0, TITLE_MAX);
        if (clipped.equals(lastTitle)) {
            return;
        }
        lastTitle = clipped;
        objective.setDisplayName(clipped);
    }

    Objective objective() {
        return objective;
    }

    List<BoardEntry> entries() {
        return entries;
    }
}
