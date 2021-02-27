package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.BackupDao;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PlotBackup implements CommandExecutor {
    public PlotBackup() {
        BackupDao.checkTable();
        Objects.requireNonNull(Bukkit.getPluginCommand("backup")).setExecutor(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!PlotUtils.hasCurrentPlotPermission(onlinePlayer)) {
                    continue;
                }
                Plot currentPlot = PlotUtils.getCurrentPlot(onlinePlayer);
                assert currentPlot != null;
                PlotId id = currentPlot.getId();
                backup(id.getX() - 1, id.getY() - 1);
                String serverName = MessageUtils.getLocalServerName();
                String format = String.format("§8[§3岛屿助手§8] §6%s(%d,%d)已自动备份.", serverName, id.getX(), id.getY());
                onlinePlayer.sendMessage(format);
            }
        }, 5 * 60 * 20, 10 * 60 * 20);
    }

    public static void backup(int mcaX, int mcaY) {
        World world = Bukkit.getWorlds().get(0);
        File worldFolder = world.getWorldFolder();
        File file = new File(worldFolder + String.format("/region/r.%d.%d.mca", mcaX, mcaY));
        BackupDao.upload(file.toString());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        assert currentPlot != null;
        PlotId id = currentPlot.getId();
        String filename = String.format("r.%d.%d.mca", id.getX() - 1, id.getY() - 1);

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (args.length == 0) {
                backup(id.getX() - 1, id.getY() - 1);
                String serverName = MessageUtils.getLocalServerName();
                String format = String.format("§8[§3岛屿助手§8] §6%s(%d,%d)已自动备份.", serverName, id.getX(), id.getY());
                player.sendMessage(format);

            } else {
                if (!player.isOp()) {
                    return;
                }
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "list": {
                        int start = 0;
                        if (args.length >= 2) {
                            start = 10 * Integer.parseInt(args[1]);
                        }
                        Map<Integer, Long> list = BackupDao.list(filename);
                        ArrayList<Integer> keys = new ArrayList<>(list.keySet());
                        keys.sort((o1, o2) -> o2 - o1);
                        for (int i = start; i < start + 10 && i < keys.size(); i++) {
                            Instant instant = Instant.ofEpochMilli(list.get(keys.get(i)));
                            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                            player.sendMessage(keys.get(i) + " " +localDateTime
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
                        }
                        break;
                    }
                    case "download": {
                        if (args.length < 2) {
                            player.sendMessage("Please specific id.");
                            return;
                        }
                        BackupDao.download(Integer.parseInt(args[1]));
                        player.sendMessage("OK, saved as " + filename);
                        break;
                    }
                    default: {
                        player.sendMessage("Unknown sub command.");
                    }
                }
            }
        });

        return true;
    }
}
