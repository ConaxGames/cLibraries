package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.CC;
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

//		if (LibNMSManager.getInstance().getServerVersion().after(LibServerVersion.v1_16_R3)) {
//			this.team.setSuffix("");
//			this.team.setPrefix(CC.translate(this.text));
////			this.team.setSuffix(CC.translate(this.text));
////			this.team.addEntry(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', "&a"));
//		} else {

//		if (this.text.length() > 16) {
//			this.team.addEntry(this.text.substring(0, 16));
//
//			boolean addOne = this.team.getPrefix().endsWith(ChatColor.COLOR_CHAR + "");
//
//			if (addOne) {
//				this.team.addEntry(CC.translate(this.text.substring(0, 15)));
//			}
//
//			String suffix = ChatColor.getLastColors(this.team.getPrefix())
//					+ this.text.substring(addOne ? 15 : 16);
//
//			if (suffix.length() > 16) {
//				if (suffix.length() - 2 <= 16) {
//					suffix = this.text.substring(addOne ? 15 : 16);
//					this.team.addEntry(CC.translate(suffix));
//				} else {
//					this.team.addEntry(CC.translate(suffix.substring(0, 16)));
//				}
//
//			} else {
//				this.team.addEntry(CC.translate(suffix));
//			}
//		} else {
//			this.team.setSuffix("");
//			this.team.addEntry(CC.translate(this.text));
//		}

		// Set Prefix & Suffix.
		String[] split = this.splitText(CC.translate(text));
		this.team.setPrefix(CC.translate(split[0]));
		this.team.setSuffix(CC.translate(split[1]));
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
			// Make the prefix the first 16 characters of our text
			String prefix = input.substring(0, 16);

			// Get the last index of the color char in the prefix
			final int lastColorIndex = prefix.lastIndexOf(ChatColor.COLOR_CHAR);

			String suffix;

			if (lastColorIndex >= 14) {
				prefix = prefix.substring(0, lastColorIndex);
				suffix = ChatColor.getLastColors(input.substring(0, 17)) + input.substring(lastColorIndex + 2);
			} else {
				suffix = ChatColor.getLastColors(prefix) + input.substring(16);
			}

			// todo: add libserverversion for checking version :D
//			if (LibNMSManager.getInstance().getServerVersion().before(LibServerVersion.v1_16_R3)) { // only substring if server ver pre-hex
//				if (suffix.length() > 16) {
//					suffix = suffix.substring(0, 16);
//				}
//			}

			return new String[] {prefix, suffix};
		} else {
			return new String[] {input, ""};
		}
	}

}