package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;

public class RespawnPoint implements Listener {


    public RespawnPoint() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public static final Map<PlotId, Location> cache = new HashMap<>();


    private static void cacheRespawnPointAsync(Player player) {
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (currentPlot == null) {
            return;
        }
        currentPlot.getHome(home -> {
            Location location = new Location(
                    Bukkit.getWorld(home.getWorld()),
                    home.getX() + 0.5,
                    home.getY(),
                    home.getZ() + 0.5,
                    home.getYaw(),
                    home.getPitch());
            cache.put(currentPlot.getId(), location);
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), (task) -> {
            if (!player.isOnline()) {
                task.cancel();
            }
            cacheRespawnPointAsync(player);
        }, 0, 1200);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        cacheRespawnPointAsync(event.getEntity());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Plot currentPlot = PlotUtils.getCurrentPlot(event.getPlayer());

        if (cache.containsKey(currentPlot.getId())) {
            event.setRespawnLocation(cache.get(currentPlot.getId()));
            return;
        }

        com.plotsquared.core.location.Location home = currentPlot.getHomeSynchronous();
        Location location = new Location(
                event.getPlayer().getWorld(),
                home.getX() + 0.5,
                home.getY(),
                home.getZ() + 0.5,
                home.getYaw(),
                home.getPitch());
        cache.put(currentPlot.getId(), location);
        event.setRespawnLocation(location);
    }
}
