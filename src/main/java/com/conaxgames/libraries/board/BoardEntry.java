package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.VersioningChecker;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.concurrent.atomic.AtomicInteger;

@Accessors(chain = true)
public class BoardEntry {

	private final Board board;
	private final String originalText;
	private Team team;
	private String text;
	private String key;
	
	// Static counter for unique team names
	private static final AtomicInteger teamCounter = new AtomicInteger(0);

	public BoardEntry(Board board, String text) {
		this.board = board;
		this.text = text;
		this.originalText = text;
		this.key = board.getNewKey(this);
		this.setup();
	}

	public BoardEntry setup() {
		Scoreboard scoreboard = this.board.getScoreboard();
		
		// Generate unique team name efficiently
		String teamName = generateUniqueTeamName();
		
		// Get or create team
		Team existingTeam = scoreboard.getTeam(teamName);
		if (existingTeam != null) {
			this.team = existingTeam;
		} else {
			this.team = scoreboard.registerNewTeam(teamName);
		}

		// Add entry to team if not already present
		if (!this.team.getEntries().contains(this.key)) {
			this.team.addEntry(this.key);
		}

		// Add to board entries if not already present
		if (!this.board.getEntries().contains(this)) {
			this.board.getEntries().add(this);
		}

		return this;
	}
	
	private String generateUniqueTeamName() {
		// Use atomic counter for guaranteed uniqueness
		int counter = teamCounter.getAndIncrement();
		return "board_" + (counter % 10000);
	}

	public BoardEntry send(int position) {
		try {
			Objective objective = board.getObjective();
			String preSplit = CC.translate(text);
			String[] split = this.splitText(preSplit);
			
			// Validate and limit prefix/suffix lengths
			String prefix = validateLength(split[0], 64);
			String suffix = validateLength(split[1], 64);
			
			this.team.setPrefix(prefix);
			this.team.setSuffix(suffix);

			Score score = objective.getScore(this.key);
			score.setScore(position);
		} catch (Exception e) {
			// Log error but don't crash
			board.getPlayer().getServer().getLogger().warning(
				"Error updating scoreboard entry for " + board.getPlayer().getName() + ": " + e.getMessage()
			);
		}

		return this;
	}
	
	private String validateLength(String input, int maxLength) {
		if (input == null) return "";
		return input.length() > maxLength ? input.substring(0, maxLength) : input;
	}

	public void remove() {
		try {
			this.board.getKeys().remove(this.key);
			this.board.getScoreboard().resetScores(this.key);
			
			// Clean up team
			if (this.team != null) {
				try {
					if (this.team.getEntries().contains(this.key)) {
						this.team.removeEntry(this.key);
					}
					
					// Only unregister if team is empty and still registered
					if (this.team.getEntries().isEmpty() && 
						this.board.getScoreboard().getTeam(this.team.getName()) != null) {
						this.team.unregister();
					}
				} catch (IllegalStateException e) {
					// Team might have been already removed - this is expected in some cases
				}
			}
		} catch (Exception e) {
			// Log error but don't crash
			board.getPlayer().getServer().getLogger().warning(
				"Error removing scoreboard entry for " + board.getPlayer().getName() + ": " + e.getMessage()
			);
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
        this.text = text;
        return this;
    }

	public String[] splitText(String input) {
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

			// Apply version-specific length limits
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