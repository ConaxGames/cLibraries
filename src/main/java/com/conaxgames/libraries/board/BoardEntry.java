package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.VersioningChecker;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.regex.Pattern;

/**
 * Represents a single entry in a scoreboard board.
 * This class handles the creation, updating, and removal of scoreboard entries.
 * 
 * @author ConaxGames
 * @since 1.0
 */
@Accessors(chain = true)
public class BoardEntry {

    // Constants
    private static final Pattern INVALID_TEAM_CHARS = Pattern.compile("[^a-zA-Z0-9_.-]");
    private static final int MAX_TEAM_NAME_LENGTH = 16;
    private static final int MAX_TEXT_LENGTH = 16;
    private static final int MAX_PREFIX_LENGTH = 64;
    private static final int MAX_SUFFIX_LENGTH = 64;
    private static final String TEAM_NAME_PREFIX = "board_";
    private static int teamCounter = 0;

    // Getter methods
    // Instance fields
    @Getter
    private final Board board;
    @Getter
    private final String originalText;
    @Getter
    private final String key;
    
    @Getter
    private Team team;
    @Getter
    private String text;
    private String[] cachedSplitText;

    /**
     * Creates a new board entry with the specified text.
     * 
     * @param board The board this entry belongs to
     * @param text The text to display
     */
    public BoardEntry(Board board, String text) {
        this.board = board;
        this.text = text;
        this.originalText = text;
        this.key = board.getNewKey(this);
        this.setup();
    }

    /**
     * Sets up the team for this entry.
     * This method creates or reuses a team to display the entry text.
     * 
     * @return This board entry for method chaining
     */
    public BoardEntry setup() {
        if (this.team == null) {
            Scoreboard scoreboard = this.board.getScoreboard();
            String teamName = createValidTeamName();
            
            this.team = scoreboard.registerNewTeam(teamName);
            this.team.addEntry(this.key);

            if (!this.board.getEntries().contains(this)) {
                this.board.getEntries().add(this);
            }
        }

        return this;
    }

    /**
     * Creates a valid team name from the key.
     * 
     * @return A valid team name
     */
    private String createValidTeamName() {
        String teamName = this.key;
        
        // Team names must be 16 characters or less and cannot contain certain characters
        if (teamName.length() > MAX_TEAM_NAME_LENGTH) {
            teamName = teamName.substring(0, MAX_TEAM_NAME_LENGTH);
        }
        
        // Remove invalid characters for team names
        teamName = INVALID_TEAM_CHARS.matcher(teamName).replaceAll("");
        
        // Ensure team name is not empty after filtering
        if (teamName.isEmpty()) {
            teamName = TEAM_NAME_PREFIX + (++teamCounter);
        }
        
        return teamName;
    }

    /**
     * Sends this entry to the scoreboard at the specified position.
     * 
     * @param position The position to display this entry at
     * @return This board entry for method chaining
     */
    public BoardEntry send(int position) {
        Objective objective = board.getObjective();
        
        // Get split text (cached if available)
        String[] split = getSplitText();
        
        // Validate prefix and suffix lengths
        String prefix = split[0];
        String suffix = split[1];
        
        if (prefix.length() > MAX_PREFIX_LENGTH) {
            prefix = prefix.substring(0, MAX_PREFIX_LENGTH);
        }
        if (suffix.length() > MAX_SUFFIX_LENGTH) {
            suffix = suffix.substring(0, MAX_SUFFIX_LENGTH);
        }
        
        // Update team prefix/suffix if changed
        if (!prefix.equals(this.team.getPrefix())) {
            this.team.setPrefix(prefix);
        }
        if (!suffix.equals(this.team.getSuffix())) {
            this.team.setSuffix(suffix);
        }

        Score score = objective.getScore(this.key);
        if (score.getScore() != position) {
            score.setScore(position);
        }

        return this;
    }

    /**
     * Removes this entry from the scoreboard and cleans up associated resources.
     */
    public void remove() {
        this.board.getUsedKeys().remove(this.key);
        this.board.getScoreboard().resetScores(this.key);
        
        if (this.team != null) {
            try {
                if (this.team.getEntries().contains(this.key)) {
                    this.team.removeEntry(this.key);
                }
                if (this.team.getEntries().isEmpty()) {
                    this.team.unregister();
                }
            } catch (IllegalStateException e) {
                // Team might have been already removed
            }
        }
    }

    /**
     * Gets split text for display on the scoreboard.
     * This method handles text longer than 16 characters by splitting it appropriately.
     * 
     * @return Array containing [prefix, suffix]
     */
    private String[] getSplitText() {
        if (cachedSplitText == null) {
            String translatedText = CC.translate(text);
            cachedSplitText = splitText(translatedText);
        }
        return cachedSplitText;
    }

    /**
     * Splits text into prefix and suffix for display on the scoreboard.
     * This method handles text longer than 16 characters by splitting it appropriately.
     * 
     * @param input The text to split
     * @return Array containing [prefix, suffix]
     */
    public String[] splitText(String input) {
        if (input == null || input.length() <= MAX_TEXT_LENGTH) {
            return new String[] {input != null ? input : "", ""};
        }
        
        String prefix = input.substring(0, MAX_TEXT_LENGTH);
        int lastColorIndex = prefix.lastIndexOf(ChatColor.COLOR_CHAR);
        String suffix;

        if (lastColorIndex >= 14) {
            prefix = prefix.substring(0, lastColorIndex);
            suffix = ChatColor.getLastColors(input.substring(0, 17)) + input.substring(lastColorIndex + 2);
        } else {
            suffix = ChatColor.getLastColors(prefix) + input.substring(MAX_TEXT_LENGTH);
        }

        if (VersioningChecker.getInstance().isServerVersionBefore("1.16.5") && suffix.length() > MAX_TEXT_LENGTH) {
            suffix = suffix.substring(0, MAX_TEXT_LENGTH);
        }
        
        return new String[] {prefix, suffix};
    }

    /**
     * Sets the text for this entry and clears the cache.
     * 
     * @param text The new text to display
     * @return This board entry for method chaining
     */
    public BoardEntry setText(String text) {
        if (text == null || !this.text.equals(text)) {
            this.text = text != null ? text : "";
            this.cachedSplitText = null;
        }
        return this;
    }
}