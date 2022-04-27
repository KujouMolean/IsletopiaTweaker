package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.shared.database.PlayerStatsDao;
import com.molean.isletopia.task.AsyncTryTask;
import com.molean.isletopia.task.ConditionalAsyncTask;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import com.molean.isletopia.utils.StatsSerializeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PlayerStatsSync implements Listener {

    private final Map<UUID, String> passwdMap = new ConcurrentHashMap<>();

    public PlayerStatsSync() {
        PluginUtils.registerEvents(this);


        Tasks.INSTANCE.addDisableTask("Save player stats to database", () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (passwdMap.containsKey(onlinePlayer.getUniqueId())) {
                    String stats = StatsSerializeUtils.getStats(onlinePlayer);
                    onLeft(onlinePlayer, stats);
                }
            }
        });

        // load data to current player
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onJoin(onlinePlayer);
        }

        //update one player data per second
        Queue<Player> queue = new ArrayDeque<>();
        Tasks.INSTANCE.intervalAsync(20, () -> {
            if (queue.isEmpty()) {
                queue.addAll(Bukkit.getOnlinePlayers());
                return;
            }

            Player player = queue.poll();

            if (player.isOnline() && passwdMap.containsKey(player.getUniqueId())) {
                Tasks.INSTANCE.sync(() -> update(player));
            }
        });

    }


    public void update(Player player) {
        try {
            String stats = StatsSerializeUtils.getStats(player);
            Tasks.INSTANCE.async(() -> {
                try {
                    if (!PlayerStatsDao.update(player.getUniqueId(), stats, passwdMap.get(player.getUniqueId()))) {
                        throw new RuntimeException("Unexpected complete player stats error!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    MessageUtils.warn(player, "player.stats.saveFailed");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.warn(player, "player.stats.saveFailed");
        }
    }

    public void onLeft(Player player, String stats) {
        try {
            if (!PlayerStatsDao.complete(player.getUniqueId(), stats, passwdMap.get(player.getUniqueId()))) {
                throw new RuntimeException("Unexpected complete player stats error!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        passwdMap.remove(player.getUniqueId());
    }

    public void waitLockThenLoadData(Player player) {
        Runnable exceptionHandler = () -> {
            MessageUtils.warn(player, "player.stats.loadFailed");
        };

        new AsyncTryTask(() -> {
            try {
                //尝试拿锁
                String lock = PlayerStatsDao.getLock(player.getUniqueId());
                if (lock != null) {
                    loadDataAsync(player, lock);
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                exceptionHandler.run();
                return true;
                //return true to end task
            }
        }, 20, 15).onFailed(() -> {
            try {
                String lockForce = PlayerStatsDao.getLockForce(player.getUniqueId());
                if (lockForce == null) {
                    //强制拿锁失败, 出大问题
                    throw new RuntimeException("Unexpected error! Force get lock failed.");
                    //end (failed)
                }
                loadDataAsync(player, lockForce);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                exceptionHandler.run();
            }
        }).run();
    }

    public void onJoin(Player player) {
        new ConditionalAsyncTask(() -> {
            try {
                return !PlayerStatsDao.exist(player.getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }).then(() -> {
            //插入数据
            new SyncThenAsyncTask<>(() -> StatsSerializeUtils.getStats(player), stats -> {
                try {
                    PlayerStatsDao.insert(player.getUniqueId(), stats);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }).then(() -> {
                waitLockThenLoadData(player);
            }).run();
        }).orElse(() -> {
            waitLockThenLoadData(player);
        }).run();
    }

    private void loadDataAsync(Player player, String passwd) throws SQLException, IOException {
        passwdMap.put(player.getUniqueId(), passwd);
        //强制拿到锁了, 加载数据
        String stats = PlayerStatsDao.query(player.getUniqueId(), passwd);
        if (stats == null) {
            throw new RuntimeException("Unexpected get player data failed!");
            //end (failed)
        }
        Tasks.INSTANCE.sync(() -> StatsSerializeUtils.loadStats(player, stats));
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDataSyncCompleteEvent event) {
        onJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerQuitEvent event) {
        if (passwdMap.containsKey(event.getPlayer().getUniqueId())) {
            String stats = StatsSerializeUtils.getStats(event.getPlayer());
            Tasks.INSTANCE.async(() -> onLeft(event.getPlayer(), stats));
        }
    }


}

