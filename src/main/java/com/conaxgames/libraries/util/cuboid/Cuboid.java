package com.conaxgames.libraries.util.cuboid;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Simple and efficient 3D cuboid region implementation.
 */
public final class Cuboid implements Iterable<Location> {
    private final String worldName;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private final String name;

    // Constructors
    public Cuboid(Location l1, Location l2) {
        if (l1 == null || l2 == null || l1.getWorld() == null || l2.getWorld() == null) {
            throw new IllegalArgumentException("Cuboid corners must have non-null worlds");
        }
        
        this.worldName = l1.getWorld().getName();
        this.minX = Math.min(l1.getBlockX(), l2.getBlockX());
        this.maxX = Math.max(l1.getBlockX(), l2.getBlockX());
        this.minY = Math.min(l1.getBlockY(), l2.getBlockY());
        this.maxY = Math.max(l1.getBlockY(), l2.getBlockY());
        this.minZ = Math.min(l1.getBlockZ(), l2.getBlockZ());
        this.maxZ = Math.max(l1.getBlockZ(), l2.getBlockZ());
        this.name = null;
    }

    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this(world.getName(), x1, y1, z1, x2, y2, z2);
    }

    public Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = worldName;
        this.minX = Math.min(x1, x2);
        this.maxX = Math.max(x1, x2);
        this.minY = Math.min(y1, y2);
        this.maxY = Math.max(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxZ = Math.max(z1, z2);
        this.name = null;
    }

    public Cuboid(Map<String, Object> map) {
        this.worldName = (String) map.get("worldName");
        if (Bukkit.getWorld(this.worldName) == null) {
            throw new IllegalArgumentException("World '" + this.worldName + "' is not loaded");
        }
        this.minX = (int) map.get("x1");
        this.maxX = (int) map.get("x2");
        this.minY = (int) map.get("y1");
        this.maxY = (int) map.get("y2");
        this.minZ = (int) map.get("z1");
        this.maxZ = (int) map.get("z2");
        this.name = (String) map.get("name");
    }

    // Core methods
    public Location getMin() {
        return new Location(getWorld(), minX, minY, minZ);
    }

    public Location getMax() {
        return new Location(getWorld(), maxX, maxY, maxZ);
    }

    public Location getCenter() {
        return new Location(getWorld(), 
            minX + (maxX - minX) / 2.0,
            minY + (maxY - minY) / 2.0,
            minZ + (maxZ - minZ) / 2.0);
    }

    public World getWorld() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalStateException("World '" + worldName + "' is not loaded");
        }
        return world;
    }

    // Size and volume
    public int getSizeX() { return maxX - minX + 1; }
    public int getSizeY() { return maxY - minY + 1; }
    public int getSizeZ() { return maxZ - minZ + 1; }
    public int getVolume() { return getSizeX() * getSizeY() * getSizeZ(); }

    // Containment checks
    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public boolean contains(int x, int z) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    public boolean contains(Location location) {
        return location != null && 
               worldName.equals(location.getWorld().getName()) &&
               contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean contains(Block block) {
        return contains(block.getLocation());
    }

    // Expansion and modification
    public Cuboid expand(RegionDirection direction, int amount) {
        switch (direction) {
            case NORTH: return new Cuboid(worldName, minX - amount, minY, minZ, maxX, maxY, maxZ);
            case SOUTH: return new Cuboid(worldName, minX, minY, minZ, maxX + amount, maxY, maxZ);
            case EAST: return new Cuboid(worldName, minX, minY, minZ - amount, maxX, maxY, maxZ);
            case WEST: return new Cuboid(worldName, minX, minY, minZ, maxX, maxY, maxZ + amount);
            case DOWN: return new Cuboid(worldName, minX, minY - amount, minZ, maxX, maxY, maxZ);
            case UP: return new Cuboid(worldName, minX, minY, minZ, maxX, maxY + amount, maxZ);
            default: throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    public Cuboid shift(RegionDirection direction, int amount) {
        return expand(direction, amount).expand(direction.opposite(), -amount);
    }

    public Cuboid outset(RegionDirection direction, int amount) {
        switch (direction) {
            case HORIZONTAL:
                return expand(RegionDirection.NORTH, amount)
                       .expand(RegionDirection.SOUTH, amount)
                       .expand(RegionDirection.EAST, amount)
                       .expand(RegionDirection.WEST, amount);
            case VERTICAL:
                return expand(RegionDirection.DOWN, amount)
                       .expand(RegionDirection.UP, amount);
            case BOTH:
                return outset(RegionDirection.HORIZONTAL, amount)
                       .outset(RegionDirection.VERTICAL, amount);
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    public Cuboid inset(RegionDirection direction, int amount) {
        return outset(direction, -amount);
    }

    // Face operations
    public Cuboid getFace(RegionDirection direction) {
        switch (direction) {
            case DOWN: return new Cuboid(worldName, minX, minY, minZ, maxX, minY, maxZ);
            case UP: return new Cuboid(worldName, minX, maxY, minZ, maxX, maxY, maxZ);
            case NORTH: return new Cuboid(worldName, minX, minY, minZ, minX, maxY, maxZ);
            case SOUTH: return new Cuboid(worldName, maxX, minY, minZ, maxX, maxY, maxZ);
            case EAST: return new Cuboid(worldName, minX, minY, minZ, maxX, maxY, minZ);
            case WEST: return new Cuboid(worldName, minX, minY, maxZ, maxX, maxY, maxZ);
            default: throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    public Cuboid[] getWalls() {
        return new Cuboid[]{
            getFace(RegionDirection.NORTH),
            getFace(RegionDirection.SOUTH),
            getFace(RegionDirection.EAST),
            getFace(RegionDirection.WEST)
        };
    }

    // Bounding operations
    public Cuboid getBoundingCuboid(Cuboid other) {
        if (other == null) return this;
        
        return new Cuboid(worldName,
            Math.min(minX, other.minX), Math.min(minY, other.minY), Math.min(minZ, other.minZ),
            Math.max(maxX, other.maxX), Math.max(maxY, other.maxY), Math.max(maxZ, other.maxZ));
    }

    // Block access
    public Block getBlock(int x, int y, int z) {
        return getWorld().getBlockAt(x, y, z);
    }

    public Block getRelativeBlock(int x, int y, int z) {
        return getWorld().getBlockAt(minX + x, minY + y, minZ + z);
    }

    // Chunk operations
    public List<Chunk> getChunks() {
        List<Chunk> chunks = new ArrayList<>();
        World world = getWorld();
        
        int chunkMinX = minX >> 4;
        int chunkMaxX = maxX >> 4;
        int chunkMinZ = minZ >> 4;
        int chunkMaxZ = maxZ >> 4;
        
        for (int x = chunkMinX; x <= chunkMaxX; x++) {
            for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                chunks.add(world.getChunkAt(x, z));
            }
        }
        
        return chunks;
    }

    // Random location
    public Location getRandomLocation() {
        return new Location(getWorld(),
            minX + Math.random() * (maxX - minX + 1),
            minY + Math.random() * (maxY - minY + 1),
            minZ + Math.random() * (maxZ - minZ + 1));
    }

    // Serialization
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("worldName", worldName);
        map.put("x1", minX);
        map.put("x2", maxX);
        map.put("y1", minY);
        map.put("y2", maxY);
        map.put("z1", minZ);
        map.put("z2", maxZ);
        map.put("name", name);
        return map;
    }

    // Iterator implementation
    @Override
    public Iterator<Location> iterator() {
        return new CuboidIterator();
    }

    private class CuboidIterator implements Iterator<Location> {
        private int x = minX, y = minY, z = minZ;
        private final World world = getWorld();

        @Override
        public boolean hasNext() {
            return x <= maxX && y <= maxY && z <= maxZ;
        }

        @Override
        public Location next() {
            Location location = new Location(world, x, y, z);
            if (++x > maxX) {
                x = minX;
                if (++y > maxY) {
                    y = minY;
                    ++z;
                }
            }
            return location;
        }
    }

    // Object methods
    @Override
    public String toString() {
        return String.format("Cuboid[%s: (%d,%d,%d) to (%d,%d,%d)]", 
            worldName, minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cuboid)) return false;
        
        Cuboid other = (Cuboid) obj;
        return worldName.equals(other.worldName) &&
               minX == other.minX && maxX == other.maxX &&
               minY == other.minY && maxY == other.maxY &&
               minZ == other.minZ && maxZ == other.maxZ;
    }

    @Override
    public int hashCode() {
        int result = worldName.hashCode();
        result = 31 * result + minX;
        result = 31 * result + maxX;
        result = 31 * result + minY;
        result = 31 * result + maxY;
        result = 31 * result + minZ;
        result = 31 * result + maxZ;
        return result;
    }

    // Getters
    public String getWorldName() { return worldName; }
    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
    public String getName() { return name; }
}
