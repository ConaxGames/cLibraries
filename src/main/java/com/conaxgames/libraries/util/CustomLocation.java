package com.conaxgames.libraries.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Efficient immutable location representation.
 * Use instead of Bukkit's Location for storage and calculations.
 */
public final class CustomLocation {
    private final String world;
    private final double x, y, z;
    private final float yaw, pitch;
    private final long timestamp;

    // Constructors
    public CustomLocation(double x, double y, double z) {
        this("world", x, y, z, 0.0f, 0.0f);
    }

    public CustomLocation(String world, double x, double y, double z) {
        this(world, x, y, z, 0.0f, 0.0f);
    }

    public CustomLocation(double x, double y, double z, float yaw, float pitch) {
        this("world", x, y, z, yaw, pitch);
    }

    public CustomLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world != null ? world : "world";
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timestamp = System.currentTimeMillis();
    }

    // Static factory methods
    public static CustomLocation fromBukkitLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }
        return new CustomLocation(
            location.getWorld().getName(),
            location.getX(), location.getY(), location.getZ(),
            location.getYaw(), location.getPitch()
        );
    }

    public static CustomLocation fromString(String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Location string cannot be null or empty");
        }

        String[] parts = string.split(", ");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Location string must contain at least x, y, z coordinates");
        }

        try {
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);
            float yaw = 0.0f;
            float pitch = 0.0f;
            String world = "world";

            if (parts.length >= 4 && !parts[3].contains(".")) {
                world = parts[3];
            } else if (parts.length >= 5) {
                yaw = Float.parseFloat(parts[3]);
                pitch = Float.parseFloat(parts[4]);
                if (parts.length >= 6) {
                    world = parts[5];
                }
            }

            return new CustomLocation(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in location string", e);
        }
    }

    // Conversion methods
    public Location toBukkitLocation() {
        World bukkitWorld = Bukkit.getWorld(world);
        return bukkitWorld != null ? new Location(bukkitWorld, x, y, z, yaw, pitch) : null;
    }

    public World toBukkitWorld() {
        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            throw new IllegalStateException("World '" + world + "' is not loaded");
        }
        return bukkitWorld;
    }

    // Distance calculations
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

    // String conversion
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%.2f, %.2f, %.2f", x, y, z));
        
        if (yaw != 0.0f || pitch != 0.0f) {
            sb.append(String.format(", %.2f, %.2f", yaw, pitch));
        }
        
        if (!"world".equals(world)) {
            sb.append(", ").append(world);
        }
        
        return sb.toString();
    }

    // Getters
    public String getWorld() { return world; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public long getTimestamp() { return timestamp; }

    // Object methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CustomLocation)) return false;
        
        CustomLocation other = (CustomLocation) obj;
        return Double.compare(other.x, x) == 0 &&
               Double.compare(other.y, y) == 0 &&
               Double.compare(other.z, z) == 0 &&
               Float.compare(other.yaw, yaw) == 0 &&
               Float.compare(other.pitch, pitch) == 0 &&
               world.equals(other.world);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + world.hashCode();
        result = 31 * result + Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        result = 31 * result + Float.hashCode(yaw);
        result = 31 * result + Float.hashCode(pitch);
        return result;
    }
}
