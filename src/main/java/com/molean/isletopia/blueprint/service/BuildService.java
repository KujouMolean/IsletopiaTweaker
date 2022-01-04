package com.molean.isletopia.blueprint.service;

import com.molean.isletopia.blueprint.obj.BluePrintData;
import com.molean.isletopia.blueprint.obj.MaterialContainerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;

public enum BuildService {
    INSTANCE;

    public enum Result{
        SUCCESS,BLOCK_CONFLICT,OVER_ISLAND,NO_MATERIAL
    }

    public void build(BluePrintData bluePrintData, MaterialContainerImpl materialContainer, Location bot) {
        Map<Integer, BlockData> localBlockDataCache = new HashMap<>();
        for (int i = 0; i < bluePrintData.getData().length; i++) {
            for (int j = 0; j < bluePrintData.getData()[i].length; j++) {
                for (int k = 0; k < bluePrintData.getData()[i][j].length; k++) {
                    int n = bluePrintData.getData()[i][j][k];
                    if (!localBlockDataCache.containsKey(n)) {
                        BlockData blockData = Bukkit.createBlockData(bluePrintData.getBlockDataMap().get(n));
                        localBlockDataCache.put(n, blockData);
                    }
                    BlockData blockData = localBlockDataCache.get(n);
                    Integer left = materialContainer.getStoredMaterial().getOrDefault(blockData.getMaterial(), 0);
                    if (left <= 0) {
                        continue;
                    }
                    materialContainer.getStoredMaterial().put(blockData.getMaterial(), left - 1);
                    bot.clone().add(i, j, k).getBlock().setBlockData(blockData);
                }
            }
        }
    }
}
