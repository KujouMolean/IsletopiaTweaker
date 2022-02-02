package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.database.MSPTDao;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Random;

public class UploadServerMSPT {
    public UploadServerMSPT() throws Exception {
        MSPTDao.checkTable();
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            try {
                Class<?> minecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");
                Method getServerMethod = minecraftServerClass.getDeclaredMethod("getServer");
                Object minecraftServer = getServerMethod.invoke(null);
                Field tickTimes60sField = minecraftServerClass.getDeclaredField("tickTimes60s");
                Object tickTimes60s = tickTimes60sField.get(minecraftServer);
                Method getAverageMethod = tickTimes60s.getClass().getDeclaredMethod("getAverage");
                double mspt = (double) getAverageMethod.invoke(tickTimes60s);
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
