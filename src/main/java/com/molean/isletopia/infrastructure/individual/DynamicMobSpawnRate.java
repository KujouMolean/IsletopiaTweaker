package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.BukkitRuntimeConfigUtils;
import com.molean.isletopia.utils.MSPTUtils;
import org.bukkit.entity.SpawnCategory;

public class DynamicMobSpawnRate {

    private int balance = 0;


    public static int getValGreater(int max, int min, int balance) {
        return (int) ((max - min) / (double) 100 * (100 - balance) + min);
    }

    public static int getValLess(int max, int min, int balance) {
        return (int) ((max - min) / (double) 100 * balance + min);
    }

//    public void setLimit(int val) {
//        BukkitRuntimeConfigUtils.setMonsterSpawn(getValGreater(70, 70, val));
//        BukkitRuntimeConfigUtils.setAnimalSpawn(getValGreater(10, 10, val));
//        BukkitRuntimeConfigUtils.setWaterAnimalSpawn(getValGreater(5, 5, val));
//        BukkitRuntimeConfigUtils.setWaterAmbientSpawn(getValGreater(20, 20, val));
//        BukkitRuntimeConfigUtils.setAmbientSpawn(getValGreater(15, 15, val));
//        BukkitRuntimeConfigUtils.setWaterUndergroundCreatureSpawn(getValGreater(5, 5, val));
//    }

    public void setRate(int val) {
        BukkitRuntimeConfigUtils.setWorld(SpawnCategory.MONSTER, getValLess(5, 1, val));
        BukkitRuntimeConfigUtils.setWorld(SpawnCategory.ANIMAL,getValLess(400, 400, val));
        BukkitRuntimeConfigUtils.setWorld(SpawnCategory.WATER_ANIMAL,getValLess(5, 1, val));
        BukkitRuntimeConfigUtils.setWorld(SpawnCategory.WATER_AMBIENT, getValLess(5, 1, val));
        BukkitRuntimeConfigUtils.setWorld(SpawnCategory.AMBIENT, getValLess(5, 1, val));
        BukkitRuntimeConfigUtils.setWorld(SpawnCategory.WATER_UNDERGROUND_CREATURE, getValLess(5, 1, val));

    }


    public DynamicMobSpawnRate() {
        Tasks.INSTANCE.interval(60 * 20, () -> {
            double v = MSPTUtils.get();
            if (v > 45) {
                balance += 5;
            } else {
                balance -= 5;
            }
            balance = (int) Math.min(balance, Math.max(v - 45, 20) * 5);
            if (balance > 100) {
                balance = 100;
            }
            if (balance < 0) {
                balance = 0;
            }
            setRate(balance);
//            setLimit(balance);
        });
    }
}
