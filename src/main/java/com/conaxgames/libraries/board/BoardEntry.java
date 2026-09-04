package com.conaxgames.libraries.board;

import com.conaxgames.libraries.message.CC;
import org.bukkit.scoreboard.Team;

@SuppressWarnings("deprecation")
final class BoardEntry {

    private final Board board;
    private final String key;
    private final Team team;
    String text;
    private String lastSent;

    BoardEntry(Board board, int index, String text) {
        this.board = board;
        this.text = text;
        this.key = Board.MODERN ? Integer.toString(index) : Board.ENTRY_KEYS[index];
        if (Board.MODERN) {
            this.team = null;
        } else {
            this.team = board.scoreboard.registerNewTeam("board_" + index);
            team.addEntry(key);
        }
    }

    void send(int position) {
        var score = board.objective.getScore(key);
        if (score.getScore() != position) {
            score.setScore(position);
        }
        if (text.equals(lastSent)) {
            return;
        }
        lastSent = text;

        var translated = CC.translate(text);
        if (Board.MODERN) {
            var component = Board.Legacy.SERIALIZER.deserialize(translated);
            score.customName(Board.TEXT_SHADOW ? component.shadowColor(Board.Legacy.SHADOW) : component);
            return;
        }

        int max = Board.SEGMENT_MAX;
        String prefix;
        String suffix;
        if (translated.length() <= max) {
            prefix = translated;
            suffix = "";
        } else {
            prefix = translated.substring(0, max);
            int lastColor = prefix.lastIndexOf('\u00a7');
            if (lastColor >= max - 2) {
                suffix = CC.getLastColors(translated.substring(0, Math.min(translated.length(), max + 1)))
                        + translated.substring(lastColor + 2);
                prefix = prefix.substring(0, lastColor);
            } else {
                suffix = CC.getLastColors(prefix) + translated.substring(max);
            }
            if (suffix.length() > max) {
                suffix = suffix.substring(0, max);
            }
        }

        if (!prefix.equals(team.getPrefix())) {
            team.setPrefix(prefix);
        }
        if (!suffix.equals(team.getSuffix())) {
            team.setSuffix(suffix);
        }
    }

    void remove() {
        board.scoreboard.resetScores(key);
        if (team != null) {
            team.unregister();
        }
    }
}
