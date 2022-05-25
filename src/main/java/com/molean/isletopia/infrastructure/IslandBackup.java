package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.shared.database.PlayerBackupDao;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.PlayerSerializeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Singleton
@BukkitCommand("backup")
public class IslandBackup implements CommandExecutor, TabCompleter {

    public IslandBackup() {


        Tasks.INSTANCE.interval(5 * 60 * 20, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.saveData();
            }
            Tasks.INSTANCE.timeoutAsync(20, () -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    PlayerBackupDao.upload(onlinePlayer.getName());
                }
            });
        });


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
            case "player" -> {
                if (args.length <= 1) {
                    player.sendMessage("参数不足");
                    return true;
                }

                try {
                    Player targetPlayer = Bukkit.getPlayerExact(args[1]);
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
                    player.sendMessage(id + " " + format);
                }
                return true;
            }

            case "restoreplayer" -> {
                if (args.length <= 2) {
                    player.sendMessage("参数不足");
                    return true;
                }
                Player targetPlayer = Bukkit.getPlayerExact(args[1]);
                if (targetPlayer == null) {
                    player.sendMessage("玩家不在线!");
                    return true;
                }
                int id = Integer.parseInt(args[2]);
                byte[] bytes = PlayerBackupDao.get(id);
                PlayerSerializeUtils.deserialize(targetPlayer, bytes, () -> {
                    player.sendMessage("成功!");
                });
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
