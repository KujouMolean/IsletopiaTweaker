package com.molean.isletopia.protect.individual;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.NMSTagUtils;
import com.molean.isletopia.shared.utils.UUIDUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeaconLimiter implements Listener {
    private static final Set<UUID> denied = new HashSet<>();
    private static final Map<String, Long> notifyTime = new ConcurrentHashMap<>();

    public BeaconLimiter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            denied.removeIf(BeaconLimiter::check);
        }, new Random().nextInt(100), 20 * 30);
        IsletopiaTweakers.addDisableTask("Disable beacon permission update..", bukkitTask::cancel);

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!check(onlinePlayer.getUniqueId()) && onlinePlayer.isOnline()) {
                    denied.add(onlinePlayer.getUniqueId());
                }
            }
        });
    }

    public static boolean check(UUID uuid) {
        String beacon = UniversalParameter.getParameter(uuid, "beacon");
        return "true".equalsIgnoreCase(beacon);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (!check(event.getPlayer().getUniqueId()) && event.getPlayer().isOnline()) {
                denied.add(event.getPlayer().getUniqueId());
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeft(PlayerQuitEvent event) {
        denied.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BeaconEffectEvent event) {
        if (!denied.contains(event.getPlayer().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        Long lastTime = notifyTime.getOrDefault(event.getPlayer().getName(), 0L);
        if (System.currentTimeMillis() - lastTime > 120 * 1000) {
            notifyTime.put(event.getPlayer().getName(), System.currentTimeMillis());
            event.getPlayer().sendMessage("§c抱歉, 你没有权限获得信标效果!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        if (!event.getBlockPlaced().getType().equals(Material.BEACON)) {
            return;
        }
        ItemStack itemInHand = event.getItemInHand();

        String bind = NMSTagUtils.get(itemInHand, "bind");
        if (bind == null || bind.isEmpty()) {
            return;
        }

        Location location = event.getBlockPlaced().getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            event.setBuild(false);
            return;
        }

        if (!Objects.equals(UUIDUtils.get(currentIsland.getUuid()), bind)) {
            event.setBuild(false);
            event.getPlayer().sendMessage("§c这个信标只能放在 " + bind + " 岛屿");
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void on(ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();
        if (itemStack.getType().equals(Material.BEACON)) {
            String bind = NMSTagUtils.get(itemStack, "bind");
            if (bind != null && !bind.isEmpty()) {
                return;
            }
            Location location = event.getLocation();
            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
            if (currentIsland == null) {
                return;
            }
            String owner = UUIDUtils.get(currentIsland.getUuid());
            itemStack.lore(List.of(Component.text("§c绑定=>" + owner)));
            item.setItemStack(NMSTagUtils.set(itemStack, "bind", owner));

        }


    }
}
