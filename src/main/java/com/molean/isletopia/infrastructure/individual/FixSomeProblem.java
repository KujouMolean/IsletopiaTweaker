package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.utils.RedisUtils;
import net.craftersland.data.bridge.PD;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import redis.clients.jedis.Jedis;

public class FixSomeProblem implements Listener {

    public FixSomeProblem() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {


        try (Jedis jedis = RedisUtils.getJedis()) {

            int joinTime =0;

            if (jedis.exists("JoinTime-" + event.getPlayer().getName())) {
                joinTime = Integer.parseInt(jedis.get("JoinTime-" + event.getPlayer().getName()));
                if (joinTime > 5) {
                    CommonResponseObject commonResponseObject = new CommonResponseObject();
                    commonResponseObject.setMessage("玩家 " + event.getPlayer().getName() + " 崩掉了 " + MessageUtils.getLocalServerName() + ", 大家恭喜!");
                    ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.kick(Component.text("检测到服务器异常, 请重新进入服务器.").color(TextColor.color(255,0,0)));
                    }
                    return;
                }
            }
            jedis.setex("JoinTime-" + event.getPlayer().getName(), 5L, "" + (joinTime + 1));

        }

        if (PD.isDisabling) {
            event.getPlayer().kick(Component.text("服务器正在重启中.."));
        }
    }

}
