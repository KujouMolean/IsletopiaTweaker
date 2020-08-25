package com.molean.isletopia.tweakers.tweakers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.database.PDBUtils;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class VisitCommand implements CommandExecutor, TabCompleter {
    public VisitCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("visit")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("visit")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player sourcePlayer = (Player) sender;
        if (args.length < 1)
            return true;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String targetServer = PDBUtils.get(args[0], "server");
            String source = sourcePlayer.getName();
            String target = args[0];
            boolean allow = true;
            UUID sourceUUID = IsletopiaTweakers.getUUID(source);
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
                sourcePlayer.sendMessage("§8[§3岛屿助手§8] §7对方没有岛屿或拒绝了你的访问.");
                return;
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
            if (!targetServer.equalsIgnoreCase(IsletopiaTweakers.getServerName())) {
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
            List<String> playerNames = IsletopiaTweakers.getOnlinePlayers();
            playerNames.removeIf(s -> !s.startsWith(args[0]));
            return playerNames;
        } else {
            return new ArrayList<>();
        }
    }
}
