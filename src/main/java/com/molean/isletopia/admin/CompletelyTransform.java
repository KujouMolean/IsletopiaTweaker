package com.molean.isletopia.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.database.*;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.shared.utils.UUIDManager;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@CommandAlias("transform")
@Singleton
@CommandPermission("isletopia.transform")
public class CompletelyTransform extends BaseCommand {
    @Default
    public void onDefault(Player player, String source, String target) {
        Tasks.INSTANCE.async(() -> {
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

}
