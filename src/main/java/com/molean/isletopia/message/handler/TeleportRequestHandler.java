package com.molean.isletopia.message.handler;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.TeleportRequest;
import com.molean.isletopia.shared.pojo.resp.TeleportResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class TeleportRequestHandler implements MessageHandler<TeleportRequest>, Listener {

    private final Map<String, Location> locationMap = new HashMap<>();
    private final Map<String, Long> expire = new HashMap<>();

    public TeleportRequestHandler() {
        RedisMessageListener.setHandler("TeleportRequest", this, TeleportRequest.class);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        Location location = this.locationMap.get(name);
        if (location != null) {
            if (System.currentTimeMillis() - this.expire.getOrDefault(name, 0L) < 10000L) {
                event.getPlayer().teleport(location);
            }
        }
    }

    @EventHandler
    public void on(PlayerDataSyncCompleteEvent event) {
        String name = event.getPlayer().getName();
        Location location = this.locationMap.get(name);
        if (location != null) {
            if (System.currentTimeMillis() - (Long) this.expire.getOrDefault(name, 0L) < 10000L) {
                event.getPlayer().teleport(location);
            }
            this.locationMap.remove(name);
        }
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, TeleportRequest teleportRequest) {
        TeleportResponse teleportResponse = new TeleportResponse();
        String sourcePlayer = teleportRequest.getSourcePlayer();
        String targetPlayer = teleportRequest.getTargetPlayer();
        Player source = Bukkit.getPlayerExact(sourcePlayer);
        Player target = Bukkit.getPlayerExact(targetPlayer);
        teleportResponse.setTarget(sourcePlayer);
        if (target == null) {
            teleportResponse.setResponse("no-player");
            teleportResponse.setResponseMessage("玩家不存在");
            String playerServerName = ServerInfoUpdater.getPlayerServerMap().get(sourcePlayer);
            ServerMessageUtils.sendMessage(playerServerName, "TeleportResponse", teleportResponse);

        } else {
            if (source != null && source.isOnline()) {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    source.teleport(target.getLocation());
                });
            } else {
                this.locationMap.put(sourcePlayer, target.getLocation());
                this.expire.put(sourcePlayer, System.currentTimeMillis());
            }
            teleportResponse.setResponse("accepted");
            teleportResponse.setResponseMessage("");
            String playerServerName = ServerInfoUpdater.getPlayerServerMap().get(sourcePlayer);
            ServerMessageUtils.sendMessage(playerServerName, "TeleportResponse", teleportResponse);
        }
    }
}
