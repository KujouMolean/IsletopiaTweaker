package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.obj.VisitNotificationObject;
import com.molean.isletopia.shared.pojo.req.PlaySoundRequest;
import com.molean.isletopia.shared.pojo.req.TeleportRequest;
import com.molean.isletopia.shared.pojo.req.TellMessageRequest;
import com.molean.isletopia.shared.pojo.req.VisitRequest;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

public class IsletopiaTweakersUtils {
    public static String getLocalServerName() {
        return switch (ServerInfoUpdater.getServerName()) {
            case "server1" -> "衔心岠";
            case "server2" -> "梦华沢";
            case "server3" -> "凌沧州";
            case "server4" -> "净琉璃世界";
            case "server5" -> "东海蓬莱";
            case "server6" -> "佳和苑";
            case "server7" -> "楠故㟓";
            case "server8" -> "胧月花栞";
            default -> "未知";
        };
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

    public static void sendVisitNotificationToPlayer(String player, String visitor, boolean isFailed) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Map<String, String> playerServerMap = ServerInfoUpdater.getPlayerServerMap();
            String server = playerServerMap.getOrDefault(player, null);
            if (server == null) {
                Bukkit.getLogger().warning("Failed send visit notification to player, player is not online!");
                return;
            }
            VisitNotificationObject visitNotificationObject = new VisitNotificationObject();
            visitNotificationObject.setVisitor(visitor);
            visitNotificationObject.setTarget(player);
            visitNotificationObject.setSuccess(!isFailed);
            ServerMessageUtils.sendMessage(server, "VisitNotification", visitNotificationObject);
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

    public static void universalPlotVisitByMessage(Player sourcePlayer, String target) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String source = sourcePlayer.getName();
            String targetServer = UniversalParameter.getParameter(target, "server");
            if (targetServer == null) {
                sourcePlayer.sendMessage("§8[§3岛屿助手§8] §6对方岛屿不存在.");
                return;
            }

            VisitRequest visitRequest = new VisitRequest(source, target);
            ServerMessageUtils.sendMessage(targetServer, "VisitRequest", visitRequest);
        });
    }
}
