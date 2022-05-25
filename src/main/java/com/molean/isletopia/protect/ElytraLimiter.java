package com.molean.isletopia.protect;

import com.google.common.collect.Sets;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
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

@Singleton
public class ElytraLimiter implements Listener {
    @AutoInject
    private PlayerPropertyManager playerPropertyManager;

    @EventHandler(ignoreCancelled = true)
    public void onMove(EntityToggleGlideEvent event) {

        if (!event.isGliding()) {
            return;
        }
        Entity entity = event.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        if (!playerPropertyManager.getPropertyAsBoolean(player, "elytra")) {
            event.setCancelled(true);
            MessageUtils.fail(player, "elytra.noPerm");
        }
    }
}
