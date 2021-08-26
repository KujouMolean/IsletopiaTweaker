package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.DownloadDao;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
        Plot currentPlot = PlotUtils.getCurrentPlot(player);

        assert currentPlot != null;

        UUID owner = currentPlot.getOwner();
        assert owner != null;
        if (!Objects.equals(owner, player.getUniqueId()) && !player.isOp()) {
            player.sendMessage("你没有权限这么做。");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String single = PlotSquared.get().getImpromptuUUIDPipeline().getSingle(owner, 100L);
            try {
                String token = DownloadDao.uploadSave(currentPlot);
                player.sendMessage("成功！请点击下方链接进行下载，此链接仅在10分钟内有效。");
                player.sendMessage("http://save.molean.com/" + single + "?token=" + token);
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