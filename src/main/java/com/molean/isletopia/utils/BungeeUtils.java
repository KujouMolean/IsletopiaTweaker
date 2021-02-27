package com.molean.isletopia.utils;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.infrastructure.individual.MessageUtils;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.VisitRequest;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BungeeUtils {

    public static void switchServer(Player player, String server) {
        @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player.getName());
        out.writeUTF(server);
        player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static void sendMessageToPlayer(String player, String message) {
        try {
            @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ForwardToPlayer");
            out.writeUTF(player);
            out.writeUTF("tell");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(message);
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            Player first = Iterables.getFirst(Bukkit.getServer().getOnlinePlayers(), null);
            if (first == null) {
                return;
            }
            first.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void sendVisitNotificationToPlayer(String player, String visitor, boolean isFailed) {
        try {
            @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ForwardToPlayer");
            out.writeUTF(player);
            if (isFailed) {
                out.writeUTF("failedVisitor");
            } else {
                out.writeUTF("visitor");
            }
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(visitor);
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            Player first = Iterables.getFirst(Bukkit.getServer().getOnlinePlayers(), null);
            if (first == null) {
                return;
            }
            first.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void universalChat(Player player, String message) {
        try {
            @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("BungeeCord");
            out.writeUTF("chat");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(player.getName());
            msgout.writeUTF(message);
            msgout.writeUTF("server");
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void updateIgnores(Player player, String ignores) {
        if (ignores == null) {
            ignores = "";
        }
        try {
            @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("BungeeCord");
            out.writeUTF("ignores");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(player.getName());
            msgout.writeUTF(ignores);
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void sendSoundToPlayer(String target, Sound sound) {
        try {
            @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ForwardToPlayer");
            out.writeUTF(target);
            out.writeUTF("sound");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(target);
            msgout.writeUTF(sound.name());
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            Player first = Iterables.getFirst(Bukkit.getServer().getOnlinePlayers(), null);
            if (first == null) {
                return;
            }
            first.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void universalTeleport(Player sourcePlayer, String target) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String source = sourcePlayer.getName();
            String targetServer = null;
            Map<String, List<String>> playersPerServer = ServerInfoUpdater.getPlayersPerServer();
            for (String s : playersPerServer.keySet()) {
                if (playersPerServer.get(s).contains(target)) {
                    targetServer = s;
                }
            }
            if (targetServer == null) {
                return;
            }
            try {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Forward");
                out.writeUTF(targetServer);
                out.writeUTF("tp");
                ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                DataOutputStream msgout = new DataOutputStream(msgbytes);
                msgout.writeUTF(source);
                msgout.writeUTF(target);
                out.writeShort(msgbytes.toByteArray().length);
                out.write(msgbytes.toByteArray());
                sourcePlayer.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            if (!targetServer.equalsIgnoreCase(ServerInfoUpdater.getServerName())) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("ConnectOther");
                out.writeUTF(source);
                out.writeUTF(targetServer);
                sourcePlayer.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
            }
        });

    }


    public static void universalPlotVisitByMessage(Player sourcePlayer, String target) {

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {

            String source = sourcePlayer.getName();
            String targetServer = UniversalParameter.getParameter(target, "server");
            if (targetServer == null) {
                sourcePlayer.sendMessage(MessageUtils.getMessage("error.visit.noIsland"));
                return;
            }

            VisitRequest visitRequest = new VisitRequest(source, target);
            ServerMessageManager.sendMessage(targetServer, "VisitRequest", visitRequest);
        });
    }
}
