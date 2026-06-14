package com.conaxgames.libraries.board;

import com.conaxgames.libraries.message.CC;
import org.bukkit.scoreboard.Team;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("deprecation")
final class BoardEntry {

    private static final AtomicInteger TEAM_COUNTER = new AtomicInteger();

    private final Board board;
    private final String key;
    private final Team team;
    private String text;
    private String[] splitCache;

    BoardEntry(Board board, int index, String text) {
        this.board = board;
        this.text = text != null ? text : "";
        this.key = Board.entryKey(index);
        this.team = board.scoreboard().registerNewTeam("board_" + TEAM_COUNTER.getAndIncrement());
        team.addEntry(key);
    }

    void send(int position) {
        var split = split();
        int max = Board.SEGMENT_MAX;
        var prefix = split[0].length() <= max ? split[0] : split[0].substring(0, max);
        var suffix = split[1].length() <= max ? split[1] : split[1].substring(0, max);

        if (!prefix.equals(team.getPrefix())) {
            team.setPrefix(prefix);
        }
        if (!suffix.equals(team.getSuffix())) {
            team.setSuffix(suffix);
        }

        var score = board.objective().getScore(key);
        if (score.getScore() != position) {
            score.setScore(position);
        }
    }

    void remove() {
        board.scoreboard().resetScores(key);
        team.removeEntry(key);
        team.unregister();
    }

    void text(String newText) {
        if (newText != null && !text.equals(newText)) {
            text = newText;
            splitCache = null;
        }
    }

    private String[] split() {
        if (splitCache != null) {
            return splitCache;
        }
        var translated = CC.translate(text);
        int unit = Board.SEGMENT_MAX;

        if (translated.length() <= unit) {
            return splitCache = new String[]{translated, ""};
        }

        var prefix = translated.substring(0, unit);
        int lastColor = prefix.lastIndexOf('\u00a7');
        if (lastColor >= unit - 2) {
            var trimmed = prefix.substring(0, lastColor);
            int end = Math.min(translated.length(), unit + 1);
            return splitCache = new String[]{
                    trimmed,
                    CC.getLastColors(translated.substring(0, end)) + translated.substring(lastColor + 2)
            };
        }

        return splitCache = new String[]{
                prefix,
                CC.getLastColors(prefix) + translated.substring(unit)
        };
    }
}
