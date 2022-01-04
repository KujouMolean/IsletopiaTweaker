package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.DownloadDao;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.utils.UUIDUtils;
import org.bukkit.Bukkit;
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

public class SaveDownload implements CommandExecutor, TabCompleter {
    public SaveDownload() {
        Objects.requireNonNull(Bukkit.getPluginCommand("download")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("download")).setTabCompleter(this);
        DownloadDao.checkTable();
    }

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
            player.sendMessage("你没有权限这么做。");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            try {
                DownloadDao.uploadSave(currentIsland, token -> {
                    player.sendMessage("成功！请点击下方链接进行下载，此链接仅在10分钟内有效。");
                    String username = UUIDUtils.get(currentIsland.getUuid());
                    assert username != null;
                    username = username.replaceAll("#", "@");
                    player.sendMessage("http://save.molean.com/" + username + "?token=" + token);
                });
            } catch (IOException exception) {
                player.sendMessage("生成下载链接失败！");
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