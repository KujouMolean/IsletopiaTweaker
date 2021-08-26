package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class PlotChunkTask extends BukkitRunnable {

    private int X;
    private int Z;
    private final int bx;
    private final int bz;
    private final int tx;
    private final int tz;

    private final Consumer<Chunk> consumer;
    private final Runnable runnable;
    private int chunkPerTick;

    public PlotChunkTask(@NotNull Plot plot, @NotNull Consumer<Chunk> consumer, @Nullable Runnable then, int timeOutTicks) {
        com.plotsquared.core.location.Location bot = plot.getBottomAbs();
        com.plotsquared.core.location.Location top = plot.getTopAbs();
        World world = Bukkit.getWorld(Objects.requireNonNull(plot.getWorldName()));
        assert world != null;
        bx = bot.getX() >> 4;
        bz = bot.getZ() >> 4;
        tx = top.getX() >> 4;
        tz = top.getZ() >> 4;
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
            for (; X <= tx; X++) {
                for (; Z <= tz; Z++) {
                    if (count++ > chunkPerTick) {
                        return;
                    }
                    Chunk chunkAt = IsletopiaTweakers.getWorld().getChunkAt(X, Z);
                    consumer.accept(chunkAt);
                }
                Z = bz;
            }
            runnable.run();
            task.cancel();
        }, 1, 1);
    }
}
