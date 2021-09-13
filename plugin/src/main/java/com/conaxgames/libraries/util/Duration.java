package com.conaxgames.libraries.util;

public enum Duration {

	SECOND(1000, "s"),
	MINUTE(60 * SECOND.duration, "m"),
	HOUR(60 * MINUTE.duration, "h"),
	DAY(24 * HOUR.duration, "d"),
	WEEK(7 * DAY.duration, "w"),
	MONTH(30 * DAY.duration, "M"),
	YEAR(365 * DAY.duration, "y");

	private final long duration;
	private final String name;

	private Duration(long duration, String name) {
		this.duration = duration;
		this.name = name;
	}

	public static Duration getByName(String name) {
		for (Duration duration : values()) {
			if (duration.getName().equals(name)) {
				return duration;
			}
		}
		return null;
	}

	public long getDuration() {
		return this.duration;
	}

	public String getName() {
		return this.name;
	}
}
