package com.molean.isletopia.utils;

import org.bukkit.entity.Player;

public enum Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST;

    public static Direction getDirection(Player player) {
        float t = (player.getLocation().getYaw() + 180) % 360;
        if (t <= 45) {
            return Direction.NORTH;
        } else if (t <= 135) {
            return Direction.EAST;
        } else if (t <= 225) {
            return Direction.SOUTH;
        } else if (t <= 315) {
            return Direction.WEST;
        } else {
            return Direction.NORTH;
        }
    }
}
