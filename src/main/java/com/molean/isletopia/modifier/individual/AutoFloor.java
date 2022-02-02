package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.utils.Direction;
import com.molean.isletopia.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AutoFloor implements Listener {

    private final Set<Material> blackList = new HashSet<>();
    private final Set<Material> whiteList = new HashSet<>();

    public AutoFloor() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        for (Material value : Material.values()) {
            if (value.name().contains("_BED")) {
                blackList.add(value);
            }
            if (!value.isSolid()) {
                blackList.add(value);
            }
            if (!value.isBlock()) {
                blackList.add(value);
            }
            if (value.isOccluding()) {
                whiteList.add(value);
            }
            if (value.name().contains("_SLAB")) {
                whiteList.add(value);
            }
        }
    }


    @EventHandler
    public void on(PlayerMoveEvent event) {
        double v = event.getFrom().getY() - event.getTo().getY();
        double x = event.getFrom().getX() - event.getTo().getX();
        double z = event.getFrom().getZ() - event.getTo().getZ();
        if (v > 0.0001 || v < -0.0001) {
            return;
        }
        if (x == 0 && z == 0) {
            return;
        }
        if (!PlayerPropertyManager.INSTANCE.isLoad(event.getPlayer().getUniqueId())) {
            return;
        }
        if (!PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(event.getPlayer(), "AutoFloor")) {
            return;
        }
        PlayerInventory inventory = event.getPlayer().getInventory();
        ItemStack itemInOffHand = inventory.getItemInOffHand();
        if (!whiteList.contains(itemInOffHand.getType())) {
            if (blackList.contains(itemInOffHand.getType())) {
                return;
            }
        }
        Direction direction = Direction.getDirection(event.getPlayer());
        BlockFace blockFace = direction.toBlockFace();
        assert blockFace != null;
        Location location = event.getPlayer().getLocation();
        World world = location.getWorld();
        int maxHeight = world.getMaxHeight();
        int minHeight = world.getMinHeight();
        if (location.getBlockY() <= minHeight) {
            return;
        }
        if (location.getBlockY() >= maxHeight + 1) {
            return;
        }

        double y = location.getY();
        Block from;
        if (y - Math.floor(y) > 0.05) {
            from = location.getBlock();
        } else {
            from = location.getBlock().getRelative(BlockFace.DOWN);
        }
        Block front = from.getRelative(blockFace);
        Block back = from.getRelative(blockFace.getOppositeFace());
        BlockFace leftBlockFace;
        switch (blockFace) {
            case NORTH -> {
                leftBlockFace = BlockFace.WEST;
            }
            case EAST -> {
                leftBlockFace = BlockFace.NORTH;
            }
            case SOUTH -> {
                leftBlockFace = BlockFace.EAST;
            }
            case WEST -> {
                leftBlockFace = BlockFace.SOUTH;
            }
            default -> throw new IllegalStateException("Unexpected value: " + blockFace);
        }

        Block left = from.getRelative(leftBlockFace);
        Block right = from.getRelative(leftBlockFace.getOppositeFace());
        Block leftFrom = left.getRelative(blockFace);
        Block rightFrom = right.getRelative(blockFace);
        Block leftBack = left.getRelative(blockFace.getOppositeFace());
        Block rightBack = right.getRelative(blockFace.getOppositeFace());

        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(from);
        blocks.add(front);
        blocks.add(back);
        blocks.add(left);
        blocks.add(right);
        blocks.add(leftBack);
        blocks.add(leftFrom);
        blocks.add(rightBack);
        blocks.add(rightFrom);
        for (Block block : blocks) {
            if (!block.getType().isAir()) {
                continue;
            }
            if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), block.getLocation())) {
                continue;
            }
            if (!InventoryUtils.takeItem(event.getPlayer(), itemInOffHand.getType(), 1)) {
                continue;
            }
            block.setType(itemInOffHand.getType());
        }
    }

}
