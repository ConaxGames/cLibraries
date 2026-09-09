package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.VersioningChecker;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

final class Board {

    static final boolean MODERN = !VersioningChecker.getInstance().isServerVersionBefore("1.20.4");
    static final boolean TEXT_SHADOW = !VersioningChecker.getInstance().isServerVersionBefore("1.21.4");
    static final int SEGMENT_MAX = VersioningChecker.getInstance().isServerVersionBefore("1.13") ? 16 : 64;
    static final int TITLE_MAX = VersioningChecker.getInstance().isServerVersionBefore("1.13") ? 32 : 128;
    static final String[] ENTRY_KEYS;
    static final int MAX_LINES;

    static {
        var codes = "0123456789abcdefklmor";
        ENTRY_KEYS = new String[codes.length()];
        for (int i = 0; i < codes.length(); i++) {
            ENTRY_KEYS[i] = "\u00a7" + codes.charAt(i) + "\u00a7f";
        }
        MAX_LINES = ENTRY_KEYS.length;
    }

    final List<BoardEntry> entries = new ArrayList<>();
    Scoreboard scoreboard;
    Objective objective;
    String lastTitle;
}
