package com.molean.isletopia.island.obj;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CuboidShape {
    private int[] data;
    private int a;
    private int b;
    private int c;
    private Map<Integer, String> map = new HashMap<>();
    private static final Gson gson = new Gson();


    public String serialize() {
        return gson.toJson(this);
    }

    public static CuboidShape deserialize(String string) {
        return gson.fromJson(string, CuboidShape.class);
    }

    public void setData(int[] data) {
        this.data = data;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setC(int c) {
        this.c = c;
    }

    public void setMap(Map<Integer, String> map) {
        this.map = map;
    }

    public CuboidShape() {
    }

    public CuboidShape(Set<Block> blockSet) {
        blockSet.removeIf(blockData -> blockData.getType().isAir());
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (Block block : blockSet) {
            minX = Math.min(block.getLocation().getBlockX(), minX);
            minY = Math.min(block.getLocation().getBlockY(), minY);
            minZ = Math.min(block.getLocation().getBlockZ(), minZ);

            maxX = Math.max(block.getLocation().getBlockX(), maxX);
            maxY = Math.max(block.getLocation().getBlockY(), maxY);
            maxZ = Math.max(block.getLocation().getBlockZ(), maxZ);
        }
        a = maxX - minX + 1;
        b = maxY - minY + 1;
        c = maxZ - minZ + 1;
        data = new int[a * b * c];


        Map<String, Integer> reverseMap = new HashMap<>();

        BlockData airBlockData = Bukkit.createBlockData(Material.AIR);
        map.put(0, airBlockData.getAsString());
        reverseMap.put(airBlockData.getAsString(), 0);

        int nextIndex = 1;

        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                for (int k = 0; k < c; k++) {
                    data[i * b * c + j * c + k] = 0;
                }
            }
        }

        for (Block block : blockSet) {
            int x = block.getLocation().getBlockX() - minX;
            int y = block.getLocation().getBlockY() - minY;
            int z = block.getLocation().getBlockZ() - minZ;
            String asString = block.getBlockData().getAsString();

            if (reverseMap.containsKey(asString)) {
                data[x * b*c+ y * c + z] = reverseMap.get(asString);
            } else {
                map.put(nextIndex, asString);
                reverseMap.put(asString, nextIndex);
                data[x * b*c + y * c + z] = nextIndex;
                nextIndex++;
            }
        }
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public BlockData getBlock(int x, int y, int z) {
        return Bukkit.createBlockData(map.get(data[x * b*c + y * c + z]));
    }

    public int[] getData() {
        return data;
    }

    public Map<Integer, String> getMap() {
        return map;
    }

    private transient int i, j, k;

    public void put(Location location, int blockPerTick, Runnable runnable) {

        i = 0;
        j = 0;
        k = 0;

        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), (task) -> {
            boolean end = true;
            int count = 0;
            for (; i < a; i++) {
                for (; j < b; j++) {
                    for (; k < c; k++) {
                        location.clone().add(i, j, k).getBlock().setBlockData(getBlock(i, j, k));
                        end = false;
                        if (++count >= blockPerTick) {
                            return;
                        }
                    }
                    k = 0;
                }
                j = 0;
            }
            if (end) {
                task.cancel();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }, 1, 1);
    }
}
