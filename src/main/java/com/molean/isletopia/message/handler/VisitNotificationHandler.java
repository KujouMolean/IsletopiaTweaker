package com.molean.isletopia.message.handler;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.obj.VisitNotificationObject;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;


public class VisitNotificationHandler implements MessageHandler<VisitNotificationObject>, Listener {
    public VisitNotificationHandler() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        List<String> visits = UniversalParameter.getParameterAsList(event.getPlayer().getName(), "visits");
        if (visits.size() > 0) {
            event.getPlayer().sendMessage("§8[§3访客提醒§8] §e离线时的访客有:");
            event.getPlayer().sendMessage("§7  " + String.join(",", visits));
            UniversalParameter.setParameter(event.getPlayer().getName(), "visits", null);
        }
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject,VisitNotificationObject message) {
        String visitor = message.getVisitor();
        String target = message.getTarget();
        boolean success = message.isSuccess();
        Player player = Bukkit.getPlayer(target);
        if (player == null) {
            Bukkit.getLogger().warning("访客提醒失败: " + visitor + "=>" + target);
            return;
        }
        if (success) {
            player.sendMessage("§8[§3访客提醒§8] §e %1% 刚刚访问了阁下的岛屿.".replace("%1%", visitor));
        } else {
            player.sendMessage("§8[§3访客提醒§8] §e %1% 请求访问阁下岛屿但被拒绝了.".replace("%1%", visitor));
        }
    }
}
