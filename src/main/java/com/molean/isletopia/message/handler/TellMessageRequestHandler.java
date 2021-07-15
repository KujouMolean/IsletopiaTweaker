package com.molean.isletopia.message.handler;

import com.google.gson.Gson;
import com.molean.isletopia.message.core.ServerMessage;
import com.molean.isletopia.message.core.ServerMessageListener;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.TellMessageRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TellMessageRequestHandler implements ServerMessageListener {
    public TellMessageRequestHandler() {
        ServerMessageManager.registerHandler("TellMessage", this);
    }

    public void handleMessage(ServerMessage serverMessage) {
        serverMessage.setStatus("done");
        Gson gson = new Gson();
        TellMessageRequest tellMessageRequest = gson.fromJson(serverMessage.getMessage(), TellMessageRequest.class);
        String target = tellMessageRequest.getTarget();
        Player player = Bukkit.getPlayer(target);
        if (player != null) {
            String message = "§7" + tellMessageRequest.getSource() + " -> "
                    + tellMessageRequest.getTarget() + ": "
                    + tellMessageRequest.getMessage();
            player.sendMessage(Component.text(message));
        }
    }
}
