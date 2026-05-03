package com.conaxgames.libraries.timer;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class QuickTimerType {

    private final boolean announce;
    public UUID player;
    public long time;
    public HashMap<String, QuickTimerType> timerMap = new HashMap<>();

    public QuickTimerType(UUID player, long time, boolean announce) {
        this.player = player;
        this.time = time;
        this.announce = announce;
    }
}
