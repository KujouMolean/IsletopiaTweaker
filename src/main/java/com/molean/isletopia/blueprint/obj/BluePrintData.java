package com.molean.isletopia.blueprint.obj;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BluePrintData implements Serializable {
    private final HashMap<Integer, String> blockDataMap;
    private final int[][][] data;
    public int[][][] getData() {
        return data;
    }
    public HashMap<Integer, String> getBlockDataMap() {
        return blockDataMap;
    }
    public BluePrintData(Location bot, Location top) {
        int a = top.getBlockX() - bot.getBlockX() + 1;
        int b = top.getBlockY() - bot.getBlockY() + 1;
        int c = top.getBlockZ() - bot.getBlockZ() + 1;
        HashMap<String, Integer> reverseMap = new HashMap<>();
        data = new int[a][b][c];
        blockDataMap = new HashMap<>();
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                for (int k = 0; k < c; k++) {
                    BlockData blockData = bot.clone().add(i, j, k).getBlock().getBlockData();
                    String asString = blockData.getAsString();
                    if (!reverseMap.containsKey(asString)) {
                        reverseMap.put(asString, reverseMap.size());
                        blockDataMap.put(blockDataMap.size(), asString);
                    }
                    data[i][j][k] = reverseMap.get(asString);
                }
            }
        }
    }
    public Map<Material, Integer> getMaterialMap() {
        Map<Integer, BlockData> localBlockDataCache = new HashMap<>();
        Map<Material, Integer> materialMap = new HashMap<>();
        for (int[][] datum : data) {
            for (int[] ints : datum) {
                for (int n : ints) {
                    if (!localBlockDataCache.containsKey(n)) {
                        BlockData blockData = Bukkit.createBlockData(blockDataMap.get(n));
                        localBlockDataCache.put(n, blockData);
                    }
                    BlockData blockData = localBlockDataCache.get(n);
                    Material material = blockData.getMaterial();
                    Integer orDefault = materialMap.getOrDefault(material, 0);
                    materialMap.put(material, orDefault + 1);
                }
            }
        }
        return materialMap;
    }


}
