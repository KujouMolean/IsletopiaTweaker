package com.molean.isletopia.infrastructure.individual;

import com.google.common.io.Files;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.IslandBackupDao;
import com.molean.isletopia.database.PlayerBackupDao;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandId;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class IslandBackup implements CommandExecutor, TabCompleter {

    public IslandBackup() {
        Objects.requireNonNull(Bukkit.getPluginCommand("backup")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("backup")).setTabCompleter(this);

        //check database before use
        PlayerBackupDao.checkTable();

        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.saveData();
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    PlayerBackupDao.upload(onlinePlayer.getName());;
                }

            }, 20L);
        }, 5 * 60 * 20, 5 * 60 * 20);

        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Set<IslandId> plotIdSet = new HashSet<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);
                if (currentPlot == null) {
                    continue;
                }
                plotIdSet.add(currentPlot.getIslandId());
            }
            for (IslandId plotId : plotIdSet) {
                try {
                    backup(plotId);
                } catch (Exception e) {
                    e.printStackTrace();
                    Island island = IslandManager.INSTANCE.getIsland(plotId);
                    if (island == null) {
                        return;
                    }
                    for (Player player : island.getPlayersInIsland()) {
                        if (IslandManager.INSTANCE.hasCurrentIslandPermission(player)) {
                            MessageUtils.warn(player, "你的岛屿备份失败，请及时联系管理员！");
                        }
                    }
                }
            }

        }, 10 * 60 * 20, 10 * 60 * 20);
    }


    @SuppressWarnings("all")
    public static void backup(IslandId islandId) throws IOException, SQLException {
        String filename = String.format("r.%d.%d.mca", islandId.getX(), islandId.getZ());
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

        FileInputStream regionInput = new FileInputStream(regionSource);
        FileInputStream poiInput = new FileInputStream(poiSource);
        FileInputStream entitiesInput = new FileInputStream(entitiesSource);
        IslandBackupDao.upload(islandId, regionInput, poiInput, entitiesInput);
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
            case "island" -> {
                Island plot = IslandManager.INSTANCE.getCurrentIsland(player);
                assert plot != null;
                try {
                    backup(plot.getIslandId());
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("§c备份失败!");
                    return true;
                }
                player.sendMessage("§c备份成功!");
                return true;
            }
            case "listisland"->{
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    player.sendMessage("?");
                    return true;
                }
                List<Pair<Integer, Timestamp>> list = IslandBackupDao.list(currentIsland.getIslandId());
                for (Pair<Integer, Timestamp> integerTimestampPair : list) {
                    Integer id = integerTimestampPair.getKey();
                    Timestamp value = integerTimestampPair.getValue();
                    LocalDateTime localDateTime = value.toLocalDateTime();
                    String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
                    player.sendMessage(integerTimestampPair.getKey() + " " + format);
                }

            }
            case "download"->{
                if (args.length <= 1) {
                    player.sendMessage("参数不足");
                    return true;
                }
                int id = Integer.parseInt(args[1]);
                try {
                    IslandBackupDao.download(id);
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("成功");

                }
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
                list.add("island");
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
