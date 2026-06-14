package com.conaxgames.libraries.hooks;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public enum HookType {

    PLACEHOLDERAPI,
    VAULT,
    TITLEMANAGER,
    WORLDGUARD,
    PROTOCOLLIB,
    CMI,
    TAB,
    LUCKPERMS,
    CITIZENS,

    CSUITE,
    CMIGRATION,
    ANTICRASH,
    ARENAPVP,
    UHC,
    MANGO,
    HCF,
    KITPVP,
    FFA,
    CGLOBE,
    SKYBLOCK;

    private static final Map<String, HookType> BY_NAME;

    static {
        HashMap<String, HookType> map = new HashMap<>();
        for (HookType type : values()) {
            map.put(type.name().toLowerCase(Locale.ROOT), type);
        }
        BY_NAME = Map.copyOf(map);
    }

    public static Optional<HookType> fromPluginName(String name) {
        return Optional.ofNullable(BY_NAME.get(name.toLowerCase(Locale.ROOT)));
    }
}
