package com.conaxgames.libraries.util.cuboid;

/**
 * Represents directions for cuboid operations.
 */
public enum RegionDirection {
    NORTH, EAST, SOUTH, WEST,
    UP, DOWN,
    HORIZONTAL, VERTICAL, BOTH;

    public RegionDirection opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            case UP: return DOWN;
            case DOWN: return UP;
            case HORIZONTAL: return VERTICAL;
            case VERTICAL: return HORIZONTAL;
            case BOTH: return BOTH;
            default: return this;
        }
    }
}