package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.VersioningChecker;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Accessors(chain = true)
public class BoardEntry {

	private final Board board;

	private final String originalText;

	private Team team;

	private String text;
	private String key;

	public BoardEntry(Board board, String text) {
		this.board = board;
		this.text = text;
		this.originalText = text;
		this.key = board.getNewKey(this);

		this.setup();
	}

	public BoardEntry setup() {
		Scoreboard scoreboard = this.board.getScoreboard();

		String teamName = this.key;
		// Team names must be 16 characters or less and cannot contain certain characters
		if (teamName.length() > 16) {
			teamName = teamName.substring(0, 16);
		}
		// Remove invalid characters for team names
		teamName = teamName.replaceAll("[^a-zA-Z0-9_.-]", "");
		
		// Ensure team name is not empty after filtering
		if (teamName.isEmpty()) {
			teamName = "board_" + System.currentTimeMillis() % 10000;
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

		if (!(this.team.getEntries().contains(this.key))) {
			this.team.addEntry(this.key);
		}

		if (!(this.board.getEntries().contains(this))) {
			this.board.getEntries().add(this);
		}

		return this;
	}

	public BoardEntry send(int position) {
		Objective objective = board.getObjective();
		String preSplit = CC.translate(text);
		String[] split = this.splitText(preSplit);
		
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
		
		this.team.setPrefix(prefix);
		this.team.setSuffix(suffix);

		Score score = objective.getScore(this.key);
		score.setScore(position);

		return this;
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