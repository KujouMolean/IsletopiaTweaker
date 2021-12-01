package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class RegionUnloadedTask implements Runnable {

    private static final int CHECK_INTERVAL = 20;

    private int timeoutTicks = 60 * 20;
    private Runnable timeoutTask = () -> {
    };
    private Runnable task = () -> {
    };
    private final int regionX, regionZ;
    private final World world;

    public RegionUnloadedTask(World world, int regionX, int regionZ) {
        this.world = world;
        this.regionX = regionX;
        this.regionZ = regionZ;
    }

    public RegionUnloadedTask timeoutTask(Runnable runnable) {
        timeoutTask = runnable;
        return this;
    }

    public RegionUnloadedTask task(Runnable runnable) {
        task = runnable;
        return this;
    }

    public RegionUnloadedTask timeoutTicks(int timeoutTicks) {
        this.timeoutTicks = timeoutTicks;
        return this;
    }


    @Override
    public void run() {
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), new Consumer<>() {
            int i = 0;

            @Override
            public void accept(BukkitTask bukkitTask) {
                if (i * CHECK_INTERVAL > timeoutTicks) {
                    bukkitTask.cancel();
                    timeoutTask.run();
                    return;
                }
                i++;
                int xStart = regionX << 5;
                int zStart = regionZ << 5;
                int xEnd = (regionX + 1) << 5;
                int zEnd = (regionZ + 1) << 5;
                for (int i = xStart; i < xEnd; i++) {
                    for (int j = zStart; j < zEnd; j++) {
                        if (world.isChunkLoaded(i, j)) {
                            return;
                        }
                    }
                }
                bukkitTask.cancel();
                task.run();
            }
        }, 0, CHECK_INTERVAL);
    }
}
