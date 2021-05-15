package com.molean.isletopia.message.handler;

import com.google.gson.Gson;
import com.molean.isletopia.message.core.ServerMessage;
import com.molean.isletopia.message.core.ServerMessageListener;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.PlaySoundRequest;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySoundRequestHandler implements ServerMessageListener {
    public PlaySoundRequestHandler() {
        ServerMessageManager.registerHandler("PlaySoundRequest", this);
    }

    @Override
    public void handleMessage(ServerMessage serverMessage) {
        Gson gson = new Gson();
        PlaySoundRequest playSoundRequest = gson.fromJson(serverMessage.getMessage(), PlaySoundRequest.class);
        String targetPlayer = playSoundRequest.getTargetPlayer();
        Player player = Bukkit.getPlayer(targetPlayer);
        if(player==null){
            serverMessage.setStatus("invalid");
            return;
        }
        Sound sound = null;
        try {
            sound = Sound.valueOf(playSoundRequest.getSoundName());
        } catch (IllegalArgumentException ignored) {

        }
        if(sound==null){
            serverMessage.setStatus("invalid");
            return;
        }
        player.playSound(player.getLocation(),sound,1.0f,1.0f);
        serverMessage.setStatus("done");
    }
}
