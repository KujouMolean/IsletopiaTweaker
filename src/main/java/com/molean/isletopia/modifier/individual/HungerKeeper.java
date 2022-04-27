package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class HungerKeeper implements Listener {
    private static final Map<UUID, Pair<Integer, Float>> hungerMap = new HashMap<>();
    private static final Map<UUID, Collection<PotionEffect>> effect = new HashMap<>();
    public HungerKeeper() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Pair<Integer, Float> hunger = new Pair<>(player.getFoodLevel(), player.getSaturation());
        hungerMap.put(player.getUniqueId(), hunger);
        effect.put(player.getUniqueId(), player.getActivePotionEffects());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Pair<Integer, Float> hunger = hungerMap.get(player.getUniqueId());
        if (hunger != null) {
            Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
                player.setFoodLevel(hunger.getKey());
                player.setSaturation(hunger.getValue());
                Collection<PotionEffect> potionEffects = effect.getOrDefault(player.getUniqueId(), List.of());
                for (PotionEffect potionEffect : potionEffects) {
                    player.addPotionEffect(potionEffect);
                }
            }, 3L);
        }
    }
}
