package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BeaconIslandOption implements Listener {
    public BeaconIslandOption() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    private final Map<PlotId, Boolean> antiFireMap = new HashMap<>();
    private final Map<PlotId, Long> cachedTime = new HashMap<>();

    public boolean hasBeaconWithDamageResistance(Plot plot) {
        if (antiFireMap.containsKey(plot.getId())) {
            if (System.currentTimeMillis() - cachedTime.getOrDefault(plot.getId(), 0L) < 60 * 1000) {
                return antiFireMap.get(plot.getId());
            }
        }
        long start = System.currentTimeMillis();
        Location bot = plot.getBottomAbs();
        Location top = plot.getTopAbs();
        World world = Bukkit.getWorld(Objects.requireNonNull(plot.getWorldName()));
        assert world != null;
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
            @NotNull BlockState[] tileEntities = chunk.getTileEntities();
            for (BlockState tileEntity : tileEntities) {
                if (tileEntity.getBlock().getType().equals(Material.BEACON)) {
                    Beacon beacon = (Beacon) tileEntity;
                    PotionEffect primaryEffect = beacon.getPrimaryEffect();
                    PotionEffect secondaryEffect = beacon.getSecondaryEffect();
                    if (secondaryEffect == null && primaryEffect != null &&
                            primaryEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                        cachedTime.put(plot.getId(), System.currentTimeMillis());
                        antiFireMap.put(plot.getId(), true);
                        long end = System.currentTimeMillis();
                        Bukkit.getLogger().info("统计岛屿信标花费: " + (end - start));
                        return true;
                    }
                }

            }
        }
        cachedTime.put(plot.getId(), System.currentTimeMillis());
        antiFireMap.put(plot.getId(), false);
        long end = System.currentTimeMillis();
        Bukkit.getLogger().info("统计岛屿信标花费: " + (end - start));
        return false;
    }

    @EventHandler
    public void on(BlockBurnEvent event) {
        org.bukkit.Location location = event.getBlock().getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        if (hasBeaconWithDamageResistance(currentPlot)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerChangeBeaconEffectEvent event) {
        if (event.getBeacon() == null) {
            return;
        }

        org.bukkit.Location location = event.getBeacon().getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        cachedTime.put(currentPlot.getId(), 0L);
    }

    @EventHandler
    public void on(BeaconActivatedEvent event) {
        org.bukkit.Location location = event.getBeacon().getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        cachedTime.put(currentPlot.getId(), 0L);
    }

    @EventHandler
    public void on(BeaconDeactivatedEvent event) {
        org.bukkit.Location location = event.getBlock().getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        cachedTime.put(currentPlot.getId(), 0L);
    }


}
