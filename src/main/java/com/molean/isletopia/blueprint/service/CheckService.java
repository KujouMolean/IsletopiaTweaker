package com.molean.isletopia.blueprint.service;

import com.molean.isletopia.blueprint.obj.BluePrintData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;

public class CheckService {
    public enum Result{
        OK,OVER_HEIGHT,OVER_ISLAND,BLOCK_CONFLICT
    }

    public static Result checkPlace(BluePrintData bluePrintData, Location bot) {
        Map<Integer, BlockData> localBlockDataCache = new HashMap<>();
        int maxHeight = bot.getWorld().getMaxHeight();
        if (bot.getBlockY() + bluePrintData.getData()[0].length > maxHeight) {
            return Result.OVER_HEIGHT;
        }
        int x1 = bot.getBlockX() / 512;
        int x2 = (bot.getBlockX() + bluePrintData.getData().length) / 512;
        int z1 = bot.getBlockZ() / 512;
        int z2 = (bot.getBlockZ() + bluePrintData.getData()[0][0].length) / 512;
        if (x1 != x2 || z1 != z2) {
            return Result.OVER_ISLAND;
        }
        for (int i = 0; i < bluePrintData.getData().length; i++) {
            for (int j = 0; j < bluePrintData.getData()[i].length; j++) {
                for (int k = 0; k < bluePrintData.getData()[i][j].length; k++) {
                    int n = bluePrintData.getData()[i][j][k];
                    if (!localBlockDataCache.containsKey(n)) {
                        BlockData blockData = Bukkit.createBlockData(bluePrintData.getBlockDataMap().get(n));
                        localBlockDataCache.put(n, blockData);
                    }
                    BlockData blockData = localBlockDataCache.get(n);
                    if (!bot.clone().add(i, j, k).getBlock().getType().isAir()) {
                        if (!blockData.getMaterial().isAir()) {
                            return Result.BLOCK_CONFLICT;
                        }
                    }
                }
            }
        }
        return Result.OK;
    }
}
