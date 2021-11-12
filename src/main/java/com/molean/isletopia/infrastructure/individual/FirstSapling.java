package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FirstSapling implements Listener {

    private static final Map<UUID, Boolean> caches = new ConcurrentHashMap<>();

    public FirstSapling() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(BlockBreakEvent event) {

        if (!event.getBlock().getType().equals(Material.OAK_LEAVES)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (!caches.containsKey(event.getPlayer().getUniqueId())) {
                String firstSapling = UniversalParameter.getParameter(event.getPlayer().getUniqueId(), "FirstSapling");
                caches.put(event.getPlayer().getUniqueId(), firstSapling != null && !firstSapling.isEmpty());

            }
            if (!caches.get(event.getPlayer().getUniqueId())) {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.OAK_SAPLING));
                });

                UniversalParameter.setParameter(event.getPlayer().getUniqueId(), "FirstSapling", "true");
                caches.put(event.getPlayer().getUniqueId(), true);
            }

        });

    }

}
