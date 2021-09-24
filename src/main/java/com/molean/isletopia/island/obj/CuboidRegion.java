package com.molean.isletopia.island.obj;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CuboidRegion {
    private Location bot;
    private Location top;

    public CuboidRegion(Location first, Location second) {
        int maxX = Math.max(first.getBlockX(), second.getBlockX());
        int maxY = Math.max(first.getBlockY(), second.getBlockY());
        int maxZ = Math.max(first.getBlockZ(), second.getBlockZ());

        int minX = Math.min(first.getBlockX(), second.getBlockX());
        int minY = Math.min(first.getBlockY(), second.getBlockY());
        int minZ = Math.min(first.getBlockZ(), second.getBlockZ());

        bot = new Location(first.getWorld(), minX, minY, minZ);
        top = new Location(first.getWorld(), maxX, maxY, maxZ);
    }

    public void forEach(Consumer<Block> blockConsumer) {
        for (int i = bot.getBlockX(); i < top.getBlockX(); i++) {
            for (int j = bot.getBlockY(); j < top.getBlockY(); j++) {
                for (int k = bot.getBlockZ(); k < top.getBlockZ(); k++) {
                    blockConsumer.accept(bot.getWorld().getBlockAt(i, j, k));
                }
            }
        }
    }

    public void clearInstantly() {
        forEach(block -> block.setType(Material.AIR));
    }


    public boolean contains(Location location) {
        if (location.getBlockX() < this.getBot().getBlockX()) {
            return false;
        }
        if (location.getBlockX() > this.getTop().getBlockX()) {
            return false;
        }

        if (location.getBlockY() < this.getBot().getBlockY()) {
            return false;
        }
        if (location.getBlockY() > this.getTop().getBlockY()) {
            return false;
        }

        if (location.getBlockZ() < this.getBot().getBlockZ()) {
            return false;
        }
        if (location.getBlockZ() > this.getTop().getBlockZ()) {
            return false;
        }
        return true;
    }

    public void kill(Predicate<Entity> filter) {
        int bx = bot.getBlockX() >> 4;
        int bz = bot.getBlockZ() >> 4;
        int tx = top.getBlockX() >> 4;
        int tz = top.getBlockZ() >> 4;
        for (int X = bx; X <= tx; X++) {
            for (int Z = bz; Z <= tz; Z++) {
                Chunk chunkAt = bot.getWorld().getChunkAt(bx, bz);
                for (Entity entity : chunkAt.getEntities()) {
                    if (contains(entity.getLocation())) {
                        entity.remove();
                    }
                }
            }
        }
    }

    public void applyCenter(CuboidShape cuboidShape, int blockPerTick, Runnable runnable) {
        int x = (top.getBlockX() - bot.getBlockX()) / 2 + bot.getBlockX() - cuboidShape.getA() / 2;
        int y = (top.getBlockY() - bot.getBlockY()) / 2 + bot.getBlockY() - cuboidShape.getB() / 2;
        int z = (top.getBlockZ() - bot.getBlockZ()) / 2 + bot.getBlockZ() - cuboidShape.getC() / 2;
        Location location = new Location(bot.getWorld(), x, y, z);
        cuboidShape.put(location, blockPerTick, runnable);
    }

    private int i,j, k;

    public void forEachBlockLimited(int blocksPerTick, Consumer<Block> consumer,Runnable runnable) {
        i = bot.getBlockX();
        j = bot.getBlockY();
        k = bot.getBlockZ();
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), (task) -> {
            boolean end = true;
            int count = 0;
            for (; i < top.getBlockX(); i++) {
                for (; j < top.getBlockY(); j++) {
                    for (; k < top.getBlockZ(); k++) {
                        Block blockAt = bot.getWorld().getBlockAt(i, j, k);
                        consumer.accept(blockAt);
                        end = false;
                        if (++count >= blocksPerTick) {
                            return;
                        }
                    }
                    k = bot.getBlockZ();
                }
                j = bot.getBlockY();
            }
            if (end) {
                task.cancel();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }, 1, 1);
    }


    public Location getBot() {
        return bot;
    }

    public void setBot(Location bot) {
        this.bot = bot;
    }

    public Location getTop() {
        return top;
    }

    public void setTop(Location top) {
        this.top = top;
    }
}
