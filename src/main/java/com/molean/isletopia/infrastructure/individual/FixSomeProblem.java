package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Locale;

public class FixSomeProblem implements Listener {

    public FixSomeProblem() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

            int joinTime = 0;

            if (RedisUtils.getCommand().exists("JoinTime-" + event.getPlayer().getName())>0) {
                joinTime = Integer.parseInt(RedisUtils.getCommand().get("JoinTime-" + event.getPlayer().getName()));
                if (joinTime > 10) {
                    CommonResponseObject commonResponseObject = new CommonResponseObject();
                    commonResponseObject.setMessage("玩家 " + event.getPlayer().getName() + " 崩掉了 " + IsletopiaTweakersUtils.getLocalServerName() + ", 大家恭喜!");
                    ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.kick(Component.text("检测到服务器异常, 请重新进入服务器.").color(TextColor.color(255, 0, 0)));
                    }
                    return;
                }
            }
        RedisUtils.getCommand().setex("JoinTime-" + event.getPlayer().getName(), 5L, "" + (joinTime + 1));
    }


    @EventHandler
    public void on(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if (message.toLowerCase(Locale.ROOT).startsWith("/stop")) {
            RedisUtils.getCommand().setex("Restarting-" + ServerInfoUpdater.getServerName(), 15L, "true");

        }
    }

}
