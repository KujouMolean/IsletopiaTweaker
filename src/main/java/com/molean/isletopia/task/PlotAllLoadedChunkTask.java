package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.LocalIsland;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PlotAllLoadedChunkTask extends BukkitRunnable {

    private int X;
    private int Z;
    private final int bx;
    private final int bz;
    private final int tx;
    private final int tz;

    private final Consumer<Chunk> consumer;
    private final Runnable runnable;
    private int chunkPerTick;

    public PlotAllLoadedChunkTask( @NotNull LocalIsland plot, @NotNull Consumer<Chunk> consumer, @Nullable Runnable then, int timeOutTicks) {
        World skyWorld = Bukkit.getWorld("SkyWorld");
        Location bottomLocation = plot.getBottomLocation(skyWorld);
        Location topLocation = plot.getTopLocation(skyWorld);
        bx = bottomLocation.getBlockX() >> 4;
        bz = bottomLocation.getBlockZ() >> 4;
        tx = topLocation.getBlockX() >> 4;
        tz = topLocation.getBlockZ() >> 4;
        this.chunkPerTick = (int) Math.ceil(1024 / (double) timeOutTicks);
        if (chunkPerTick < 4) {
            this.chunkPerTick = 4;
        }
        this.consumer = consumer;
        this.runnable = then;
    }

    @Override
    public void run() {
        X = bx;
        Z = bz;
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), (task) -> {
            int count = 0;
            for (; X < tx; X++) {
                for (; Z < tz; Z++) {
                    if (count++ > chunkPerTick) {
                        return;
                    }
                    for (World world : Bukkit.getWorlds()) {
                        if (world.isChunkLoaded(X, Z)) {
                            Chunk chunkAt = world.getChunkAt(X, Z);
                            consumer.accept(chunkAt);
                        }
                    }

                }
                Z = bz;
            }
            runnable.run();
            task.cancel();
        }, 1, 1);
    }
}
