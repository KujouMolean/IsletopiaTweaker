package com.molean.isletopia.infrastructure;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.database.PlayerBackupDao;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.PlayerSerializeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Singleton
@CommandAlias("backup")
@CommandPermission("isletopia.backup")
public class IslandBackup extends BaseCommand {

    @Interval(5 * 60 * 20)
    public void savePlayerData() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.saveData();
        }
        Tasks.INSTANCE.timeoutAsync(20, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PlayerBackupDao.upload(onlinePlayer.getName());
            }
        });
    }

    @Subcommand("apply")
    public void apply(Player player, String target) {
        try {
            Player targetPlayer = Bukkit.getPlayerExact(target);
            if (targetPlayer != null && targetPlayer.isOnline()) {
                targetPlayer.saveData();
            }
            PlayerBackupDao.upload(target);
        } catch (Exception e) {
            player.sendMessage("§c备份失败!");
        }
    }

    @CommandAlias("list")
    public void list(Player player, String target) {
        List<Pair<Integer, Timestamp>> list = null;
        try {
            list = PlayerBackupDao.list(target);
        } catch (Exception e) {
            player.sendMessage("§c失败!");
            return;
        }

        for (Pair<Integer, Timestamp> integerTimestampPair : list) {
            Integer id = integerTimestampPair.getKey();
            Timestamp value = integerTimestampPair.getValue();
            LocalDateTime localDateTime = value.toLocalDateTime();
            String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
            player.sendMessage(id + " " + format);
        }
    }


    @CommandAlias("restore")
    public void restore(Player player, String target, int backupID) {
        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null) {
            player.sendMessage("玩家不在线!");
            return;
        }
        byte[] bytes = PlayerBackupDao.get(backupID);
        PlayerSerializeUtils.deserialize(targetPlayer, bytes, () -> {
            player.sendMessage("成功!");
        });

    }

}
