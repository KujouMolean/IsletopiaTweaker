package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;

public class HungerKeeper implements Listener {
    private static final Map<String, Pair<Integer, Float>> hungerMap = new HashMap<>();

    public HungerKeeper() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Pair<Integer, Float> hunger = new Pair<>(player.getFoodLevel(), player.getSaturation());
        hungerMap.put(player.getName(), hunger);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Pair<Integer, Float> hunger = hungerMap.get(player.getName());
        if (hunger != null) {
            Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
                if (hunger.getKey() < 6) {
                    player.setFoodLevel(6);
                } else {
                    player.setFoodLevel(hunger.getKey());
                }
                player.setSaturation(hunger.getValue());
            },3L);
        }
    }
}
