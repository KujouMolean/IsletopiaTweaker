package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.database.PlayerStatsDao;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.StatsSerializeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class PlayerStatsSync implements Listener {

    private final Map<UUID, String> passwdMap = new HashMap<>();

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
                if (passwdMap.containsKey(onlinePlayer.getUniqueId())) {
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

            if (player.isOnline() && passwdMap.containsKey(player.getUniqueId())) {

                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                        update(player));

            }
        }, 20, 20);

    }


    public void update(Player player) {
        try {
            String stats = StatsSerializeUtils.getStats(player);
            if (!PlayerStatsDao.update(player.getUniqueId(), stats, passwdMap.get(player.getUniqueId()))) {
                throw new RuntimeException("Unexpected complete player stats error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.warn(player, "你的统计保存失败，请尽快联系管理员处理！");
        }
    }

    public void onLeft(Player player) {
        try {
            String stats = StatsSerializeUtils.getStats(player);
            if (!PlayerStatsDao.complete(player.getUniqueId(), stats, passwdMap.get(player.getUniqueId()))) {
                throw new RuntimeException("Unexpected complete player stats error!");
            }
            passwdMap.remove(player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onJoin(Player player) {
        try {
            if (!PlayerStatsDao.exist(player.getUniqueId())) {
                //插入数据
                String stats = StatsSerializeUtils.getStats(player);
                PlayerStatsDao.insert(player.getUniqueId(), stats);
            }
            //拿锁
            String passwd = PlayerStatsDao.getLock(player.getUniqueId());
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
                            String lock = PlayerStatsDao.getLock(player.getUniqueId());
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
                                String lockForce = PlayerStatsDao.getLockForce(player.getUniqueId());
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
        passwdMap.put(player.getUniqueId(), passwd);
        //强制拿到锁了, 加载数据

        String stats = PlayerStatsDao.query(player.getUniqueId(), passwd);

        if (stats == null) {
            throw new RuntimeException("Unexpected get player data failed!");
            //end (failed)
        }
        StatsSerializeUtils.loadStats(player, stats);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDataSyncCompleteEvent event) {
        onJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerQuitEvent event) {
        if (passwdMap.containsKey(event.getPlayer().getUniqueId())) {
            onLeft(event.getPlayer());
        }
    }




}

