package com.conaxgames.libraries.timer;

import lombok.Getter;

public abstract class Timer {

    protected final String name;
    @Getter
    protected final long defaultCooldown;

    public Timer(String name, long defaultCooldown) {
        this.name = name;
        this.defaultCooldown = defaultCooldown;
    }

    public final String getDisplayName() {
        return this.name;
    }
}