package com.molean.isletopia.message.handler;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.core.ServerMessage;
import com.molean.isletopia.message.core.ServerMessageListener;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.TeleportRequest;
import com.molean.isletopia.message.obj.VisitRequest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    }


    @Override
    public void handleMessage(ServerMessage serverMessage) {
        serverMessage.setStatus("done");

        Gson gson = new Gson();
        TeleportRequest teleportRequest = gson.fromJson(serverMessage.getMessage(),TeleportRequest.class);

    }
}
