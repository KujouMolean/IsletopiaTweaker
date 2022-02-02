package com.molean.isletopia.utils;

import org.bukkit.block.BlockFace;
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

    public BlockFace toBlockFace() {
        switch (this) {
            case SOUTH -> {
                return BlockFace.SOUTH;
            }
            case EAST -> {
                return BlockFace.EAST;
            }
            case NORTH -> {
                return BlockFace.NORTH;
            }
            case WEST ->{
                return BlockFace.WEST;
            }
        }
        return null;
    }
}
