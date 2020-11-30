package com.molean.isletopia.utils;

import org.bukkit.entity.Player;

public enum Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST;

    public static Direction getDirection(Player player) {
        float t = (player.getLocation().getYaw()+360)%360;
        if (t <=45 ) {
            return Direction.SOUTH;
        } else if (t <= 135) {
            return Direction.WEST;
        } else if (t <= 225) {
            return Direction.NORTH;
        } else if (t <= 315) {
            return Direction.EAST;
        } else {
            return Direction.SOUTH;
        }
    }
}
