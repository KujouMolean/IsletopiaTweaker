package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;

public class RailWay implements Listener {
    public RailWay() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void railWay(PlayerToggleSneakEvent event) {
        if (!event.isSneaking())
            return;
        Block init = event.getPlayer().getLocation().getBlock();
        if (!init.getType().name().toLowerCase().contains("rail")) {
            return;
        }
        Block lastBlock = init;
        Block now = null;
        {
            List<Block> connectedRails = getConnectedRails(init);
            Direction direction = Direction.getDirection(event.getPlayer());
            switch (direction) {
                case NORTH -> {
                    for (Block connectedRail : connectedRails) {
                        if (connectedRail.getZ() < lastBlock.getZ()) {
                            now = connectedRail;
                            break;
                        }
                    }
                }
                case EAST -> {
                    for (Block connectedRail : connectedRails) {
                        if (connectedRail.getX() > lastBlock.getX()) {
                            now = connectedRail;
                            break;
                        }
                    }
                }
                case SOUTH -> {
                    for (Block connectedRail : connectedRails) {
                        if (connectedRail.getZ() > lastBlock.getZ()) {
                            now = connectedRail;
                            break;
                        }
                    }
                }
                case WEST -> {
                    for (Block connectedRail : connectedRails) {
                        if (connectedRail.getX() < lastBlock.getX()) {
                            now = connectedRail;
                            break;
                        }
                    }
                }
            }
        }
        if (now == null) {
            return;
        }
        outer:
        while (true) {
            List<Block> connectedRails = getConnectedRails(now);
            for (Block connectedRail : connectedRails) {
                if (!connectedRail.equals(lastBlock) && !connectedRail.equals(init)) {
                    lastBlock = now;
                    now = connectedRail;
                    continue outer;
                }
            }
            break;
        }
        Location location = event.getPlayer().getLocation();
        location.setX(now.getX() + location.getX() - location.getBlockX());
        location.setY(now.getY() + location.getY() - location.getBlockY());
        location.setZ(now.getZ() + location.getZ() - location.getBlockZ());
        event.getPlayer().teleport(location);
        event.getPlayer().getWorld().playSound(location, Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
    }

    public List<Block> getConnectedRails(Block outerBlock) {
        Rail.Shape outerShape = getShape(outerBlock);
        List<Block> blocks = new ArrayList<>();
        switch (outerShape) {
            case NORTH_SOUTH: {
                {
                    Block block = outerBlock.getRelative(BlockFace.NORTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.SOUTH_EAST ||
                            shape == Rail.Shape.SOUTH_WEST ||
                            shape == Rail.Shape.NORTH_SOUTH ||
                            shape == Rail.Shape.ASCENDING_NORTH
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_SOUTH)
                        blocks.add(block);
                }


                {
                    Block block = outerBlock.getRelative(BlockFace.SOUTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.NORTH_SOUTH ||
                            shape == Rail.Shape.ASCENDING_SOUTH
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_NORTH)
                        blocks.add(block);
                }

                break;
            }

            case EAST_WEST: {
                {
                    Block block = outerBlock.getRelative(BlockFace.EAST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.SOUTH_WEST ||
                            shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.ASCENDING_EAST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_WEST)
                        blocks.add(block);
                }

                {
                    Block block = outerBlock.getRelative(BlockFace.WEST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.SOUTH_EAST ||
                            shape == Rail.Shape.ASCENDING_WEST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_EAST)
                        blocks.add(block);
                }


                break;
            }
            case ASCENDING_EAST: {
                {
                    Block block = outerBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.UP);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.SOUTH_WEST ||
                            shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.ASCENDING_EAST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.WEST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.SOUTH_EAST ||
                            shape == Rail.Shape.ASCENDING_WEST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_WEST)
                        blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_EAST)
                        blocks.add(block);
                }
                break;
            }
            case ASCENDING_WEST: {
                {
                    Block block = outerBlock.getRelative(BlockFace.EAST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.SOUTH_WEST ||
                            shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.ASCENDING_EAST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.UP);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.SOUTH_EAST ||
                            shape == Rail.Shape.ASCENDING_WEST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_WEST)
                        blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_EAST)
                        blocks.add(block);
                }
                break;
            }
            case ASCENDING_NORTH: {
                {
                    Block block = outerBlock.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.SOUTH_EAST ||
                            shape == Rail.Shape.SOUTH_WEST ||
                            shape == Rail.Shape.NORTH_SOUTH ||
                            shape == Rail.Shape.ASCENDING_NORTH
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.SOUTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.NORTH_SOUTH ||
                            shape == Rail.Shape.ASCENDING_SOUTH
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_NORTH)
                        blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_SOUTH)
                        blocks.add(block);
                }
                break;
            }
            case ASCENDING_SOUTH: {
                {
                    Block block = outerBlock.getRelative(BlockFace.NORTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.SOUTH_EAST ||
                            shape == Rail.Shape.SOUTH_WEST ||
                            shape == Rail.Shape.NORTH_SOUTH ||
                            shape == Rail.Shape.ASCENDING_NORTH
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.NORTH_SOUTH ||
                            shape == Rail.Shape.ASCENDING_SOUTH
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_NORTH)
                        blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_SOUTH)
                        blocks.add(block);
                }
                break;
            }
            case SOUTH_EAST: {
                {
                    Block block = outerBlock.getRelative(BlockFace.SOUTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.NORTH_SOUTH ||
                            shape == Rail.Shape.ASCENDING_SOUTH
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_NORTH)
                        blocks.add(block);
                }

                {
                    Block block = outerBlock.getRelative(BlockFace.EAST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.SOUTH_WEST ||
                            shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.ASCENDING_EAST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_WEST)
                        blocks.add(block);
                }
                break;
            }
            case SOUTH_WEST: {
                {
                    Block block = outerBlock.getRelative(BlockFace.SOUTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.NORTH_SOUTH ||
                            shape == Rail.Shape.ASCENDING_SOUTH
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_NORTH)
                        blocks.add(block);
                }

                {
                    Block block = outerBlock.getRelative(BlockFace.WEST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.SOUTH_EAST ||
                            shape == Rail.Shape.ASCENDING_WEST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_EAST)
                        blocks.add(block);
                }
                break;
            }
            case NORTH_WEST: {
                {
                    Block block = outerBlock.getRelative(BlockFace.WEST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.NORTH_EAST ||
                            shape == Rail.Shape.SOUTH_EAST ||
                            shape == Rail.Shape.ASCENDING_WEST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_EAST)
                        blocks.add(block);
                }

            }
            case NORTH_EAST: {
                {
                    Block block = outerBlock.getRelative(BlockFace.NORTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.SOUTH_EAST ||
                            shape == Rail.Shape.SOUTH_WEST ||
                            shape == Rail.Shape.NORTH_SOUTH ||
                            shape == Rail.Shape.ASCENDING_NORTH
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_SOUTH)
                        blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.EAST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.NORTH_WEST ||
                            shape == Rail.Shape.SOUTH_WEST ||
                            shape == Rail.Shape.EAST_WEST ||
                            shape == Rail.Shape.ASCENDING_EAST
                    ) blocks.add(block);
                }
                {
                    Block block = outerBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST);
                    Rail.Shape shape = getShape(block);
                    if (shape == Rail.Shape.ASCENDING_WEST)
                        blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public Rail.Shape getShape(Block block) {
        if (block.getType().name().toLowerCase().contains("rail")) {
            Rail railData = (Rail) block.getBlockData();
            return railData.getShape();
        }
        return null;
    }
}
