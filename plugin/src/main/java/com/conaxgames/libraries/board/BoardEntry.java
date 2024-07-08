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
		if (teamName.length() > 16) {
			teamName = teamName.substring(0, 16);
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
		this.team.setPrefix(split[0]);
		this.team.setSuffix(split[1]);
		this.team.addEntry(ChatColor.translateAlternateColorCodes('&', "&a"));

		Score score = objective.getScore(this.key);
		score.setScore(position);

		return this;
	}

	public void remove() {
		this.board.getKeys().remove(this.key);
		this.board.getScoreboard().resetScores(this.key);
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