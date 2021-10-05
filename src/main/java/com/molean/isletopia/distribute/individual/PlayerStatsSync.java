package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlayerStatsDao;
import com.molean.isletopia.utils.MessageUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.stats.ServerStatisticManager;
import net.minecraft.stats.StatisticManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class PlayerStatsSync implements Listener {

    private final Map<String, String> passwdMap = new HashMap<>();

    public PlayerStatsSync() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        // check table
        try {
            PlayerStatsDao.checkTable();
        } catch (SQLException e) {
            e.printStackTrace();
            //stop server if check has error
            Logger.getAnonymousLogger().severe("Database check error!");
            Bukkit.shutdown();
        }

        IsletopiaTweakers.addDisableTask("Save player stats to database", () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (passwdMap.containsKey(onlinePlayer.getName())) {
                    onLeft(onlinePlayer);
                }
            }
        });

        // load data to current player
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onJoin(onlinePlayer);
        }

        //update one player data per second
        Queue<Player> queue = new ArrayDeque<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (queue.isEmpty()) {
                queue.addAll(Bukkit.getOnlinePlayers());
                return;
            }

            Player player = queue.poll();

            if (player.isOnline() && passwdMap.containsKey(player.getName())) {

                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    update(player);
                });

            }
        }, 20, 20);

    }


    public void update(Player player) {
        try {
            String stats = getStats(player);
            if (!PlayerStatsDao.update(player.getName(), stats, passwdMap.get(player.getName()))) {
                throw new RuntimeException("Unexpected complete player stats error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.warn(player, "你的统计保存失败，请尽快联系管理员处理！");
        }
    }

    public void onLeft(Player player) {
        try {
            String stats = getStats(player);
            if (!PlayerStatsDao.complete(player.getName(), stats, passwdMap.get(player.getName()))) {
                throw new RuntimeException("Unexpected complete player stats error!");
            }
            passwdMap.remove(player.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onJoin(Player player) {

        try {
            if (!PlayerStatsDao.exist(player.getName())) {
                //插入数据
                String stats = getStats(player);
                PlayerStatsDao.insert(player.getName(), stats);
            }
            //拿锁
            String passwd = PlayerStatsDao.getLock(player.getName());
            if (passwd != null) {
                loadData(player, passwd);
                // end
            } else {
                //没拿到, 开始等锁
                Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), new Consumer<>() {
                    private int times = 0;

                    @Override
                    public void accept(BukkitTask task) {
                        try {
                            //尝试拿锁
                            String lock = PlayerStatsDao.getLock(player.getName());
                            if (lock != null) {
                                loadData(player, lock);
                                task.cancel();
                                //end
                                return;
                            }
                            times++;
                            if (times > 15) {
                                task.cancel();
                                //等待超时, 可能是上个服务器崩了, 强制拿锁
                                String lockForce = PlayerStatsDao.getLockForce(player.getName());
                                if (lockForce == null) {
                                    //强制拿锁失败, 出大问题
                                    throw new RuntimeException("Unexpected error! Force get lock failed.");
                                    //end (failed)
                                }
                                loadData(player, lockForce);
                                //end (success)
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MessageUtils.warn(player, "你的统计信息读取错误, 可能已经被污染.");
                        }
                    }
                }, 20, 20);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.warn(player, "你的统计信息读取错误, 可能已经被污染.");
        }
    }

    private void loadData(Player player, String passwd) throws SQLException, IOException {
        passwdMap.put(player.getName(), passwd);
        //强制拿到锁了, 加载数据

        String stats = PlayerStatsDao.query(player.getName(), passwd);

        if (stats == null) {
            throw new RuntimeException("Unexpected get player data failed!");
            //end (failed)
        }
        loadStats(player, stats);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerJoinEvent event) {
        onJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerQuitEvent event) {
        if (passwdMap.containsKey(event.getPlayer().getName())) {
            onLeft(event.getPlayer());
        }
    }


    //utils below
    private static final String TO_JSON_METHOD = "b";
    private static final String STATS_FIELD = "a";


    public static String getStats(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Must run in main thread");
        }
        try {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            assert craftPlayer != null;
            EntityPlayer entityPlayer = craftPlayer.getHandle();
            ServerStatisticManager statisticManager = entityPlayer.getStatisticManager();
            Method toJsonMethod = ServerStatisticManager.class.getDeclaredMethod(TO_JSON_METHOD);
            toJsonMethod.setAccessible(true);
            return (String) toJsonMethod.invoke(statisticManager);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't get stats by nms!");
        }
    }

    public static void loadStats(Player player, String json) {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Must run in main thread");
        }
        try {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            assert craftPlayer != null;
            EntityPlayer entityPlayer = craftPlayer.getHandle();
            MinecraftServer minecraftServer = entityPlayer.c;
            ServerStatisticManager statisticManager = entityPlayer.getStatisticManager();
            Field stats = StatisticManager.class.getDeclaredField(STATS_FIELD);
            stats.setAccessible(true);
            @SuppressWarnings("all")
            Object2IntMap o = (Object2IntMap) stats.get(statisticManager);
            o.clear();
            statisticManager.a(minecraftServer.getDataFixer(), json);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't load stats by nms!");
        }
    }
}

