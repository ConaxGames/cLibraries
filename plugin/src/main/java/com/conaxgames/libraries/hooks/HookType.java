package com.conaxgames.libraries.hooks;

public enum HookType {

    /* PUBLIC */
    PLACEHOLDERAPI(GamemodeType.UNKNOWN),
    VAULT(GamemodeType.UNKNOWN),
    TITLE_MANAGER(GamemodeType.UNKNOWN),
    WORLD_GUARD(GamemodeType.UNKNOWN),
    PROTOCOLLIB(GamemodeType.UNKNOWN),
    CMI(GamemodeType.UNKNOWN),
    TAB(GamemodeType.UNKNOWN),
    LUCKPERMS(GamemodeType.UNKNOWN),

    /* CONAX */
    CSUITE(GamemodeType.UNKNOWN),
    CADDONS(GamemodeType.UNKNOWN),
    CMIGRATION(GamemodeType.UNKNOWN),
    ARENAPVP(GamemodeType.ARENA_PVP),
    UHC(GamemodeType.UHC),
    MANGO(GamemodeType.HUB),
    HCF(GamemodeType.HCF),
    KITPVP(GamemodeType.KITPVP),
    FFA(GamemodeType.FFA),
    CGLOBE(GamemodeType.SURVIVAL),
    SKYBLOCK(GamemodeType.SKYBLOCK);

    public final GamemodeType gamemode;

    private HookType(GamemodeType gamemode) {
        this.gamemode = gamemode;
    }

    public GamemodeType getGamemode() {
        return this.gamemode;
    }
}
