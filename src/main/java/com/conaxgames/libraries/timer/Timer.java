package com.conaxgames.libraries.timer;

public abstract class Timer {

	protected final String name;
	protected final long defaultCooldown;

	public Timer(String name, long defaultCooldown) {
		this.name = name;
		this.defaultCooldown = defaultCooldown;
	}

	public final String getDisplayName() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public long getDefaultCooldown() {
		return this.defaultCooldown;
	}
}