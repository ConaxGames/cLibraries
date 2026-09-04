package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.message.CC;
import com.conaxgames.libraries.util.VersioningChecker;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
final class Board {

    static final boolean MODERN = !VersioningChecker.getInstance().isServerVersionBefore("1.20.4");
    static final boolean TEXT_SHADOW = !VersioningChecker.getInstance().isServerVersionBefore("1.21.4");
    static final int SEGMENT_MAX = VersioningChecker.getInstance().isServerVersionBefore("1.13") ? 16 : 64;
    static final int TITLE_MAX = VersioningChecker.getInstance().isServerVersionBefore("1.13") ? 32 : 128;

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
        if (MODERN) {
            this.objective = scoreboard.registerNewObjective("sb", Criteria.DUMMY, Component.empty());
            objective.numberFormat(NumberFormat.blank());
        } else {
            this.objective = scoreboard.registerNewObjective("sb", "dummy");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    static String entryKey(int index) {
        return MODERN ? Integer.toString(index) : ENTRY_KEYS[index];
    }

    static int maxLines() {
        return ENTRY_KEYS.length;
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
        if (MODERN) {
            var component = Legacy.SERIALIZER.deserialize(clipped);
            objective.displayName(TEXT_SHADOW ? component.shadowColor(Legacy.SHADOW) : component);
        } else {
            objective.setDisplayName(clipped);
        }
    }

    Objective objective() {
        return objective;
    }

    List<BoardEntry> entries() {
        return entries;
    }

    static final class Legacy {
        static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
                .character(LegacyComponentSerializer.SECTION_CHAR)
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
        static final ShadowColor SHADOW = ShadowColor.shadowColor(0xFF000000);
    }
}
