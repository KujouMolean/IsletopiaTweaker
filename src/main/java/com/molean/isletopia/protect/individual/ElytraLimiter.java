package com.molean.isletopia.protect.individual;

import com.google.common.collect.Sets;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ElytraLimiter implements Listener {
    private static final Set<UUID> denied = Collections.synchronizedSet(Sets.newHashSet());

    public ElytraLimiter() {
        PluginUtils.registerEvents(this);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            denied.removeIf(ElytraLimiter::check);
        }, new Random().nextInt(100), 20 * 30);
        Tasks.INSTANCE.addDisableTask("Disable beacon permission update..", bukkitTask::cancel);

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!check(onlinePlayer.getUniqueId()) && onlinePlayer.isOnline()) {
                    denied.add(onlinePlayer.getUniqueId());
                }
            }
        });
    }

    public static boolean check(UUID uuid) {
        String beacon = UniversalParameter.getParameter(uuid, "elytra");
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
    public void onMove(EntityToggleGlideEvent event) {

        if (!event.isGliding()) {
            return;
        }
        Entity entity = event.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        if (denied.contains(player.getUniqueId())) {
            event.setCancelled(true);
            MessageUtils.fail(player, "elytra.noPerm");
        }
    }
}
