package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.task.PlotLoadedChunkTask;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeaconIslandOption implements Listener {

    public BeaconIslandOption() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    private static final Map<IslandId, Set<PotionEffectType>> stringHashSetMap = new HashMap<>();
    private static final HashSet<IslandId> updatingPlot = new HashSet<>();

    public static void updatePrimaryBeaconEffects(LocalIsland plot) {
        if (updatingPlot.contains(plot.getIslandId())) {
            return;
        }
        updatingPlot.add(plot.getIslandId());
        HashSet<PotionEffectType> potionEffectTypes = new HashSet<>();
        new PlotLoadedChunkTask(plot, chunk -> {
            @NotNull BlockState[] tileEntities = chunk.getTileEntities();
            for (BlockState tileEntity : tileEntities) {
                if (tileEntity.getBlock().getType().equals(Material.BEACON)) {
                    Beacon beacon = (Beacon) tileEntity;
                    PotionEffect primaryEffect = beacon.getPrimaryEffect();
                    PotionEffect secondaryEffect = beacon.getSecondaryEffect();
                    if ( primaryEffect != null) {
                        potionEffectTypes.add(primaryEffect.getType());
                    }
                    if (secondaryEffect != null) {
                        potionEffectTypes.add(secondaryEffect.getType());
                    }
                }
            }
        }, () -> {
            stringHashSetMap.put(plot.getIslandId(), potionEffectTypes);
            updatingPlot.remove(plot.getIslandId());
        }, 20 * 15).run();
    }

    public static boolean isAntiFire(LocalIsland plot) {
        if (!stringHashSetMap.containsKey(plot.getIslandId())) {
            updatePrimaryBeaconEffects(plot);
            return true;
        }
        return stringHashSetMap.get(plot.getIslandId()).contains(PotionEffectType.DAMAGE_RESISTANCE);
    }


    @EventHandler(ignoreCancelled = true)
    public void on(BlockBurnEvent event) {
        org.bukkit.Location location = event.getBlock().getLocation();
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentPlot==null||isAntiFire(currentPlot)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BeaconActivatedEvent event) {
        org.bukkit.Location location = event.getBlock().getLocation();
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentPlot == null) {
            return;
        }
        updatePrimaryBeaconEffects(currentPlot);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BeaconDeactivatedEvent event) {
        org.bukkit.Location location = event.getBlock().getLocation();
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentPlot == null) {
            return;
        }
        updatePrimaryBeaconEffects(currentPlot);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerChangeBeaconEffectEvent event) {
        Block beacon = event.getBeacon();
        if (beacon == null) {
            return;
        }
        org.bukkit.Location location = beacon.getLocation();
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentPlot == null) {
            return;
        }
        updatePrimaryBeaconEffects(currentPlot);
    }
}
