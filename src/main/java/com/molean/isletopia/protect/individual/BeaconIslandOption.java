package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeaconIslandOption implements Listener {

    public BeaconIslandOption() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (PlotUtils.hasCurrentPlotPermission(onlinePlayer)) {
                    Plot currentPlot = PlotUtils.getCurrentPlot(onlinePlayer);
                    if (isEnablePvP(currentPlot)) {
                        onlinePlayer.sendActionBar(Component.text("§c此岛屿已开启PVP, 生物不受保护!"));

                    }
                }
            }
        }, 60 * 20, 60 * 3 * 20);
    }

    private static final Map<PlotId, Set<PotionEffectType>> stringHashSetMap = new HashMap<>();
    private static final Map<PlotId, Long> lastUpdate = new HashMap<>();


    public static Set<PotionEffectType> getPrimaryBeaconEffects(Plot plot) {
        if (System.currentTimeMillis() - lastUpdate.getOrDefault(plot.getId(),0L) < 60 * 1000) {
            if (stringHashSetMap.containsKey(plot.getId())) {
                return stringHashSetMap.get(plot.getId());
            }
        }
        Set<PotionEffectType> effectTypes = new HashSet<>();
        Set<Chunk> chunks = PlotUtils.getPlotChunks(plot);
        for (Chunk chunk : chunks) {
            @NotNull BlockState[] tileEntities = chunk.getTileEntities();
            for (BlockState tileEntity : tileEntities) {
                if (tileEntity.getBlock().getType().equals(Material.BEACON)) {
                    Beacon beacon = (Beacon) tileEntity;
                    PotionEffect primaryEffect = beacon.getPrimaryEffect();
                    PotionEffect secondaryEffect = beacon.getSecondaryEffect();
                    if (secondaryEffect == null && primaryEffect != null) {
                        effectTypes.add(primaryEffect.getType());
                    }
                }
            }
        }
        lastUpdate.put(plot.getId(), System.currentTimeMillis());
        stringHashSetMap.put(plot.getId(), effectTypes);
        return effectTypes;
    }

    public static boolean isAntiFire(Plot plot) {
        return getPrimaryBeaconEffects(plot).contains(PotionEffectType.DAMAGE_RESISTANCE);
    }

    public static boolean isEnablePvP(Plot plot) {
        return getPrimaryBeaconEffects(plot).contains(PotionEffectType.INCREASE_DAMAGE);
    }

    @EventHandler
    public void on(BlockBurnEvent event) {
        org.bukkit.Location location = event.getBlock().getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        if (isAntiFire(currentPlot)) {
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
        lastUpdate.put(currentPlot.getId(), 0L);
    }

    @EventHandler
    public void on(BeaconActivatedEvent event) {
        org.bukkit.Location location = event.getBeacon().getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        lastUpdate.put(currentPlot.getId(), 0L);
    }

    @EventHandler
    public void on(BeaconDeactivatedEvent event) {
        org.bukkit.Location location = event.getBlock().getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        lastUpdate.put(currentPlot.getId(), 0L);
    }
}
