//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.google.gson.Gson;
import com.molean.isletopia.message.core.ServerMessage;
import com.molean.isletopia.message.core.ServerMessageListener;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.TeleportResponse;
import com.molean.isletopia.utils.BungeeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportResponseHandler implements ServerMessageListener {
    public TeleportResponseHandler() {
        ServerMessageManager.registerHandler("TeleportResponse", this);
    }

    public void handleMessage(ServerMessage serverMessage) {
        serverMessage.setStatus("done");
        Gson gson = new Gson();
        TeleportResponse teleportResponse = (TeleportResponse)gson.fromJson(serverMessage.getMessage(), TeleportResponse.class);
        String target = teleportResponse.getTarget();
        Player player = Bukkit.getPlayer(target);
        if (player != null) {
            if (teleportResponse.getResponse().equals("accepted")) {
                BungeeUtils.switchServer(player, serverMessage.getSource());
            } else {
                player.sendMessage(teleportResponse.getResponseMessage());
            }

        }
    }
}
