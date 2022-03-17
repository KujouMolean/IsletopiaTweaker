package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.database.*;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.shared.utils.UUIDManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
        PlayerParameterDao.deletePlayer(uuid);
        CollectionDao.deleteTarget(uuid);
        CollectionDao.deleteSource(uuid);
        IslandDao.deleteMember(uuid);

    }

    public void completelyReplaceAccount(UUID source, UUID target) throws SQLException, IOException {
        PlayerDataDao.replace(source, target);
        PlayerStatsDao.replace(source, target);
        PlayerParameterDao.replace(source, target);
        IslandDao.replaceOwner(source, target);
        IslandDao.replaceMember(source, target);
        CollectionDao.replaceTarget(source, target);
        CollectionDao.replaceSource(source, target);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;
        if (args.length < 2) {
            MessageUtils.fail(player, "/transform source target");
            return true;
        }

        String source = args[0];
        String target = args[1];

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            UUID sourceUUID = UUIDManager.get(source);
            if (sourceUUID == null) {
                MessageUtils.fail(player, "source not found!");
                return;
            }
            UUID targetUUID = UUIDManager.get(target);
            if (targetUUID == null) {
                targetUUID = UUIDManager.getOnlineSync(target);
            }
            if (IslandManager.INSTANCE.getPlayerIslandCount(targetUUID) > 0) {
                MessageUtils.fail(player, "target has island, delete it manually.");
                return;
            }
            try {
                completelyDeleteAccount(targetUUID);
                completelyReplaceAccount(sourceUUID, targetUUID);
                MessageUtils.success(player, "OK!");
            } catch (SQLException | IOException e) {
                MessageUtils.fail(player, "some error occurred!");
                e.printStackTrace();
            }
        });


        return true;
    }
}
