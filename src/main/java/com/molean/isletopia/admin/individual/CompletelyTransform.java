package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.database.*;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.shared.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class CompletelyTransform implements CommandExecutor {

    public CompletelyTransform() {
        Objects.requireNonNull(Bukkit.getPluginCommand("transform")).setExecutor(this);
    }


    public void completelyDeleteAccount(UUID uuid) throws SQLException, IOException {
        PlayerDataDao.delete(uuid);
        PlayerStatsDao.delete(uuid);
        ParameterDao.deletePlayer(uuid);
        CollectionDao.deleteTarget(uuid);
        CollectionDao.deleteSource(uuid);
        IslandDao.deleteMember(uuid);

    }

    public void completelyReplaceAccount(UUID source, UUID target) throws SQLException, IOException {
        PlayerDataDao.replace(source, target);
        PlayerStatsDao.replace(source, target);
        ParameterDao.replace(source, target);
        IslandDao.replaceOwner(source, target);
        IslandDao.replaceMember(source, target);
        CollectionDao.replaceTarget(source, target);
        CollectionDao.replaceSource(source, target);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 2) {
            MessageUtils.fail(sender, "/transform source target");
            return true;
        }

        String source = args[0];
        String target = args[1];

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            UUID sourceUUID = UUIDUtils.get(source);
            if (sourceUUID == null) {
                MessageUtils.fail(sender, "未找到source用户");
                return;
            }
            UUID targetUUID = UUIDUtils.get(target);
            if (targetUUID == null) {
                targetUUID = UUIDUtils.getOnlineSync(target);
            }
            if (IslandManager.INSTANCE.getPlayerIslandCount(targetUUID) > 0) {
                MessageUtils.fail(sender, "target用户存在岛屿,请手动删除.");
                return;
            }
            try {
                completelyDeleteAccount(targetUUID);
                completelyReplaceAccount(sourceUUID, targetUUID);
                MessageUtils.success(sender, "OK!");
            } catch (SQLException | IOException e) {
                MessageUtils.fail(sender, "Some error!");
                e.printStackTrace();
            }

        });


        return true;
    }
}
