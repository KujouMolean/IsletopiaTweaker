package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.function.Consumer;

public class MassiveChunkTask extends BukkitRunnable {
    private final Chunk[] chunks;
    private final Consumer<Chunk> chunkConsumer;

    private int chunkPerTick;
    private final int totalTickTime;
    private int currentTick;

    public MassiveChunkTask(Set<Chunk> chunkSet, Consumer<Chunk> consumer) {
        this(chunkSet, consumer, 60);
    }

    public MassiveChunkTask(Set<Chunk> chunkSet, Consumer<Chunk> consumer, int timeOutTicks) {
        this.chunkConsumer = consumer;
        this.chunkPerTick = (int) Math.ceil(chunkSet.size() / (double) timeOutTicks);

        if (chunkPerTick < 4) {
            this.chunkPerTick = 4;
        }

        this.totalTickTime = (int) Math.ceil(chunkSet.size() / (double) chunkPerTick);

        this.currentTick = 0;


        this.chunks = chunkSet.toArray(new Chunk[0]);
    }

    public void run() {
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), (task) -> {
            for (int i = 0; i < totalTickTime; i++) {
                int chunkIndex = i * chunkPerTick + currentTick;
                if (chunkIndex < chunks.length) {
                    chunkConsumer.accept(chunks[chunkIndex]);
                }
            }
            currentTick++;
            if (currentTick > totalTickTime) {
                task.cancel();
            }
        }, 1, 1);
    }
}
