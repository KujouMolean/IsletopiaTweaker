package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class AnimalProtect implements Listener {
    private PlotAPI plotAPI;


    public AnimalProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (plotAPI == null) {
            plotAPI = new PlotAPI();
        }
        if (event.getEntity() instanceof Animals || event.getEntity() instanceof Villager) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                event.setCancelled(true);
                Projectile projectile = (Projectile) event.getDamager();
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player) {
                    Player player = (Player) shooter;
                    PlotPlayer plotPlayer = plotAPI.wrapPlayer(player.getUniqueId());
                    Plot currentPlot = plotPlayer.getCurrentPlot();
                    if (currentPlot != null) {
                        List<UUID> builder = new ArrayList<>();
                        UUID owner = currentPlot.getOwner();
                        builder.add(owner);
                        HashSet<UUID> trusted = currentPlot.getTrusted();
                        builder.addAll(trusted);
                        if (builder.contains(player.getUniqueId())) {
                            event.setCancelled(false);
                        }
                    }
                }
                return;
            }
            if (event.getDamager() instanceof Player) {
                event.setCancelled(true);
                Player player = (Player) event.getDamager();
                PlotPlayer plotPlayer = plotAPI.wrapPlayer(player.getUniqueId());
                Plot currentPlot = plotPlayer.getCurrentPlot();
                if (currentPlot != null) {
                    List<UUID> builder = new ArrayList<>();
                    UUID owner = currentPlot.getOwner();
                    builder.add(owner);
                    HashSet<UUID> trusted = currentPlot.getTrusted();
                    builder.addAll(trusted);
                    if (builder.contains(player.getUniqueId())) {
                        event.setCancelled(false);
                    }
                }
            }
        }
    }
}
