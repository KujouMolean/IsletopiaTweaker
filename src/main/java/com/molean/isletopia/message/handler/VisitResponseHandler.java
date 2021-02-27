package com.molean.isletopia.message.handler;

import com.google.gson.Gson;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.message.core.ServerMessage;
import com.molean.isletopia.message.core.ServerMessageListener;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.VisitResponse;
import com.molean.isletopia.utils.BungeeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class VisitResponseHandler implements ServerMessageListener {
    public VisitResponseHandler() {
        ServerMessageManager.registerHandler("VisitResponse", this);
    }

    @Override
    public void handleMessage(ServerMessage serverMessage) {

        serverMessage.setStatus("done");

        String message = serverMessage.getMessage();
        VisitResponse visitResponse = new Gson().fromJson(message, VisitResponse.class);
        Player player = Bukkit.getPlayer(visitResponse.getTarget());
        if (player == null) {
            return;
        }
        if (!Objects.equals("accepted", visitResponse.getResponse())) {
            player.sendMessage(visitResponse.getResponseMessage());
            return;
        }
        if (!ServerInfoUpdater.getServerName().equalsIgnoreCase(serverMessage.getSource())) {
            BungeeUtils.switchServer(player, serverMessage.getSource());
        }
    }
}
