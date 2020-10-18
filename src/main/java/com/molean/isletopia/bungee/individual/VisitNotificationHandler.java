package com.molean.isletopia.bungee.individual;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.I18n;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class VisitNotificationHandler implements PluginMessageListener {
    public VisitNotificationHandler() {
        Bukkit.getMessenger().registerIncomingPluginChannel(IsletopiaTweakers.getPlugin(), "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equals("visitor")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String visitor = msgin.readUTF();
                player.sendMessage(I18n.getMessage("notification.visitor", player).replace("%1%", visitor));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else if (subChannel.equals("failedVisitor")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String visitor = msgin.readUTF();
                player.sendMessage(I18n.getMessage("notification.failedVisitor", player).replace("%1%", visitor));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
