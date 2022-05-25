package com.molean.isletopia.infrastructure;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import com.molean.isletopia.database.DownloadDao;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.entity.Player;

import java.io.IOException;

@Singleton
@CommandAlias("download")
public class SaveDownload extends BaseCommand {

    @Default
    public void onCommand(Player player, @Flags("owner") LocalIsland localIsland) {
        Tasks.INSTANCE.async(() -> {
            try {
                DownloadDao.uploadSave(localIsland, token -> {
                    MessageUtils.info(player, "island.save.success");
                    String username = UUIDManager.get(localIsland.getUuid());
                    assert username != null;
                    username = username.replaceAll("#", "@");
                    player.sendMessage("http://save.molean.com/" + username + "?token=" + token);
                });
            } catch (IOException exception) {
                MessageUtils.fail(player, "island.save.failed.unexpected");
                exception.printStackTrace();
            }
        });
    }

}