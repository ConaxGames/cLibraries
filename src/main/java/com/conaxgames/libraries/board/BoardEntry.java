package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.VersioningChecker;
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
    private static final String TIME_SUFFIX = String.valueOf(System.currentTimeMillis() % 10000);
    
    // Instance fields
    private final Board board;
    private final String originalText;
    private final String key;
    
    private Team team;
    private String text;
    private String cachedTranslatedText;
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
        // Only setup team if not already done or if text changed
        if (this.team == null) {
            Scoreboard scoreboard = this.board.getScoreboard();
            String teamName = createValidTeamName();
            
            // Ensure unique team name by adding counter if needed
            String originalTeamName = teamName;
            int counter = 1;
            while (scoreboard.getTeam(teamName) != null && !scoreboard.getTeam(teamName).getEntries().contains(this.key)) {
                teamName = originalTeamName.substring(0, Math.min(originalTeamName.length(), 14)) + counter;
                counter++;
            }

            if (scoreboard.getTeam(teamName) != null) {
                this.team = scoreboard.getTeam(teamName);
            } else {
                this.team = scoreboard.registerNewTeam(teamName);
            }

            if (!this.team.getEntries().contains(this.key)) {
                this.team.addEntry(this.key);
            }

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
        
        // Remove invalid characters for team names using pre-compiled pattern
        teamName = INVALID_TEAM_CHARS.matcher(teamName).replaceAll("");
        
        // Ensure team name is not empty after filtering
        if (teamName.isEmpty()) {
            teamName = TEAM_NAME_PREFIX + TIME_SUFFIX;
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
        
        // Cache translated text to avoid repeated CC.translate calls
        if (cachedTranslatedText == null) {
            cachedTranslatedText = CC.translate(text);
            cachedSplitText = this.splitText(cachedTranslatedText);
        }
        
        // Use cached split text
        String[] split = cachedSplitText;
        
        // Validate prefix and suffix lengths to prevent protocol errors
        String prefix = split[0];
        String suffix = split[1];
        
        // Modern Minecraft has stricter limits - ensure we don't exceed them
        if (prefix.length() > MAX_PREFIX_LENGTH) {
            prefix = prefix.substring(0, MAX_PREFIX_LENGTH);
        }
        if (suffix.length() > MAX_SUFFIX_LENGTH) {
            suffix = suffix.substring(0, MAX_SUFFIX_LENGTH);
        }
        
        // Only update team prefix/suffix if they actually changed (reduces packet spam)
        if (!prefix.equals(this.team.getPrefix())) {
            this.team.setPrefix(prefix);
        }
        if (!suffix.equals(this.team.getSuffix())) {
            this.team.setSuffix(suffix);
        }

        Score score = objective.getScore(this.key);
        // Only update score if it changed
        if (score.getScore() != position) {
            score.setScore(position);
        }

        return this;
    }

    /**
     * Removes this entry from the scoreboard and cleans up associated resources.
     */
    public void remove() {
        this.board.getKeys().remove(this.key);
        this.board.getScoreboard().resetScores(this.key);
        
        // Remove entry from team and unregister empty teams
        if (this.team != null) {
            try {
                // Check if the team still contains this entry before removing
                if (this.team.getEntries().contains(this.key)) {
                    this.team.removeEntry(this.key);
                }
                // Only unregister if team is empty and still registered
                if (this.team.getEntries().isEmpty() && this.board.getScoreboard().getTeam(this.team.getName()) != null) {
                    this.team.unregister();
                }
            } catch (IllegalStateException e) {
                // Team might have been already removed or player not on team
                // This is expected behavior in some edge cases
            }
        }
    }

    /**
     * Splits text into prefix and suffix for display on the scoreboard.
     * This method handles text longer than 16 characters by splitting it appropriately.
     * 
     * @param input The text to split
     * @return Array containing [prefix, suffix]
     */
    public String[] splitText(String input) {
        final int inputLength = input.length();
        if (inputLength > MAX_TEXT_LENGTH) {
            String prefix = input.substring(0, MAX_TEXT_LENGTH);
            final int lastColorIndex = prefix.lastIndexOf(ChatColor.COLOR_CHAR);
            String suffix;

            if (lastColorIndex >= 14) {
                prefix = prefix.substring(0, lastColorIndex);
                suffix = ChatColor.getLastColors(input.substring(0, 17)) + input.substring(lastColorIndex + 2);
            } else {
                suffix = ChatColor.getLastColors(prefix) + input.substring(MAX_TEXT_LENGTH);
            }

            if (VersioningChecker.getInstance().isServerVersionBefore("1.16.5")) {
                if (suffix.length() > MAX_TEXT_LENGTH) {
                    suffix = suffix.substring(0, MAX_TEXT_LENGTH);
                }
            }
            return new String[] {prefix, suffix};
        } else {
            return new String[] {input, ""};
        }
    }

    // Getter methods
    public Board getBoard() {
        return this.board;
    }

    public String getOriginalText() {
        return this.originalText;
    }

    public Team getTeam() {
        return this.team;
    }

    public String getText() {
        return this.text;
    }

    public String getKey() {
        return this.key;
    }

    /**
     * Sets the text for this entry and clears the cache.
     * 
     * @param text The new text to display
     * @return This board entry for method chaining
     */
    public BoardEntry setText(String text) {
        // Only update if text actually changed
        if (!this.text.equals(text)) {
            this.text = text;
            // Clear caches when text changes
            this.cachedTranslatedText = null;
            this.cachedSplitText = null;
        }
        return this;
    }
}