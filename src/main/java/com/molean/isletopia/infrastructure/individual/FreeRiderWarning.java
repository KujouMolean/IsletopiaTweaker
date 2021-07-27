package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import redis.clients.jedis.Jedis;

public class FreeRiderWarning implements Listener {

    public FreeRiderWarning() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(PlayerAttemptPickupItemEvent event) {
        if (PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            return;
        }

        int amount = event.getItem().getItemStack().getAmount();

        int total = amount;

        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("ItemPickUp-" + event.getPlayer().getName())) {
                total += Integer.parseInt(jedis.get("ItemPickUp-" + event.getPlayer().getName())) + amount;
            }
            jedis.setex("ItemPickUp-" + event.getPlayer().getName(), 60L, total + "");
        }

        if (total > 10000) {
            try (Jedis jedis = RedisUtils.getJedis()) {
                if (!jedis.exists("ItemPickUp-Notify-CoolDown-" + event.getPlayer().getName())) {
                    jedis.setex("ItemPickUp-Notify-CoolDown-" + event.getPlayer().getName(), 60 * 60L, "true");
                    Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
                        int i = Integer.parseInt(jedis.get("ItemPickUp-" + event.getPlayer().getName()));
                        CommonResponseObject commonResponseObject = new CommonResponseObject();
                        commonResponseObject.setMessage("通报批评, 玩家 " + event.getPlayer().getName() + " 连续白嫖了 " + i + " 个物品.");
                        ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                        commonResponseObject.setMessage("最强的琪露诺提醒大家, 快乐游戏, 自力更生, 拒绝白嫖, 从我做起!");
                        ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                    }, 30 * 20L);
                }
            }
        }
    }
}
