package com.molean.isletopia.message.handler;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.core.ServerMessage;
import com.molean.isletopia.message.core.ServerMessageListener;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.TeleportRequest;
import com.molean.isletopia.message.obj.TeleportResponse;
import com.molean.isletopia.message.obj.VisitRequest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class TeleportRequestHandler implements ServerMessageListener, Listener {

    private final Map<String, Location> locationMap = new HashMap<>();
    private final Map<String, Long> expire = new HashMap<>();

    public TeleportRequestHandler() {
        ServerMessageManager.registerHandler("TeleportRequest", this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }


    @EventHandler
    public void on(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        Location location = this.locationMap.get(name);
        if (location != null) {
            if (System.currentTimeMillis() - (Long)this.expire.getOrDefault(name, 0L) < 10000L) {
                event.getPlayer().teleport(location);
            }
            this.locationMap.remove(name);
        }
    }

    @Override
    public void handleMessage(ServerMessage serverMessage) {
        serverMessage.setStatus("done");
        Gson gson = new Gson();
        TeleportRequest teleportRequest = gson.fromJson(serverMessage.getMessage(), TeleportRequest.class);
        TeleportResponse teleportResponse = new TeleportResponse();
        String sourcePlayer = teleportRequest.getSourcePlayer();
        String targetPlayer = teleportRequest.getTargetPlayer();
        Player source = Bukkit.getPlayer(sourcePlayer);
        Player target = Bukkit.getPlayer(targetPlayer);
        teleportResponse.setTarget(sourcePlayer);
        if (target == null) {
            teleportResponse.setResponse("no-player");
            teleportResponse.setResponseMessage("玩家不存在");
            ServerMessageManager.sendMessage(serverMessage.getSource(), "TeleportResponse", teleportResponse);
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
            ServerMessageManager.sendMessage(serverMessage.getSource(), "TeleportResponse", teleportResponse);
        }

    }
}
