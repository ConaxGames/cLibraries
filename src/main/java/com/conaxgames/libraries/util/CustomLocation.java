package com.conaxgames.libraries.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.StringJoiner;

/**
 * Highly efficient version of the ${@link Location} class.
 * Recommended to store ${@link CustomLocation}
 * instead of using Bukkit's interpretation.
 */
public class CustomLocation {

	private final long timestamp = System.currentTimeMillis();
	private final String world;
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;

	public CustomLocation(double x, double y, double z) {
		this("world", x, y, z, 0.0F, 0.0F);
	}

	public CustomLocation(String world, double x, double y, double z) {
		this(world, x, y, z, 0.0F, 0.0F);
	}

	public CustomLocation(double x, double y, double z, float yaw, float pitch) {
		this("world", x, y, z, yaw, pitch);
	}

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
			return null;
		}

		World world = location.getWorld();
		if (world == null) {
			throw new IllegalArgumentException("Location's world cannot be null");
		}

		return new CustomLocation(world.getName(), location.getX(), location.getY(), location.getZ(),
				location.getYaw(), location.getPitch());
	}

	public static CustomLocation stringToLocation(String string) {
		if (string == null || string.isEmpty()) {
			throw new IllegalArgumentException("Location string cannot be null or empty");
		}

		String[] split = string.split(", ");
		if (split.length < 3) {
			throw new IllegalArgumentException("Location string must contain at least x, y, z coordinates");
		}

		try {
			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);
			double z = Double.parseDouble(split[2]);
			float yaw = 0.0f;
			float pitch = 0.0f;
			String world = "world";

			if (split.length >= 4 && !split[3].contains(".")) {
				world = split[3];
			}
			else if (split.length >= 5) {
				yaw = Float.parseFloat(split[3]);
				pitch = Float.parseFloat(split[4]);

				if (split.length >= 6) {
					world = split[5];
				}
			}

			return new CustomLocation(world, x, y, z, yaw, pitch);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid number format in location string", e);
		}
	}

	public static String locationToString(CustomLocation loc) {
		if (loc == null) {
			throw new IllegalArgumentException("Location cannot be null");
		}

		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%.6f, %.6f, %.6f", loc.getX(), loc.getY(), loc.getZ()));

		if (loc.getYaw() != 0.0f || loc.getPitch() != 0.0f) {
			builder.append(String.format(", %.6f, %.6f", loc.getYaw(), loc.getPitch()));
		}

		if (!loc.getWorld().equals("world")) {
			builder.append(", ").append(loc.getWorld());
		}

		return builder.toString();
	}

	public Location toBukkitLocation() {
		World bukkitWorld = Bukkit.getServer().getWorld(this.world);
		if (bukkitWorld == null) {
			return null;
		}
		return new Location(bukkitWorld, this.x, this.y, this.z, this.yaw, this.pitch);
	}

	public double getGroundDistanceTo(CustomLocation location) {
		if (location == null) {
			throw new IllegalArgumentException("Location cannot be null");
		}
		return Math.sqrt(Math.pow(this.x - location.x, 2) + Math.pow(this.z - location.z, 2));
	}

	public double getDistanceTo(CustomLocation location) {
		if (location == null) {
			throw new IllegalArgumentException("Location cannot be null");
		}
		return Math.sqrt(Math.pow(this.x - location.x, 2) + Math.pow(this.y - location.y, 2) + Math.pow(this.z - location.z, 2));
	}

	public World toBukkitWorld() {
		World bukkitWorld = Bukkit.getServer().getWorld(this.world);
		if (bukkitWorld == null) {
			throw new IllegalStateException("World '" + world + "' is not loaded");
		}
		return bukkitWorld;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CustomLocation)) {
			return false;
		}

		CustomLocation location = (CustomLocation) obj;
		return location.x == this.x && location.y == this.y && location.z == this.z
				&& location.pitch == this.pitch && location.yaw == this.yaw
				&& location.world.equals(this.world);
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

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("x", this.x)
				.append("y", this.y)
				.append("z", this.z)
				.append("yaw", this.yaw)
				.append("pitch", this.pitch)
				.append("world", this.world)
				.append("timestamp", this.timestamp)
				.toString();
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String getWorld() {
		return this.world;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}
}
