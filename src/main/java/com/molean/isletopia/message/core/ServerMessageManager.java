package com.molean.isletopia.message.core;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.ServerMessageDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerMessageManager {

    private static final Map<String, Set<ServerMessageListener>> listeners = new HashMap<>();


    public static void init() {
        ServerMessageDao.checkTable();
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Set<ServerMessage> serverMessages = ServerMessageDao.fetchMessage();
            for (ServerMessage serverMessage : serverMessages) {
                String channel = serverMessage.getChannel();
                Set<ServerMessageListener> serverMessageListeners = listeners.get(channel);
                if (serverMessageListeners == null) {
                    ServerMessageDao.updateStatus(serverMessage.getId(), "invalid");
                    continue;
                }
                outer:
                for (ServerMessageListener serverMessageListener : serverMessageListeners) {
                    serverMessageListener.handleMessage(serverMessage);
                    ServerMessageDao.updateStatus(serverMessage.getId(), serverMessage.getStatus());
                    switch (serverMessage.getStatus()) {
                        case "done":
                        case "invalid":
                        case "expire":
                            break outer;
                        default:
                    }
                }
                if (System.currentTimeMillis() - serverMessage.getTime() > 10000) {
                    ServerMessageDao.updateStatus(serverMessage.getId(), "expire");
                }

            }
        }, 5, 5);
    }

    public static void sendMessage(String targetServer, String channel, Object message) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setSource(ServerInfoUpdater.getServerName());
        serverMessage.setTarget(targetServer);
        serverMessage.setChannel(channel);
        serverMessage.setStatus("todo");
        serverMessage.setTime(System.currentTimeMillis());
        serverMessage.setMessage(new Gson().toJson(message));
        ServerMessageDao.addMessage(serverMessage);
    }

    public static void registerHandler(String channel, ServerMessageListener serverMessageListener) {
        listeners.put(channel, listeners.getOrDefault(channel, new HashSet<>()));
        listeners.get(channel).add(serverMessageListener);
    }
}
