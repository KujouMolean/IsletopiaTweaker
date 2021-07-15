package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.bungee.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.PlaySoundRequest;
import com.molean.isletopia.message.obj.TeleportRequest;
import com.molean.isletopia.message.obj.TellMessageRequest;
import com.molean.isletopia.message.obj.VisitRequest;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

public class ServerMessageUtils {
    public static void sendSoundToPlayer(String target, Sound sound) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Map<String, String> playerServerMap = ServerInfoUpdater.getPlayerServerMap();
            String server = playerServerMap.getOrDefault(target,null);
            if(server==null){
                return;
            }
            PlaySoundRequest playSoundRequest = new PlaySoundRequest();
            playSoundRequest.setTargetPlayer(target);
            playSoundRequest.setSoundName(sound.name());
            ServerMessageManager.sendMessage(server, "TeleportRequest", playSoundRequest);
        });
    }

    public static void sendTellToPlayer(String source,String target,String message) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Map<String, String> playerServerMap = ServerInfoUpdater.getPlayerServerMap();
            String server = playerServerMap.getOrDefault(target,null);
            if(server==null){
                Bukkit.getLogger().warning("Failed tell to player, server is null!");
                return;
            }
            TellMessageRequest tellMessageRequest = new TellMessageRequest();
            tellMessageRequest.setSource(source);
            tellMessageRequest.setTarget(target);
            tellMessageRequest.setMessage(message);
            ServerMessageManager.sendMessage(server, "TellMessage", tellMessageRequest);
        });
    }

    public static void universalTeleport(Player sourcePlayer, String target) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Map<String, String> playerServerMap = ServerInfoUpdater.getPlayerServerMap();
            String server = playerServerMap.getOrDefault(target,null);
            if(server==null){
                Bukkit.getLogger().warning("Failed teleport to player, server is null!");
                return;
            }
            TeleportRequest teleportRequest = new TeleportRequest();
            teleportRequest.setSourcePlayer(sourcePlayer.getName());
            teleportRequest.setTargetPlayer(target);
            ServerMessageManager.sendMessage(server, "TeleportRequest", teleportRequest);
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
            ServerMessageManager.sendMessage(targetServer, "VisitRequest", visitRequest);
        });
    }




}
