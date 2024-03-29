package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.database.DownloadDao;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
@BukkitCommand("download")
public class SaveDownload implements CommandExecutor, TabCompleter {

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);


        if (currentIsland == null) {
            player.sendMessage("??");
            return true;
        }
        if (!Objects.equals(currentIsland.getUuid(), player.getUniqueId()) && !player.isOp()) {
            MessageUtils.fail(player, "island.save.failed.noPerm");
            return true;
        }

        Tasks.INSTANCE.async(() -> {
            try {
                DownloadDao.uploadSave(currentIsland, token -> {
                    MessageUtils.info(player, "island.save.success");
                    String username = UUIDManager.get(currentIsland.getUuid());
                    assert username != null;
                    username = username.replaceAll("#", "@");
                    player.sendMessage("http://save.molean.com/" + username + "?token=" + token);
                });
            } catch (IOException exception) {
                MessageUtils.fail(player, "island.save.failed.unexpected");
                exception.printStackTrace();
            }
        });
        return true;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return new ArrayList<>();
    }
}