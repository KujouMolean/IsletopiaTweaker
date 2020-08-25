package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class PlotMobCap implements Listener {

    public PlotMobCap() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        PlotArea plotAreaAbs = PlotSquared.get().getPlotAreaAbs(BukkitUtil.getLocation(event.getLocation()));
        Plot plot = plotAreaAbs.getPlotAbs(BukkitUtil.getLocation(event.getLocation()));
        if (plot == null) {
            return;
        }
        EntityType entityType = event.getEntity().getType();
        if (entityType == EntityType.ZOMBIFIED_PIGLIN && countEntities(plot, EntityType.ZOMBIFIED_PIGLIN) >= 15) {
            event.setCancelled(true);
        }
        if (entityType == EntityType.PIGLIN && countEntities(plot, EntityType.PIGLIN) >= 15) {
            event.setCancelled(true);
        }
        if (entityType == EntityType.HOGLIN && countEntities(plot, EntityType.HOGLIN) >= 15) {
            event.setCancelled(true);
        }
        if (entityType == EntityType.ZOGLIN && countEntities(plot, EntityType.ZOGLIN) >= 15) {
            event.setCancelled(true);
        }
        if (entityType == EntityType.MAGMA_CUBE && countEntities(plot, EntityType.MAGMA_CUBE) >= 15) {
            if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT)) {
                return;
            }
            event.setCancelled(true);
        }
        if (entityType == EntityType.GHAST && countEntities(plot, EntityType.GHAST) >= 15) {
            event.setCancelled(true);
        }
        if (entityType == EntityType.STRIDER && countEntities(plot, EntityType.STRIDER) >= 15) {
            event.setCancelled(true);
        }
        if (entityType == EntityType.GUARDIAN && countEntities(plot, EntityType.GUARDIAN) >= 15) {
            event.setCancelled(true);
        }
    }

    public static int countEntities(@NotNull Plot plot,@NotNull EntityType entityType) {
        String typeString = entityType.toString();
        Integer prevCount = (Integer) plot.getMeta("Isletopia-Cap-" + typeString);
        Long prevTime = (Long) plot.getMeta("Isletopia-Cap" + typeString + "-Time");
        if (prevTime != null && System.currentTimeMillis() - prevTime < 1e3 && prevCount != null) {
            return prevCount;
        }

        int count = 0;
        PlotArea area = plot.getArea();
        World world = BukkitUtil.getWorld(area.getWorldName());

        Location bot = plot.getBottomAbs();
        Location top = plot.getTopAbs();
        int bx = bot.getX() >> 4;
        int bz = bot.getZ() >> 4;

        int tx = top.getX() >> 4;
        int tz = top.getZ() >> 4;

        Set<Chunk> chunks = new HashSet<>();
        for (int X = bx; X <= tx; X++) {
            for (int Z = bz; Z <= tz; Z++) {
                if (world.isChunkLoaded(X, Z)) {
                    chunks.add(world.getChunkAt(X, Z));
                }
            }
        }

        for (Chunk chunk : chunks) {
            for (Entity chunkEntity : chunk.getEntities()) {
                if (chunkEntity.getType() == entityType) {
                    count++;
                }
            }
        }
        plot.setMeta("Isletopia-Cap-" + typeString, count);
        plot.setMeta("Isletopia-Cap" + typeString + "-Time", System.currentTimeMillis());
        return count;
    }
}
