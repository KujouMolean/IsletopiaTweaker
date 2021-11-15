package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetherPortal implements Listener {
    public NetherPortal() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    private static final Map<UUID, Long> map = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onMob(EntityPortalEnterEvent event) {
        if (event.getEntity() instanceof Mob) {
            if (!map.containsKey(event.getEntity().getUniqueId())) {
                map.put(event.getEntity().getUniqueId(), System.currentTimeMillis());
            } else if (System.currentTimeMillis() - map.get(event.getEntity().getUniqueId()) > 4000) {
                event.getEntity().remove();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayer(EntityPortalEnterEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!map.containsKey(event.getEntity().getUniqueId())) {
                map.put(event.getEntity().getUniqueId(), System.currentTimeMillis());
            } else if (System.currentTimeMillis() - map.get(event.getEntity().getUniqueId()) > 4000) {
                player.setHealth(0);
                MessageUtils.notify(player, "下界已被关闭, 如需地狱生物, 请切换到地狱生物群系.");
                map.remove(event.getEntity().getUniqueId());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityPortalExitEvent event) {
        map.remove(event.getEntity().getUniqueId());
    }
}
