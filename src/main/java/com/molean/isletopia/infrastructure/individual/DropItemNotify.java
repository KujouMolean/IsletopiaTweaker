package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DropItemNotify implements Listener {

    public DropItemNotify() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    private static class DropData {
        public Long lastTime;
        public int amount;

        public DropData(Long lastTime, int amount) {
            this.lastTime = lastTime;
            this.amount = amount;
        }
    }

    private final Map<Player, DropData> dropDataMap = new HashMap<>();

    @EventHandler

    public void on(PlayerDropItemEvent event) {
        if (PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            return;
        }
        ItemStack itemStack = event.getItemDrop().getItemStack();
        DropData dropData = dropDataMap.getOrDefault(event.getPlayer(), new DropData(0L, 0));
        if (System.currentTimeMillis() - dropData.lastTime > 30000) {
            dropData.lastTime = System.currentTimeMillis();
            dropData.amount = itemStack.getAmount();
        } else {
            dropData.lastTime = System.currentTimeMillis();
            dropData.amount += itemStack.getAmount();
        }


        if (dropData.amount > 128) {
            event.getPlayer().sendMessage("§8[§3温馨提示§8] §e请不要过度帮助新人, 否则会破坏游戏体验.");
            dropData.lastTime = 0L;
            dropData.amount = 0;
        }
        dropDataMap.put(event.getPlayer(), dropData);
    }
}
