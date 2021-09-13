package com.conaxgames.libraries.hooks;

public enum HookType {

    /* PUBLIC */
    PAPI(GamemodeType.UNKNOWN),
    VAULT(GamemodeType.UNKNOWN),
    TITLE_MANAGER(GamemodeType.UNKNOWN),
    WORLD_GUARD(GamemodeType.UNKNOWN),

    /* CONAX */
    ARENAPVP(GamemodeType.ARENA_PVP),
    UHC(GamemodeType.UHC),
    MANGO(GamemodeType.HUB),
    HCF(GamemodeType.HCF),
    KITPVP(GamemodeType.KITPVP),
    SKYBLOCK(GamemodeType.SKYBLOCK);

    public final GamemodeType gamemode;

    private HookType(GamemodeType gamemode) {
        this.gamemode = gamemode;
    }

    public GamemodeType getGamemode() {
        return this.gamemode;
    }
}
