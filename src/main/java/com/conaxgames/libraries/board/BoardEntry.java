package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.CC;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.regex.Pattern;

/**
 * A single line on a {@link Board}. Uses a scoreboard team (prefix + suffix) to show text that may
 * exceed 16 characters; the line is split with color codes preserved. Created by the board's
 * {@link Board#getNewKey} and managed by {@link BoardManager} during {@link BoardAdapter#getScoreboard} sync.
 */
@Accessors(chain = true)
public class BoardEntry {

    private static final Pattern INVALID_TEAM_CHARS = Pattern.compile("[^a-zA-Z0-9_.-]");
    private static final int MAX_TEAM_NAME = 16;
    private static final int MAX_LINE = 16;
    private static final int MAX_PREFIX = 64;
    private static final int MAX_SUFFIX = 64;
    private static final String TEAM_PREFIX = "sb_";
    private static int teamCounter = 0;

    @Getter private final Board board;
    @Getter private final String key;
    @Getter private Team team;
    @Getter private String text;
    private String[] cachedSplit;

    /**
     * Creates an entry on the board with the given text. Allocates a key and registers the team.
     */
    public BoardEntry(Board board, String text) {
        this.board = board;
        this.text = text != null ? text : "";
        this.key = board.getNewKey(this);
        this.setup();
    }

    /**
     * Registers the team on the scoreboard if not already done and adds this entry to the board's list.
     * Chainable. Called after construction and when text changes and the team needs to exist.
     */
    public BoardEntry setup() {
        if (this.team == null) {
            Scoreboard sb = board.getScoreboard();
            String name = key.length() > MAX_TEAM_NAME ? key.substring(0, MAX_TEAM_NAME) : key;
            name = INVALID_TEAM_CHARS.matcher(name).replaceAll("");
            if (name.isEmpty()) name = TEAM_PREFIX + (++teamCounter);
            this.team = sb.registerNewTeam(name);
            this.team.addEntry(this.key);
            if (!board.getEntries().contains(this)) board.getEntries().add(this);
        }
        return this;
    }

    /**
     * Updates the team prefix/suffix and score to match current text and position. Called by the
     * manager each tick for each visible line. Position is the sidebar score (1 = bottom).
     */
    public BoardEntry send(int position) {
        Objective obj = board.getObjective();
        String[] split = getSplitText();
        String prefix = split[0].length() > MAX_PREFIX ? split[0].substring(0, MAX_PREFIX) : split[0];
        String suffix = split[1].length() > MAX_SUFFIX ? split[1].substring(0, MAX_SUFFIX) : split[1];
        if (!prefix.equals(team.getPrefix())) team.setPrefix(prefix);
        if (!suffix.equals(team.getSuffix())) team.setSuffix(suffix);
        Score score = obj.getScore(this.key);
        if (score.getScore() != position) score.setScore(position);
        return this;
    }

    /**
     * Removes the entry from the scoreboard, frees the key, and unregisters the team.
     */
    public void remove() {
        board.getUsedKeys().remove(this.key);
        board.getScoreboard().resetScores(this.key);
        if (team != null) {
            try {
                if (team.getEntries().contains(this.key)) team.removeEntry(this.key);
                if (team.getEntries().isEmpty()) team.unregister();
            } catch (IllegalStateException ignored) {}
        }
    }

    private String[] getSplitText() {
        if (cachedSplit == null) {
            cachedSplit = splitText(CC.translate(text));
        }
        return cachedSplit;
    }

    private String[] splitText(String input) {
        if (input == null || input.length() <= MAX_LINE) {
            return new String[]{ input != null ? input : "", "" };
        }
        String prefix = input.substring(0, MAX_LINE);
        int lastColor = prefix.lastIndexOf(ChatColor.COLOR_CHAR);
        String suffix;
        if (lastColor >= 14) {
            prefix = prefix.substring(0, lastColor);
            suffix = ChatColor.getLastColors(input.substring(0, 17)) + input.substring(lastColor + 2);
        } else {
            suffix = ChatColor.getLastColors(prefix) + input.substring(MAX_LINE);
        }
        return new String[]{ prefix, suffix };
    }

    /**
     * Sets the display text and invalidates the cached split. Chainable. Next {@link #send} will
     * push the new text to the client.
     */
    public BoardEntry setText(String text) {
        if (text != null && !this.text.equals(text)) {
            this.text = text;
            this.cachedSplit = null;
        }
        return this;
    }
}
