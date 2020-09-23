package com.molean.isletopia.distribute.individual;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.plotsquared.core.PlotSquared;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getScheduler;

public class ServerInfoUpdater implements PluginMessageListener {

    private static String serverName;

    public static String getServerName() {
        return serverName;
    }

    private static final List<String> onlinePlayers = new ArrayList<>();

    public static List<String> getOnlinePlayers() {
        return onlinePlayers;
    }

    private static final List<String> servers = new ArrayList<>();

    public static List<String> getServers() {
        return servers;
    }

    public ServerInfoUpdater() {
        Bukkit.getMessenger().registerIncomingPluginChannel(IsletopiaTweakers.getPlugin(), "BungeeCord", this);
        getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), this::updates, 20, 20);
    }

    public void updates() {
        updateOnlinePlayers();
        updateServerName();
        updateServers();
    }

    public void updateOnlinePlayers() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");
        out.writeUTF("PlayerList");
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public void updateServerName() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        out.writeUTF("GetServer");
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public void updateServers() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        out.writeUTF("GetServers");
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static UUID getUUID(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8));
    }


    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("PlayerList")) {
            String server = in.readUTF();
            String[] playerList = in.readUTF().split(", ");
            onlinePlayers.clear();
            onlinePlayers.addAll(Arrays.asList(playerList));
        } else if (subChannel.equalsIgnoreCase("GetServer")) {
            serverName = in.readUTF();
        } else if (subChannel.equalsIgnoreCase("GetServers")) {
            String[] serverList = in.readUTF().split(", ");
            servers.clear();
            servers.addAll(Arrays.asList(serverList));
        } else if (subChannel.equalsIgnoreCase("updateUUID")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String name = msgin.readUTF();
                String uuid = msgin.readUTF();
                PlotSquared.get().getImpromptuUUIDPipeline().storeImmediately(name, UUID.fromString(uuid));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
