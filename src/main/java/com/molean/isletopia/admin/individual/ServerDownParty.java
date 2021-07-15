package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServerDownParty implements Listener {
    private final Map<Player, Integer> fireworkCountMap = new HashMap<>();

    public ServerDownParty() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

    }

    @EventHandler
    @SuppressWarnings("all")
    public void on(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.getDisplayName().contains("崩服牛排")) {
            fireworkCountMap.put(event.getPlayer(), 120);
            Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), (task) -> {
                Integer integer = fireworkCountMap.get(event.getPlayer());
                if (integer == null || !event.getPlayer().isOnline()) {
                    task.cancel();
                    return;
                }
                if (integer <= 0) {
                    task.cancel();
                    return;
                }
                fireworkCountMap.put(event.getPlayer(), integer - 1);
                spawnFirework(event.getPlayer().getLocation());
            }, 10, 10);
        }
    }
    private void spawnFirework(Location location) {
        Firework entity = (Firework) location.getWorld().spawnEntity(location.clone().add(0, 2, 0), EntityType.FIREWORK);
        FireworkMeta itemMeta = entity.getFireworkMeta();
        Random random = new Random();
        itemMeta.setPower(random.nextInt(3) + 1);
        for (int i = 0; i < random.nextInt(10) + 1; i++) {
            FireworkEffect build = FireworkEffect.builder()
                    .flicker(false)
                    .trail(false)
                    .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
                    .withColor(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                    .withFade(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                    .build();

            itemMeta.addEffect(build);
        }
        entity.detonate();
        entity.setBounce(true);
        entity.setFireworkMeta(itemMeta);
    }

}
