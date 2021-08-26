package com.molean.isletopia.infrastructure.individual;

import com.google.common.io.Files;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlayerBackupDao;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PlotBackup implements CommandExecutor, TabCompleter {

    public PlotBackup() {
        Objects.requireNonNull(Bukkit.getPluginCommand("backup")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("backup")).setTabCompleter(this);

        PlayerBackupDao.checkTable();

        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.saveData();
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    PlayerBackupDao.upload(onlinePlayer.getName());
                }

            }, 20L);
        }, 5 * 60 * 20, 5 * 60 * 20);


        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {

            Set<PlotId> plotIdSet = new HashSet<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Plot currentPlot = PlotUtils.getCurrentPlot(onlinePlayer);
                if (currentPlot == null) {
                    continue;
                }
                plotIdSet.add(currentPlot.getId());
            }
            for (PlotId plotId : plotIdSet) {
                try {
                    backup(plotId.getX() - 1, plotId.getY() - 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }, 10 * 60 * 20, 10 * 60 * 20);

    }


    public static boolean deleteFile(File dirFile) {
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {
            File[] files = dirFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFile(file);
                }
            }
        }
        return dirFile.delete();
    }

    public static void trim(int mcaX, int mcaY) {
        File backupFolder = new File(String.format("backup/%d,%d/", mcaX, mcaY));
        File[] files = backupFolder.listFiles((dir, name) -> {
            return name.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}-[0-9]{2}-[0-9]{2}-[0-9]{2}");
        });

        if (files == null) {
            return;
        }

        for (File file : files) {
            String name = file.getName();
            LocalDateTime parse = LocalDateTime.parse(name, DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
            if (LocalDateTime.now().minusDays(7).isAfter(parse)) {
                boolean b = deleteFile(file);
            }
        }
    }

    @SuppressWarnings("all")
    public static void backup(int mcaX, int mcaY) throws IOException {

        trim(mcaX, mcaY);

        String filename = String.format("r.%d.%d.mca", mcaX, mcaY);
        File regionSource = new File("SkyWorld/region/" + filename);
        File poiSource = new File("SkyWorld/poi/" + filename);
        File entitiesSource = new File("SkyWorld/entities/" + filename);

        if (!regionSource.exists()) {
            boolean newFile = regionSource.createNewFile();
        }
        if (!poiSource.exists()) {
            boolean newFile = poiSource.createNewFile();
        }
        if (!entitiesSource.exists()) {
            boolean newFile = entitiesSource.createNewFile();
        }

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));


        File regionTargetFolder = new File(String.format("backup/%d,%d/%s/region/", mcaX, mcaY, time));
        File poiTargetFolder = new File(String.format("backup/%d,%d/%s/poi/", mcaX, mcaY, time));
        File entitiesTargetFolder = new File(String.format("backup/%d,%d/%s/entities/", mcaX, mcaY, time));

        if (!regionTargetFolder.exists()) {
            boolean mkdir = regionTargetFolder.mkdirs();
        }
        if (!poiTargetFolder.exists()) {
            boolean mkdir = poiTargetFolder.mkdirs();
        }
        if (!entitiesTargetFolder.exists()) {
            boolean mkdir = entitiesTargetFolder.mkdirs();
        }

        File regionTarget = new File(regionTargetFolder + "/" + filename);
        File poiTarget = new File(poiTargetFolder + "/" + filename);
        File entitiesTarget = new File(entitiesTargetFolder + "/" + filename);

        Files.copy(regionSource, regionTarget);
        Files.copy(poiSource, poiTarget);
        Files.copy(entitiesSource, entitiesTarget);


    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            return true;
        }
        if (!player.isOp()) {
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "plot" -> {
                Plot plot = PlotUtils.getCurrentPlot(player);
                assert plot != null;
                try {
                    backup(plot.getId().getX() - 1, plot.getId().getY() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("§c备份失败!");
                    return true;
                }
                player.sendMessage("§c备份成功!");
                return true;
            }

            case "player" -> {

                if (args.length <= 1) {
                    player.sendMessage("参数不足");
                    return true;
                }

                try {
                    Player targetPlayer = Bukkit.getPlayer(args[1]);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        targetPlayer.saveData();
                    }
                    PlayerBackupDao.upload(args[1]);
                } catch (Exception e) {
                    player.sendMessage("§c备份失败!");
                    return true;
                }
                player.sendMessage("备份成功!");
                return true;
            }

            case "listplayer" -> {
                if (args.length <= 1) {
                    player.sendMessage("参数不足");
                    return true;
                }

                List<Pair<Integer, Timestamp>> list = null;
                try {
                    list = PlayerBackupDao.list(args[1]);
                } catch (Exception e) {
                    player.sendMessage("§c失败!");
                    return true;
                }

                for (Pair<Integer, Timestamp> integerTimestampPair : list) {
                    Integer id = integerTimestampPair.getKey();
                    Timestamp value = integerTimestampPair.getValue();
                    LocalDateTime localDateTime = value.toLocalDateTime();
                    String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
                    player.sendMessage(integerTimestampPair.getKey() + " " + format);
                }
                return true;
            }

            case "restoreplayer" -> {
                if (args.length <= 2) {
                    player.sendMessage("参数不足");
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayer(args[1]);

                if (targetPlayer == null) {
                    player.sendMessage("玩家不在线!");
                    return true;
                }

                int id = Integer.parseInt(args[2]);

                try {
                    PlayerBackupDao.restore(targetPlayer, id);
                    targetPlayer.loadData();
                } catch (Exception e) {
                    player.sendMessage("失败!");
                    return true;
                }

                player.sendMessage("成功!");
                return true;
            }

        }
        return true;


    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        ArrayList<String> list = new ArrayList<>();

        switch (strings.length) {
            case 1 -> {
                list.add("plot");
                list.add("player");
                list.add("listplayer");
                list.add("restoreplayer");
                return list;
            }
            case 2 -> {
                if (strings[0].toLowerCase(Locale.ROOT).contains("player")) {
                    return null;
                } else {
                    return list;
                }
            }
            default -> {
                return list;
            }
        }
    }
}
