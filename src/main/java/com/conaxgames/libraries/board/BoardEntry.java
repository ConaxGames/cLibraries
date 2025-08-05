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

@Accessors(chain = true)
public class BoardEntry {

	// Pre-compiled regex pattern for better performance
	private static final Pattern INVALID_TEAM_CHARS = Pattern.compile("[^a-zA-Z0-9_.-]");
	
	private final Board board;
	private final String originalText;
	private Team team;
	private String text;
	private String key;
	private String cachedTranslatedText;
	private String[] cachedSplitText;
	private int lastPosition = -1;
	private boolean needsTeamUpdate = true;

	public BoardEntry(Board board, String text) {
		// Safety check for null board
		if (board == null) {
			throw new IllegalArgumentException("Board cannot be null");
		}
		
		this.board = board;
		this.text = text != null ? text : "";
		this.originalText = this.text;
		this.key = board.getNewKey(this);

		this.setup();
	}

	public BoardEntry setup() {
		// Only setup team if not already done or if text changed
		if (this.team == null) {
			Scoreboard scoreboard = this.board.getScoreboard();
			
			// Safety check for null scoreboard
			if (scoreboard == null) {
				return this;
			}
			
			// Safety check for null board
			if (this.board == null) {
				return this;
			}

			String teamName = this.key;
			// Team names must be 16 characters or less and cannot contain certain characters
			if (teamName.length() > 16) {
				teamName = teamName.substring(0, 16);
			}
			// Remove invalid characters for team names using pre-compiled pattern
			teamName = INVALID_TEAM_CHARS.matcher(teamName).replaceAll("");
			
			// Ensure team name is not empty after filtering
			if (teamName.isEmpty()) {
				teamName = "board_" + (System.currentTimeMillis() % 10000);
			}

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
		}

		return this;
	}

	/**
	 * Optimized send method that batches team updates and reduces operations
	 */
	public BoardEntry sendOptimized(int position) {
		Objective objective = board.getObjective();
		
		// Safety check for null objective
		if (objective == null) {
			return this;
		}
		
		// Ensure team is set up before proceeding
		if (this.team == null) {
			this.setup();
		}
		
		// Only update team if text changed or first time
		if (needsTeamUpdate) {
			updateTeamText();
			needsTeamUpdate = false;
		}
		
		// Only update score if position changed
		if (position != lastPosition) {
			Score score = objective.getScore(this.key);
			score.setScore(position);
			lastPosition = position;
		}

		return this;
	}
	
	/**
	 * Only update position without team text changes
	 */
	public BoardEntry sendPositionOnly(int position) {
		if (position != lastPosition) {
			Objective objective = board.getObjective();
			// Safety check for null objective
			if (objective == null) {
				return this;
			}
			Score score = objective.getScore(this.key);
			score.setScore(position);
			lastPosition = position;
		}
		return this;
	}
	
	/**
	 * Batch team text updates for better performance
	 */
	private void updateTeamText() {
		// Safety check for null team
		if (this.team == null) {
			return;
		}
		
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
		if (prefix.length() > 64) {
			prefix = prefix.substring(0, 64);
		}
		if (suffix.length() > 64) {
			suffix = suffix.substring(0, 64);
		}
		
		// Performance optimization: Only update if values actually changed
		String currentPrefix = this.team.getPrefix();
		String currentSuffix = this.team.getSuffix();
		
		if (!prefix.equals(currentPrefix) || !suffix.equals(currentSuffix)) {
			this.team.setPrefix(prefix);
			this.team.setSuffix(suffix);
		}
	}

	/**
	 * Legacy method for backward compatibility
	 */
	public BoardEntry send(int position) {
		return sendOptimized(position);
	}

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
	 * Reset this entry for reuse from the object pool
	 */
	public void resetForReuse(String newText) {
		// Reset all state variables
		this.text = newText;
		this.cachedTranslatedText = null;
		this.cachedSplitText = null;
		this.lastPosition = -1;
		this.needsTeamUpdate = true;
		
		// Generate new key for this entry
		this.key = board.getNewKey(this);
		
		// Reset team state
		this.team = null;
		
		// Setup the entry with new state
		this.setup();
	}
	
	/**
	 * Clean up this entry before returning to pool
	 */
	public void cleanupForPool() {
		// Remove from current board entries list
		if (this.board.getEntries().contains(this)) {
			this.board.getEntries().remove(this);
		}
		
		// Remove key from board keys set
		this.board.getKeys().remove(this.key);
		
		// Reset scores but don't unregister team (keep for reuse)
		this.board.getScoreboard().resetScores(this.key);
		
		// Remove entry from team and properly unregister empty teams to prevent conflicts
		if (this.team != null) {
			try {
				if (this.team.getEntries().contains(this.key)) {
					this.team.removeEntry(this.key);
				}
				// Unregister empty teams to prevent name conflicts and memory leaks
				if (this.team.getEntries().isEmpty() && this.board.getScoreboard().getTeam(this.team.getName()) != null) {
					this.team.unregister();
				}
			} catch (IllegalStateException e) {
				// Team might have been already removed
			}
		}
	}

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

    public BoardEntry setText(String text) {
        // Safety check for null text
        if (text == null) {
            text = "";
        }
        
        // Only update if text actually changed
        if (!this.text.equals(text)) {
            this.text = text;
            // Clear caches when text changes
            this.cachedTranslatedText = null;
            this.cachedSplitText = null;
            this.needsTeamUpdate = true;
        }
        return this;
    }

	public String[] splitText(String input) { // allows up-to 32 chars length on under 1.16 server version
		final int inputLength = input.length();
		if (inputLength > 16) {
			String prefix = input.substring(0, 16);
			final int lastColorIndex = prefix.lastIndexOf(ChatColor.COLOR_CHAR);
			String suffix;

			if (lastColorIndex >= 14) {
				prefix = prefix.substring(0, lastColorIndex);
				suffix = ChatColor.getLastColors(input.substring(0, 17)) + input.substring(lastColorIndex + 2);
			} else {
				suffix = ChatColor.getLastColors(prefix) + input.substring(16);
			}

			if (VersioningChecker.getInstance().isServerVersionBefore("1.16.5")) {
				if (suffix.length() > 16) {
					suffix = suffix.substring(0, 16);
				}
			}
			return new String[] {prefix, suffix};
		} else {
			return new String[] {input, ""};
		}
	}
}