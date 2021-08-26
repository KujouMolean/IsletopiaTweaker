package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import redis.clients.jedis.Jedis;

public class FreeRiderWarning implements Listener {

    public FreeRiderWarning() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(InventoryPickupItemEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.PLAYER)) {
            return;
        }
        PlayerInventory playerInventory = (PlayerInventory) event.getInventory();
        HumanEntity holder = playerInventory.getHolder();
        if (holder == null || !holder.getType().equals(EntityType.PLAYER)) {
            return;
        }
        Player player = (Player) holder;
        if (PlotUtils.hasCurrentPlotPermission(player)) {
            return;
        }


        int amount = event.getItem().getItemStack().getAmount();

        int total = amount;

        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("ItemPickUp-" + player.getName())) {
                total += Integer.parseInt(jedis.get("ItemPickUp-" + player.getName())) + amount;
            }
            jedis.setex("ItemPickUp-" + player.getName(), 60L, total + "");
        }

        if (total > 500) {
            try (Jedis jedis = RedisUtils.getJedis()) {
                if (!jedis.exists("ItemPickUp-Notify-CoolDown-" + player.getName())) {
                    jedis.setex("ItemPickUp-Notify-CoolDown-" + player.getName(), 60 * 60L, "true");
                    Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
                        int i = Integer.parseInt(jedis.get("ItemPickUp-" + player.getName()));
                        CommonResponseObject commonResponseObject = new CommonResponseObject();
                        commonResponseObject.setMessage("真·通报批评, 玩家 " + player.getName() + " 真的连续白嫖了 " + i + " 个物品.");
                        ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                        commonResponseObject.setMessage("最强的琪露诺提醒大家, 快乐游戏, 自力更生, 拒绝白嫖, 从我做起!");
                        ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                    }, 30 * 20L);
                }
            }
        }
    }
}
