package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.NMSTagUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ElytraLimiter implements Listener {
    private static final List<UUID> denied = new ArrayList<>();

    public ElytraLimiter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            denied.removeIf(ElytraLimiter::check);
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
            MessageUtils.fail(player, "§c抱歉, 使用鞘翅需要经过管理员审核!");
            return;
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || !chestplate.getType().equals(Material.ELYTRA)) {
            return;
        }
        String bind = NMSTagUtils.get(chestplate, "bind");
        if (bind == null || bind.length() < 20) {
            ItemStack newChestplate = NMSTagUtils.set(chestplate, "bind", player.getUniqueId().toString());
            player.getInventory().setChestplate(newChestplate);
            return;
        }
        if (!bind.equalsIgnoreCase(player.getUniqueId().toString())) {
            event.setCancelled(true);
            MessageUtils.fail(player, "§c这是" + UUIDUtils.get(bind) + "的鞘翅，你不能使用!");
            return;
        }


    }
}
