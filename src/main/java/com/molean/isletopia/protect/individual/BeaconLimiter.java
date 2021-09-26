package com.molean.isletopia.protect.individual;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.NMSTagUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    public void onLeft(PlayerQuitEvent event) {
        denied.remove(event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true)
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
        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            event.setBuild(false);
            return;
        }

        if (!currentIsland.getOwner().equals(bind)) {
            event.setBuild(false);
            event.getPlayer().sendMessage("§c这个信标只能放在 " + bind + " 岛屿");
        }

    }

//    @EventHandler(ignoreCancelled = true)
//    public void on(BlockDropItemEvent event) {
//        Location location = event.getBlock().getLocation();
//        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
//        if (currentIsland == null) {
//            return;
//        }
//        String owner = currentIsland.getOwner();
//        for (Item item : event.getItems()) {
//            ItemStack itemStack = item.getItemStack();
//            if (!itemStack.getType().equals(Material.BEACON)) {
//                continue;
//            }
//            itemStack.lore(List.of(Component.text("§c绑定=>" + owner)));
//            item.setItemStack(NMSTagUtils.set(itemStack, "bind", owner));
//
//        }
//    }

    @EventHandler
    public void on(ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();
        if (itemStack.getType().equals(Material.BEACON)) {
            Location location = event.getLocation();
            Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
            if (currentIsland == null) {
                return;
            }
            String owner = currentIsland.getOwner();
            itemStack.lore(List.of(Component.text("§c绑定=>" + owner)));
            item.setItemStack(NMSTagUtils.set(itemStack, "bind", owner));

        }


    }
}
