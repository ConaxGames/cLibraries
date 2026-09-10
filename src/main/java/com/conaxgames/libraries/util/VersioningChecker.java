package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.reflection.XReflection;

public final class VersioningChecker {

    public static boolean supports(String version) {
        String[] parts = version.split("\\.");
        return XReflection.supports(
                Integer.parseInt(parts[0]),
                parts.length > 1 ? Integer.parseInt(parts[1]) : 0,
                parts.length > 2 ? Integer.parseInt(parts[2]) : 0
        );
    }
}
