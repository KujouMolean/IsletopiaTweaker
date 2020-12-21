package com.molean.isletopia;

import com.molean.isletopia.admin.IsletopiaAdmin;
import com.molean.isletopia.bungee.IsletopiaBungee;
import com.molean.isletopia.distribute.IsletopiaDistributeSystem;
import com.molean.isletopia.distribute.parameter.IsletopiaParamter;
import com.molean.isletopia.infrastructure.IsletopiaInfrastructure;
import com.molean.isletopia.infrastructure.individual.MenuCommand;
import com.molean.isletopia.modifier.IsletopiaModifier;
import com.molean.isletopia.protect.IsletopiaProtect;
import com.molean.isletopia.statistics.IsletopiaStatistics;
import com.molean.isletopia.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Objects;

public final class IsletopiaTweakers extends JavaPlugin {

    private static IsletopiaTweakers isletopiaTweakers;

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }


    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        isletopiaTweakers = this;
        ConfigUtils.setupConfig(this);
        new IsletopiaInfrastructure();
        new IsletopiaModifier();
        new IsletopiaDistributeSystem();
        new IsletopiaParamter();
        new IsletopiaProtect();
        new MenuCommand();
        new IsletopiaBungee();
        new IsletopiaStatistics();
        new IsletopiaAdmin();

//        new IsletopiaStory();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


        // 向文件持续写入当前时间, 每分钟一次.
        // Keep write current time to plugin folder per minute.
        File dataFolder = getDataFolder();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            File file = new File(dataFolder + "/status");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write((System.currentTimeMillis() + "").getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 60 * 20);

        // 持续读取文件, 若时间与当前时间相差过大, 则停止服务器, 并在30秒后强制关闭程序, 每两分钟一次.
        // Keep read file per two minutes.
        // If timestamp difference too big then stop server, and kill process gracefully after 30s.
        new Thread(() -> {
            while (true) {

                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                File file = new File(dataFolder + "/status");

                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    long l = Long.parseLong(new String(inputStream.readAllBytes()));
                    if (System.currentTimeMillis() - l > 1000 * 60 * 2) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(30 * 1000);
                                Class<?> aClass = Class.forName("java.lang.Shutdown");
                                Method halt = aClass.getDeclaredMethod("halt", int.class);
                                halt.setAccessible(true);
                                halt.invoke(null, -1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();

                        Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).save();
                        Bukkit.shutdown();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
