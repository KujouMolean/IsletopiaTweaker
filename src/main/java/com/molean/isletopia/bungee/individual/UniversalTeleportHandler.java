package com.molean.isletopia.bungee.individual;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
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
import java.util.HashMap;
import java.util.Map;

public class UniversalTeleportHandler implements PluginMessageListener, Listener {

    public UniversalTeleportHandler() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Bukkit.getMessenger().registerIncomingPluginChannel(IsletopiaTweakers.getPlugin(), "BungeeCord", this);
    }

    private static final Map<String, String> teleports = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            // Check if player is going to visit other.
            Player sourcePlayer = event.getPlayer();
            if (teleports.containsKey(sourcePlayer.getName())) {
                String target = teleports.get(sourcePlayer.getName());
                Player targetPlayer = Bukkit.getPlayer(target);
                if (targetPlayer == null) {
                    sourcePlayer.sendMessage("§c玩家不在线");
                } else {
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        sourcePlayer.teleport(targetPlayer);
                    });
                }
            }
        });
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        @SuppressWarnings("all") ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("tp")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

                String source = msgin.readUTF();
                String target = msgin.readUTF();
                Player sourcePlayer = Bukkit.getPlayer(source);
                Player targetPlayer = Bukkit.getPlayer(target);

                //if the player is not online, save to map and wait her join.
                if (sourcePlayer != null && targetPlayer != null) {
                    sourcePlayer.teleport(targetPlayer);
                } else {
                    teleports.put(source, target);
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
