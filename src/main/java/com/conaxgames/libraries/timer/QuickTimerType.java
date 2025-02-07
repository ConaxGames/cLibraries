package com.conaxgames.libraries.timer;

import java.util.HashMap;
import java.util.UUID;

public class QuickTimerType {

    public String ability = "";
    public UUID player;
    public long time;
    public boolean xp;
    private boolean announce;

    public HashMap<String, QuickTimerType> timerMap = new HashMap<>();

    public QuickTimerType(UUID UUID, long time, boolean announce) {
        this.player = UUID;
        this.time = time;
        this.announce = announce;
    }

    public String getAbility() {
        return this.ability;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public long getTime() {
        return this.time;
    }

    public boolean isXp() {
        return this.xp;
    }

    public boolean isAnnounce() {
        return this.announce;
    }

    public HashMap<String, QuickTimerType> getTimerMap() {
        return this.timerMap;
    }

    public void setXp(boolean xp) {
        this.xp = xp;
    }
}