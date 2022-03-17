package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.database.MSPTDao;
import com.molean.isletopia.utils.MSPTUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Random;

public class UploadServerMSPT {
    public UploadServerMSPT() throws Exception {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            try {
                double mspt = MSPTUtils.get();

                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    try {
                        MSPTDao.addRecord(ServerInfoUpdater.getServerName(), mspt);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, new Random().nextInt(20 * 5 * 60), 20 * 5 * 60);
        IsletopiaTweakers.addDisableTask("stop upload server mspt", bukkitTask::cancel);
    }
}
