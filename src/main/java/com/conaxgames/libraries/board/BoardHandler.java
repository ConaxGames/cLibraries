package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.VersioningChecker;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class BoardHandler {

	private static final String DUMMY_CRITERIA = "dummy";

	private static volatile boolean initialized;
	private static int maxPrefixLength = 64;
	private static int maxSuffixLength = 64;
	private static int lineSplitUnit = 64;
	private static int maxObjectiveTitleLength = 1024;

	private BoardHandler() {
	}

	public static void init() {
		if (initialized) {
			return;
		}
		synchronized (BoardHandler.class) {
			if (initialized) {
				return;
			}
			if (VersioningChecker.getInstance().isServerVersionBefore("1.13")) {
				maxPrefixLength = 16;
				maxSuffixLength = 16;
				lineSplitUnit = 16;
				maxObjectiveTitleLength = 32;
			}
			initialized = true;
		}
	}

	public static int maxPrefixLength() {
		return maxPrefixLength;
	}

	public static int maxSuffixLength() {
		return maxSuffixLength;
	}

	public static int lineSplitUnit() {
		return lineSplitUnit;
	}

	public static int maxObjectiveTitleLength() {
		return maxObjectiveTitleLength;
	}

	static String clipToLength(String value, int maxChars) {
		return value.length() <= maxChars ? value : value.substring(0, maxChars);
	}

	public static Objective createSidebarObjective(Scoreboard scoreboard, String name, String rawTitle) {
		Objective objective = scoreboard.registerNewObjective(name, DUMMY_CRITERIA);
		objective.setDisplayName(sidebarTitle(rawTitle));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		return objective;
	}

	public static void applyObjectiveTitle(Objective objective, String rawTitle) {
		objective.setDisplayName(sidebarTitle(rawTitle));
	}

	private static String sidebarTitle(String rawTitle) {
		String t = rawTitle != null ? CC.translate(rawTitle) : "";
		return clipToLength(t != null ? t : "", maxObjectiveTitleLength);
	}

}
