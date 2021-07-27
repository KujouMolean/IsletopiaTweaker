package com.molean.isletopia.protect.individual;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import io.papermc.paper.event.player.PlayerNameEntityEvent;
import io.papermc.paper.event.player.PlayerPurchaseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class OtherProtect implements Listener {
    public OtherProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerTakeLecternBookEvent event) {
        if(event.getPlayer().isOp()){
            return;
        }
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(BeaconEffectEvent event) {
        if(event.getPlayer().isOp()){
            return;
        }
        Location location = event.getBlock().getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        if (!PlotUtils.hasPlotPermission(currentPlot, event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        if(event.getPlayer().isOp()){
            return;
        }
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.ALLOW);

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack item = event.getItem();
            if(item!=null&&item.getType().equals(Material.FIREWORK_ROCKET)){
                return;
            }
        }

        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerFishEvent event) {
        if (BeaconIslandOption.isEnablePvP(PlotUtils.getCurrentPlot(event.getPlayer()))) {
            return;
        }
        if(event.getPlayer().isOp()){
            return;
        }
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerShearBlockEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerShearEntityEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerNameEntityEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerHarvestBlockEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerFlowerPotManipulateEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerPurchaseEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerChangeBeaconEffectEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerLaunchProjectileEvent event) {
        boolean fireball = event.getProjectile().getType().equals(EntityType.FIREWORK);
        if(fireball){
            return;
        }

        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
