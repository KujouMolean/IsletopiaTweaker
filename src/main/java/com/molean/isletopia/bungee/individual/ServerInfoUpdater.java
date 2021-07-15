package com.molean.isletopia.bungee.individual;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.BukkitMessageListener;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.bungee.PlayerInfoObject;
import com.molean.isletopia.shared.utils.BukkitBungeeUtils;
import com.plotsquared.core.PlotSquared;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.bukkit.Bukkit.getScheduler;

public class ServerInfoUpdater implements MessageHandler<PlayerInfoObject> {


    private static final String serverName = new File(System.getProperty("user.dir")).getName();

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

    private static final Map<String, String> playerServerMap = new HashMap<>();

    public static Map<String, List<String>> getPlayersPerServer() {
        return new HashMap<>(playersPerServer);
    }

    public static Map<String, String> getPlayerServerMap() {
        return playerServerMap;
    }

    public ServerInfoUpdater() {
        BukkitMessageListener.setHandler("PlayerInfo", this, PlayerInfoObject.class);
    }

    public static UUID getUUID(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void handle(PlayerInfoObject message) {
        List<String> players = message.getPlayers();
        Map<String, List<String>> playersPerServer = message.getPlayersPerServer();

        onlinePlayers.clear();
        onlinePlayers.addAll(players);

        for (String server : playersPerServer.keySet()) {
            List<String> serverPlayers = playersPerServer.get(server);
            playersPerServer.put(server, new ArrayList<>(serverPlayers));
            for (String player : serverPlayers) {
                playerServerMap.put(player, server);
            }
        }

        servers.clear();
        servers.addAll(new ArrayList<>(playersPerServer.keySet()));

    }
}
