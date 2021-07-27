package com.molean.isletopia.infrastructure.individual;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class RespawnPoint implements Listener {


    public RespawnPoint() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    private static void cacheRespawnPointAsync(Player player) {
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (currentPlot == null) {
            return;
        }
        currentPlot.getHome(home -> {
            Location location = new Location(
                    Bukkit.getWorld(home.getWorldName()),
                    home.getX() + 0.5,
                    home.getY(),
                    home.getZ() + 0.5,
                    home.getYaw(),
                    home.getPitch());
            try (Jedis jedis = RedisUtils.getJedis()) {
                Map<String, Object> serialize = location.serialize();
                String s = new Gson().toJson(serialize);
                jedis.set(currentPlot.getId().toDashSeparatedString(), s);
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), (task) -> {
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
        assert currentPlot != null;
        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists(currentPlot.getId().toDashSeparatedString())) {
                String s = jedis.get(currentPlot.getId().toDashSeparatedString());
                Map map = new Gson().fromJson(s, Map.class);
                Location deserialize = Location.deserialize(map);
                event.setRespawnLocation(deserialize);
                return;
            }
        }
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            event.getPlayer().performCommand("is");
        });
    }
}
