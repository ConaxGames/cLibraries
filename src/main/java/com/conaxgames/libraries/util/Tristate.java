package com.conaxgames.libraries.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

// TODO: move to csuite
public enum Tristate {
    NOT_SET,
    FALSE,
    TRUE;

    public @Nullable Boolean toBoolean() {
        switch (this) {
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            default:
                return null;
        }
    }

    public boolean toBooleanOrDefault(final boolean def) {
        switch (this) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            default:
                return def;
        }
    }

    public boolean toBooleanOrElseGet(final @NotNull BooleanSupplier supplier) {
        switch (this) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            default:
                return supplier.getAsBoolean();
        }
    }

    public static @NotNull Tristate fromBoolean(final boolean value) {
        return value ? TRUE : FALSE;
    }

    public static @NotNull Tristate fromBoolean(final @Nullable Boolean value) {
        return value == null ? NOT_SET : fromBoolean(value);
    }
}