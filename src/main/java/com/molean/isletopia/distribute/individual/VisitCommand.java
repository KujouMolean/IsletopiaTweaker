package com.molean.isletopia.distribute.individual;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.database.ParameterDao;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class VisitCommand implements CommandExecutor, TabCompleter, PluginMessageListener, Listener {
    private static final Map<String, String> visits = new HashMap<>();


    public VisitCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("visit")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("visit")).setTabCompleter(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Bukkit.getMessenger().registerIncomingPluginChannel(IsletopiaTweakers.getPlugin(), "BungeeCord", this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (visits.containsKey(player.getName())) {
            Bukkit.dispatchCommand(player, "plot visit " + visits.get(player.getName()));
            visits.remove(player.getName());
        }
        List<String> visits = UniversalParameter.getParameterAsList(event.getPlayer().getName(), "visits");
        if (visits.size() > 0) {
            player.sendMessage("§8[§3访客提醒§8] §e离线时的访客有:");
            player.sendMessage("§7  " + String.join(",", visits));
            UniversalParameter.setParameter(event.getPlayer().getName(), "visits", null);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player sourcePlayer = (Player) sender;
        if (args.length < 1)
            return true;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String targetServer = ParameterDao.get(args[0], "server");
            String source = sourcePlayer.getName();
            String target = args[0];
            boolean allow = true;
            UUID sourceUUID = ServerInfoUpdater.getUUID(source);
            UUID allUUID = PlotDao.getAllUUID();
            if (PlotDao.getPlotID(targetServer, target) == null) {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    sourcePlayer.kickPlayer("发生错误, 对方岛屿本应该存在, 但实际不存在.");
                });
                return;
            }
            List<UUID> denied = PlotDao.getDenied(targetServer, target);
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
                        sendMessageToPlayer(target, "§8[§3访客提醒§8] §e" + source + " 请求访问阁下岛屿但被拒绝了.");
                    }
                }
                sourcePlayer.sendMessage("§8[§3岛屿助手§8] §7对方没有岛屿或拒绝了你的访问.");
                return;
            }
            if (!target.equalsIgnoreCase(source) && !sourcePlayer.isOp()) {
                if (ServerInfoUpdater.getOnlinePlayers().contains(target)) {
                    sendMessageToPlayer(target, "§8[§3访客提醒§8] §e" + source + " 刚刚访问了阁下的岛屿.");
                } else {
                    if (!UniversalParameter.getParameterAsList(target, "visits").contains(source)) {
                        UniversalParameter.addParameter(target, "visits", source);
                    }
                }
            }

            try {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Forward");
                out.writeUTF(targetServer);
                out.writeUTF("visit");
                ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                DataOutputStream msgout = new DataOutputStream(msgbytes);
                msgout.writeUTF(source);
                msgout.writeUTF(args[0]);
                out.writeShort(msgbytes.toByteArray().length);
                out.write(msgbytes.toByteArray());
                sourcePlayer.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            assert targetServer != null;
            if (!targetServer.equalsIgnoreCase(ServerInfoUpdater.getServerName())) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("ConnectOther");
                out.writeUTF(source);
                out.writeUTF(targetServer);
                sourcePlayer.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
            }
        });
        return true;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = ServerInfoUpdater.getOnlinePlayers();
            playerNames.removeIf(s -> !s.startsWith(args[0]));
            return playerNames;
        } else {
            return new ArrayList<>();
        }
    }

    public void sendMessageToPlayer(String player, String message) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
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

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("visit")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

                String source = msgin.readUTF();
                String target = msgin.readUTF();

                Player sourcePlayer = Bukkit.getPlayer(source);
                if (sourcePlayer != null) {
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        Bukkit.dispatchCommand(sourcePlayer, "plot visit " + target);
                    });
                } else {
                    visits.put(source, target);
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
