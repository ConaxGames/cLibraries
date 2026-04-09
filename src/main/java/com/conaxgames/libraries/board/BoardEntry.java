package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.CC;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.regex.Pattern;

@Accessors(chain = true)
public class BoardEntry {

    private static final Pattern INVALID_TEAM_CHARS = Pattern.compile("[^a-zA-Z0-9_.-]");
    private static final int MAX_TEAM_NAME = 16;
    private static final String TEAM_PREFIX = "sb_";
    private static int teamCounter = 0;

    @Getter
    private final Board board;
    @Getter
    private final String key;
    @Getter
    private Team team;
    @Getter
    private String text;
    private String[] cachedSplit;

    public BoardEntry(Board board, String text) {
        this.board = board;
        this.text = text != null ? text : "";
        this.key = board.getNewKey(this);
        this.setup();
    }

    public void setup() {
        if (this.team == null) {
            Scoreboard sb = board.getScoreboard();
            String name = key.length() > MAX_TEAM_NAME ? key.substring(0, MAX_TEAM_NAME) : key;
            name = INVALID_TEAM_CHARS.matcher(name).replaceAll("");
            if (name.isEmpty()) {
                name = TEAM_PREFIX + (++teamCounter);
            }
            this.team = sb.registerNewTeam(name);
            this.team.addEntry(this.key);
            board.getEntries().add(this);
        }
    }

    public void send(int position) {
        Objective obj = board.getObjective();
        String[] split = getSplitText();
        int maxP = BoardHandler.maxPrefixLength();
        int maxS = BoardHandler.maxSuffixLength();
        String prefix = BoardHandler.clipToLength(split[0], maxP);
        String suffix = BoardHandler.clipToLength(split[1], maxS);
        if (!prefix.equals(team.getPrefix())) {
            team.setPrefix(prefix);
        }
        if (!suffix.equals(team.getSuffix())) {
            team.setSuffix(suffix);
        }
        Score score = obj.getScore(this.key);
        if (score.getScore() != position) {
            score.setScore(position);
        }
    }

    public void remove() {
        board.getUsedKeys().remove(this.key);
        board.getScoreboard().resetScores(this.key);
        if (team != null) {
            team.removeEntry(this.key);
            team.unregister();
        }
    }

    private String[] getSplitText() {
        if (cachedSplit == null) {
            cachedSplit = splitLine(CC.translate(text));
        }
        return cachedSplit;
    }

    private String[] splitLine(String input) {
        int unit = BoardHandler.lineSplitUnit();
        if (input.length() <= unit) {
            return new String[]{input, ""};
        }
        String prefix = input.substring(0, unit);
        int lastColor = prefix.lastIndexOf('\u00a7');
        String suffix;
        if (lastColor >= unit - 2) {
            prefix = prefix.substring(0, lastColor);
            int end = Math.min(input.length(), unit + 1);
            suffix = CC.getLastColors(input.substring(0, end)) + input.substring(lastColor + 2);
        } else {
            suffix = CC.getLastColors(prefix) + input.substring(unit);
        }
        return new String[]{prefix, suffix};
    }

    public BoardEntry setText(String text) {
        if (text != null && !this.text.equals(text)) {
            this.text = text;
            this.cachedSplit = null;
        }
        return this;
    }
}
