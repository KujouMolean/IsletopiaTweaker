package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlotMobCap implements Listener {

    private static final Map<EntityType, Integer> map = new HashMap<>();


    public static void setMobCap(EntityType entityType, Integer integer) {
        map.put(entityType, integer);
    }

    public static void unsetMobCap(EntityType entityType) {
        map.remove(entityType);
    }

    public PlotMobCap() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        setMobCap(EntityType.ZOMBIFIED_PIGLIN, 15);
        setMobCap(EntityType.PIGLIN, 15);
        setMobCap(EntityType.HOGLIN, 15);
        setMobCap(EntityType.ZOGLIN, 15);
        setMobCap(EntityType.MAGMA_CUBE, 15);

        setMobCap(EntityType.GHAST, 15);
        setMobCap(EntityType.STRIDER, 15);
        setMobCap(EntityType.GUARDIAN, 15);
        setMobCap(EntityType.SPIDER, 15);

        setMobCap(EntityType.COD, 30);
        setMobCap(EntityType.TROPICAL_FISH, 30);
        setMobCap(EntityType.SALMON, 30);

    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        PlotArea plotAreaAbs = PlotSquared.get().getPlotAreaAbs(BukkitUtil.getLocation(event.getLocation()));
        Plot plot = plotAreaAbs.getPlotAbs(BukkitUtil.getLocation(event.getLocation()));
        if (plot == null) {
            return;
        }
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SLIME_SPLIT)) {
            return;
        }
        EntityType entityType = event.getEntity().getType();
        if (map.containsKey(entityType)) {
            if (countEntities(plot, entityType) >= map.get(entityType)) {
                event.setCancelled(true);
            }
        }
    }

    public static int countEntities(@NotNull Plot plot, @NotNull EntityType entityType) {
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
