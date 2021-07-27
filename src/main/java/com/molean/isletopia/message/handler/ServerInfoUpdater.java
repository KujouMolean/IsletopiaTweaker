package com.molean.isletopia.message.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.obj.PlayerInfoObject;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        RedisMessageListener.setHandler("PlayerInfo", this, PlayerInfoObject.class);
    }

    public static UUID getUUID(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject,PlayerInfoObject message) {
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
