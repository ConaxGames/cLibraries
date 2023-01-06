package com.conaxgames.libraries.hooks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum GamemodeType {
    HUB("Hub", Arrays.asList("hub","lobby")),
    ARENA_PVP("ArenaPvP", Arrays.asList("prac","practice","arenapvp")),
    UHC("UHC", Collections.singletonList("uhc")),
    UHC_MEETUP("UHC Meetup", Arrays.asList("meetup","uhcm")),
    HCF("HCF", Arrays.asList("hcf","hardcorefactions")),
    SKYBLOCK("Skyblock", Arrays.asList("sb","skyblock")),
    SURVIVAL("Survival", Arrays.asList("survival","smp")),
    KITPVP("KitPvP", Collections.singletonList("kitpvp")),
    FFA("FFA", Collections.singletonList("ffa")),
    UNKNOWN("Unknown", Arrays.asList("null","unknown"));

    private final String display;
    private final List<String> identifiers;

    GamemodeType(String display, List<String> identifiers) {
        this.display = display;
        this.identifiers = identifiers;
    }

    public String getDisplay() {
        return this.display;
    }

    public List<String> getIdentifiers() {
        return this.identifiers;
    }
}
