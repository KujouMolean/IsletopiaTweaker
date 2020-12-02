package com.molean.isletopia.bungee.individual;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

// Handler player tell message


public class PlayerMessageHandler implements PluginMessageListener {
    public PlayerMessageHandler() {
        Bukkit.getMessenger().registerIncomingPluginChannel(IsletopiaTweakers.getPlugin(), "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        @SuppressWarnings("all")ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("tell")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String tellMessage = msgin.readUTF();
                player.sendMessage(tellMessage);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

}
