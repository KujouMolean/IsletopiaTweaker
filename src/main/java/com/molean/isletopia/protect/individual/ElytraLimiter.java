package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ElytraLimiter implements Listener {
    private static final List<String> denied = new ArrayList<>();

    public ElytraLimiter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            List<String> parameterAsList = UniversalParameter.getParameterAsList("Molean", "elytra");
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
    public void onMove(PlayerMoveEvent event) {
        ItemStack chestplate = event.getPlayer().getInventory().getChestplate();
        if (chestplate == null) {
            return;
        }
        if (!chestplate.getType().equals(Material.ELYTRA)) {
            return;
        }
        if (!denied.contains(event.getPlayer().getName())) {
            return;
        }
        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), chestplate);
        event.getPlayer().getInventory().setChestplate(null);
        event.getPlayer().sendMessage("§c抱歉, 使用该物品需要经过管理员审核!");
    }
}
