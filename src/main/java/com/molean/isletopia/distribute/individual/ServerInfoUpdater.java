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
import java.util.*;

import static org.bukkit.Bukkit.getScheduler;

public class ServerInfoUpdater implements PluginMessageListener {

    private static String serverName;

    public static String getServerName() {
        return serverName;
    }

    private static final List<String> onlinePlayers = new ArrayList<>();

    public static List<String> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers);
    }

    private static final List<String> servers = new ArrayList<>();

    public static List<String> getServers() {
        return new ArrayList<>(servers);
    }

    private static final Map<String, List<String>> playersPerServer = new HashMap<>();

    public static Map<String, List<String>> getPlayersPerServer() {
        return new HashMap<>(playersPerServer);
    }

    public ServerInfoUpdater() {
        Bukkit.getMessenger().registerIncomingPluginChannel(IsletopiaTweakers.getPlugin(), "BungeeCord", this);
        getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), ServerInfoUpdater::updates, 20, 20);
    }

    public static void updates() {
        updateOnlinePlayers();
        updateServerName();
        updateServers();
    }

    public static void updateOnlinePlayersPerServer() {
        for (String server : servers) {
            @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerList");
            out.writeUTF(server);
            Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            if (player != null)
                player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
        }
    }

    public static void updateOnlinePlayers() {
        @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static void updateServerName() {
        @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static void updateServers() {
        @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
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
        @SuppressWarnings("all") ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("PlayerList")) {
            String server = in.readUTF();
            String[] playerList = in.readUTF().split(", ");
            if (server.equalsIgnoreCase("all")) {
                onlinePlayers.clear();
                onlinePlayers.addAll(Arrays.asList(playerList));
            } else {
                playersPerServer.put(server, Arrays.asList(playerList));
            }
        } else if (subChannel.equalsIgnoreCase("GetServer")) {
            serverName = in.readUTF();
        } else if (subChannel.equalsIgnoreCase("GetServers")) {
            String[] serverList = in.readUTF().split(", ");
            servers.clear();
            servers.addAll(Arrays.asList(serverList));
            updateOnlinePlayersPerServer();
        } else if (subChannel.equalsIgnoreCase("updateUUID")) {
            try {
                short len = in.readShort();
                byte[] msgBytes = new byte[len];
                in.readFully(msgBytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgBytes));
                String name = msgin.readUTF();
                String uuid = msgin.readUTF();
                PlotSquared.get().getImpromptuUUIDPipeline().storeImmediately(name, UUID.fromString(uuid));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
