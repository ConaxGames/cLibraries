package com.conaxgames.libraries.board;

import com.conaxgames.libraries.util.CC;
import com.conaxgames.libraries.util.VersioningChecker;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class BoardHandler {

	private static volatile boolean initialized;
	private static int maxPrefixLength = 64;
	private static int maxSuffixLength = 64;
	private static int lineSplitUnit = 16;
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
			} else {
				maxPrefixLength = 64;
				maxSuffixLength = 64;
				lineSplitUnit = 64;
				maxObjectiveTitleLength = 1024;
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

	public static boolean legacySidebarLimits() {
		return lineSplitUnit <= 16;
	}

	public static Objective createSidebarObjective(Scoreboard scoreboard, String name, String rawTitle) {
		String translated = rawTitle != null ? CC.translate(rawTitle) : "";
		String clipped = clipTitle(translated);
		Objective objective = scoreboard.registerNewObjective(name, Criteria.DUMMY, clipped);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		return objective;
	}

	public static void applyObjectiveTitle(Objective objective, String rawTitle) {
		String translated = rawTitle != null ? CC.translate(rawTitle) : "";
		objective.setDisplayName(clipTitle(translated));
	}

	private static String clipTitle(String translated) {
		if (translated.length() <= maxObjectiveTitleLength) {
			return translated;
		}
		return translated.substring(0, maxObjectiveTitleLength);
	}

}
