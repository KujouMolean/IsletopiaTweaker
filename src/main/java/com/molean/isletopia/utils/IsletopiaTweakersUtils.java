package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.pojo.req.PlaySoundRequest;
import com.molean.isletopia.shared.pojo.req.TeleportRequest;
import com.molean.isletopia.shared.pojo.req.TellMessageRequest;
import com.molean.isletopia.shared.pojo.req.VisitRequest;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

public class IsletopiaTweakersUtils {
    public static String getLocalServerName(String server) {
        return switch (server) {
            case "server1" -> "①";
            case "server2" -> "②";
            case "server3" -> "③";
            case "server4" -> "④";
            case "server5" -> "⑤";
            case "server6" -> "⑥";
            case "server7" -> "⑦";
            case "server8" -> "⑧";
            case "server9" -> "⑨";
            case "server10" -> "⑩";
            case "server11" -> "⑪";
            case "server12" -> "⑫";
            default -> "未知";
        };
    }

    public static String getLocalServerName() {
        return getLocalServerName(ServerInfoUpdater.getServerName());
    }


    public static void sendSoundToPlayer(String target, Sound sound) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Map<String, String> playerServerMap = ServerInfoUpdater.getPlayerServerMap();
            String server = playerServerMap.getOrDefault(target, null);
            if (server == null) {
                return;
            }
            PlaySoundRequest playSoundRequest = new PlaySoundRequest();
            playSoundRequest.setTargetPlayer(target);
            playSoundRequest.setSoundName(sound.name());
            ServerMessageUtils.sendMessage(server, "TeleportRequest", playSoundRequest);
        });
    }

    public static void sendTellToPlayer(String source, String target, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Map<String, String> playerServerMap = ServerInfoUpdater.getPlayerServerMap();
            String server = playerServerMap.getOrDefault(target, null);
            if (server == null) {
                Bukkit.getLogger().warning("Failed tell to player, server is null!");
                return;
            }
            TellMessageRequest tellMessageRequest = new TellMessageRequest();
            tellMessageRequest.setSource(source);
            tellMessageRequest.setTarget(target);
            tellMessageRequest.setMessage(message);
            ServerMessageUtils.sendMessage(server, "TellMessage", tellMessageRequest);
        });
    }

    public static void universalTeleport(Player sourcePlayer, String target) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Map<String, String> playerServerMap = ServerInfoUpdater.getPlayerServerMap();
            String server = playerServerMap.getOrDefault(target, null);
            if (server == null) {
                Bukkit.getLogger().warning("Failed teleport to player, server is null!");
                return;
            }
            TeleportRequest teleportRequest = new TeleportRequest();
            teleportRequest.setSourcePlayer(sourcePlayer.getName());
            teleportRequest.setTargetPlayer(target);
            ServerMessageUtils.sendMessage(server, "TeleportRequest", teleportRequest);
        });
    }

    public static void universalPlotVisitByMessage(Player sourcePlayer, IslandId islandId) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            VisitRequest visitRequest = new VisitRequest(sourcePlayer.getUniqueId(), islandId);
            ServerMessageUtils.sendMessage(islandId.getServer(), "VisitRequest", visitRequest);
        });
    }
}
