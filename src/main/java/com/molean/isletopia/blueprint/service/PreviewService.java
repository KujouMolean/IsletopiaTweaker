package com.molean.isletopia.blueprint.service;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.blueprint.obj.BluePrintData;
import com.molean.isletopia.blueprint.obj.MaterialContainerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PreviewService {
    public enum Result{
        SUCCESS,BLOCK_CONFLICT,OVER_ISLAND
    }


    public static Result preview(Player player, BluePrintData bluePrintData, MaterialContainerImpl materialContainer, Location bot, int previewTicks, boolean full) {
        Map<Integer, BlockData> localBlockDataCache = new HashMap<>();
        HashMap<Material, Integer> localStoredMaterial = new HashMap<>(materialContainer.getStoredMaterial());
        for (int i = 0; i < bluePrintData.getData().length; i++) {
            for (int j = 0; j < bluePrintData.getData()[i].length; j++) {
                for (int k = 0; k < bluePrintData.getData()[i][j].length; k++) {
                    int n = bluePrintData.getData()[i][j][k];
                    if (!localBlockDataCache.containsKey(n)) {
                        BlockData blockData = Bukkit.createBlockData(bluePrintData.getBlockDataMap().get(n));
                        localBlockDataCache.put(n, blockData);
                    }
                    BlockData blockData = localBlockDataCache.get(n);
                    if (!full) {
                        Integer left = localStoredMaterial.getOrDefault(blockData.getMaterial(), 0);
                        if (left <= 0) {
                            continue;
                        }
                        localStoredMaterial.put(blockData.getMaterial(), left - 1);
                    }

                    player.sendBlockChange(bot.clone().add(i, j, k), blockData);
                }
            }
        }
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
            for (int i = 0; i < bluePrintData.getData().length; i++) {
                for (int j = 0; j < bluePrintData.getData()[i].length; j++) {
                    for (int k = 0; k < bluePrintData.getData()[i][j].length; k++) {
                        player.sendBlockChange(bot.clone().add(i, j, k), bot.clone().add(i, j, k).getBlock().getBlockData());
                    }
                }
            }
        }, previewTicks);
        return Result.SUCCESS;
    }
}
