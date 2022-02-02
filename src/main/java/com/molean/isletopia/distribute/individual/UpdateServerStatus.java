package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.utils.RedisUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class UpdateServerStatus {
    public UpdateServerStatus() {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                long l = System.currentTimeMillis();
                RedisUtils.getCommand().set("ServerStatus:LastUpdate:" + ServerInfoUpdater.getServerName(), l + "");
            });
        }, 0, 20);
        IsletopiaTweakers.addDisableTask("stop update server status", bukkitTask::cancel);
    }
}
