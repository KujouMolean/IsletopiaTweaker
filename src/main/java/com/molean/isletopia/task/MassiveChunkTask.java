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

    public MassiveChunkTask(Chunk[] chunks, Consumer<Chunk> consumer) {
        this(chunks, consumer, 60);
    }

    public MassiveChunkTask(Chunk[] chunks, Consumer<Chunk> consumer, int timeOutTicks) {
        this.chunkConsumer = consumer;
        this.chunkPerTick = (int) Math.ceil(chunks.length / (double) timeOutTicks);

        if (chunkPerTick < 4) {
            this.chunkPerTick = 4;
        }

        this.totalTickTime = (int) Math.ceil(chunks.length / (double) chunkPerTick);

        this.currentTick = 0;


        this.chunks = chunks;
    }

    public void run() {
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), (task) -> {
            for (int i = 0; i < chunkPerTick; i++) {
                int chunkIndex = currentTick * chunkPerTick + i;
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
