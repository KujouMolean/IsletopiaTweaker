package com.molean.isletopia.bungee.individual;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.infrastructure.individual.I18n;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class VisitNotificationHandler implements PluginMessageListener, Listener {
    public VisitNotificationHandler() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Bukkit.getMessenger().registerIncomingPluginChannel(IsletopiaTweakers.getPlugin(), "BungeeCord", this);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        List<String> visits = UniversalParameter.getParameterAsList(event.getPlayer().getName(), "visits");
        if (visits.size() > 0) {
            event.getPlayer().sendMessage(I18n.getMessage("island.notify.offlineVisitors", event.getPlayer()));
            event.getPlayer().sendMessage("§7  " + String.join(",", visits));
            UniversalParameter.setParameter(event.getPlayer().getName(), "visits", null);
        }
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
