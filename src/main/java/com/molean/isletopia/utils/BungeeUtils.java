package com.molean.isletopia.utils;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.infrastructure.individual.I18n;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class BungeeUtils {
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
            @SuppressWarnings("all")ByteArrayDataOutput out = ByteStreams.newDataOutput();
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
            @SuppressWarnings("all")ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("BungeeCord");
            out.writeUTF("chat");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(player.getName());
            msgout.writeUTF(message);
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    @SuppressWarnings("all")
    public static void universalPlotVisit(Player sourcePlayer, String target) {

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            long start = System.currentTimeMillis();

            sourcePlayer.sendActionBar("§d§f■■■■■");
            String source = sourcePlayer.getName();
            String targetServer = UniversalParameter.getParameter(target, "server");

            while (System.currentTimeMillis() - start < 500) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sourcePlayer.sendActionBar("§d■§f■■■■");

            if (targetServer == null) {
                sourcePlayer.sendMessage(I18n.getMessage("error.visit.noIsland", sourcePlayer));
                return;
            }
            boolean allow = true;
            UUID sourceUUID = ServerInfoUpdater.getUUID(source);
            UUID allUUID = PlotDao.getAllUUID();
            if (PlotDao.getPlotID(targetServer, target) == null) {
                sourcePlayer.sendMessage(I18n.getMessage("error.visit.noIsland", sourcePlayer));
                return;
            }

            while (System.currentTimeMillis() - start < 1000) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sourcePlayer.sendActionBar("§d■■§f■■■");
            List<UUID> denied = PlotDao.getDenied(targetServer, target);


            while (System.currentTimeMillis() - start < 1500) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sourcePlayer.sendActionBar("§d■■■§f■■");
            List<UUID> trusted = PlotDao.getTrusted(targetServer, target);
            if (denied.contains(sourceUUID) || denied.contains(allUUID)) {
                allow = false;
            }
            if (trusted.contains(sourceUUID) || target.equalsIgnoreCase(source) || sourcePlayer.isOp()) {
                allow = true;
            }
            if (!allow) {
                if (!sourcePlayer.isOp() && !target.equalsIgnoreCase(source)) {
                    if (ServerInfoUpdater.getOnlinePlayers().contains(target)) {
                        BungeeUtils.sendVisitNotificationToPlayer(target, source, true);
                    }
                }
                sourcePlayer.sendMessage(I18n.getMessage("error.visit.refused", sourcePlayer));
                return;
            }

            while (System.currentTimeMillis() - start < 2000) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sourcePlayer.sendActionBar("§d■■■■§f■");
            if (!target.equalsIgnoreCase(source) && !sourcePlayer.isOp()) {
                if (ServerInfoUpdater.getOnlinePlayers().contains(target)) {
                    BungeeUtils.sendVisitNotificationToPlayer(target, source, false);
                } else {
                    if (!UniversalParameter.getParameterAsList(target, "visits").contains(source)) {
                        UniversalParameter.addParameter(target, "visits", source);
                    }
                }
            }

            while (System.currentTimeMillis() - start < 2500) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sourcePlayer.sendActionBar("§d■■■■■§f");

            try {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Forward");
                out.writeUTF(targetServer);
                out.writeUTF("visit");
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
}
