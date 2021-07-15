//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.google.gson.Gson;
import com.molean.isletopia.bungee.individual.ServerInfoUpdater;
import com.molean.isletopia.message.core.ServerMessage;
import com.molean.isletopia.message.core.ServerMessageListener;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.VisitResponse;
import com.molean.isletopia.shared.utils.BukkitBungeeUtils;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VisitResponseHandler implements ServerMessageListener {
    public VisitResponseHandler() {
        ServerMessageManager.registerHandler("VisitResponse", this);
    }

    public void handleMessage(ServerMessage serverMessage) {
        serverMessage.setStatus("done");
        String message = serverMessage.getMessage();
        VisitResponse visitResponse = (new Gson()).fromJson(message, VisitResponse.class);
        Player player = Bukkit.getPlayer(visitResponse.getTarget());
        if (player != null) {
            if (!Objects.equals("accepted", visitResponse.getResponse())) {
                player.sendMessage(visitResponse.getResponseMessage());
            } else {
                if (!ServerInfoUpdater.getServerName().equalsIgnoreCase(serverMessage.getSource())) {
                    BukkitBungeeUtils.switchServer(player, serverMessage.getSource());
                }

            }
        }
    }
}
