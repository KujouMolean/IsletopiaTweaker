package com.molean.isletopia.protect.individual;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeaconLimiter implements Listener {
    private static final List<String> denied = new ArrayList<>();
    private static final Map<String, Long> notifyTime = new ConcurrentHashMap<>();

    public BeaconLimiter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            List<String> parameterAsList = UniversalParameter.getParameterAsList("Molean", "beacon");
            if (!parameterAsList.contains(event.getPlayer().getName())) {
                if (event.getPlayer().isOnline()) {
                    denied.add(event.getPlayer().getName());
                }
            }
        });
    }

    @EventHandler
    public void onLeft(PlayerQuitEvent event) {
        denied.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void on(BeaconEffectEvent event) {
        if (!denied.contains(event.getPlayer().getName())) {
            return;
        }
        event.setCancelled(true);
        Long lastTime = notifyTime.getOrDefault(event.getPlayer().getName(), 0L);
        if (System.currentTimeMillis() - lastTime > 60 * 1000) {
            notifyTime.put(event.getPlayer().getName(), System.currentTimeMillis());
            event.getPlayer().sendMessage("§c抱歉, 你没有权限获得信标效果!");
        }
    }
}
