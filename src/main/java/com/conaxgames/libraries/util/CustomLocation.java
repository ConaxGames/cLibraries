package com.conaxgames.libraries.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class CustomLocation {

    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public CustomLocation(String world, double x, double y, double z, float yaw, float pitch) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static CustomLocation fromBukkitLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (location.getWorld() == null) {
            throw new IllegalArgumentException("Location world cannot be null");
        }
        return new CustomLocation(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    public static CustomLocation fromString(String string) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException("Location string cannot be null or empty");
        }

        String[] parts = string.split(",\\s*");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Location string must be x, y, z, yaw, pitch, world");
        }

        try {
            return new CustomLocation(
                    parts[5],
                    Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Float.parseFloat(parts[3]),
                    Float.parseFloat(parts[4])
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in location string", e);
        }
    }

    public Location toBukkitLocation() {
        World bukkitWorld = Bukkit.getWorld(world);
        return bukkitWorld == null ? null : new Location(bukkitWorld, x, y, z, yaw, pitch);
    }

    public double distanceTo(CustomLocation other) {
        if (other == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double groundDistanceTo(CustomLocation other) {
        if (other == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        double dx = x - other.x;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public String toString() {
        return String.format("%.2f, %.2f, %.2f, %.2f, %.2f, %s", x, y, z, yaw, pitch, world);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CustomLocation other)) {
            return false;
        }
        return world.equals(other.world)
                && Double.compare(x, other.x) == 0
                && Double.compare(y, other.y) == 0
                && Double.compare(z, other.z) == 0
                && Float.compare(yaw, other.yaw) == 0
                && Float.compare(pitch, other.pitch) == 0;
    }

    @Override
    public int hashCode() {
        int result = world.hashCode();
        result = 31 * result + Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        result = 31 * result + Float.hashCode(yaw);
        result = 31 * result + Float.hashCode(pitch);
        return result;
    }
}
