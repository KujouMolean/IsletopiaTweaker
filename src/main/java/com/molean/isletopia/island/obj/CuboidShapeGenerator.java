package com.molean.isletopia.island.obj;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class CuboidShapeGenerator {
    public CuboidShape generate(Player player) {
        Location location = player.getLocation();
        //find gold block
        Location bot = null, top = null;

        int distance = 1;

        outer:
        while (distance++ < 100) {
            for (int i = 0; i < distance; i++) {
                for (int j = 0; j < distance; j++) {
                    for (int k = 0; k < distance; k++) {
                        Location minus = location.clone().add(-i, -j, -k);
                        Location add = location.clone().add(i, j, k);
                        if (bot == null && minus.getBlock().getType().equals(Material.GOLD_BLOCK)) {
                            minus.getBlock().setType(Material.AIR);
                            bot = minus;
                        }

                        if (top == null && add.getBlock().getType().equals(Material.GOLD_BLOCK)) {
                            add.getBlock().setType(Material.AIR);
                            top = add;
                        }

                        if (top != null && bot != null) {
                            break outer;
                        }
                    }
                }
            }
        }
        if (bot == null || top == null) {
            return null;
        }
        HashSet<Block> blocks = new HashSet<>();
        for (int i = 0; i <= top.getBlockX() - bot.getBlockX(); i++) {
            for (int j = 0; j <= top.getBlockY() - bot.getBlockY(); j++) {
                for (int k = 0; k <= top.getBlockZ() - bot.getBlockZ(); k++) {
                    Location add = bot.clone().add(i, j, k);
                    blocks.add(add.getBlock());
                }
            }
        }
        return new CuboidShape(blocks);
    }

}
