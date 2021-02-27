package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import net.craftersland.data.bridge.events.PlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class OfflineVisitorNotify implements Listener {
    public OfflineVisitorNotify() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            List<String> visits = UniversalParameter.getParameterAsList(event.getPlayer().getName(), "visits");
            if (visits.size() > 0) {
                player.sendMessage(MessageUtils.getMessage("island.notify.offlineVisitors"));
                player.sendMessage("§7  " + String.join(",", visits));
                UniversalParameter.setParameter(event.getPlayer().getName(), "visits", null);
            }
        });

    }
}
