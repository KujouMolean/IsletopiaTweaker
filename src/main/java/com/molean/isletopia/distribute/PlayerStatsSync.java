package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.shared.database.PlayerStatsDao;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.StatsSerializeUtils;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PlayerStatsSync implements Listener {

    private final Map<UUID, String> passwdMap = new ConcurrentHashMap<>();


    private final PlayerManager playerManager;

    public PlayerStatsSync(PlayerManager playerManager) {
        this.playerManager = playerManager;

        DataLoadTask<String> stringDataLoadTask = new DataLoadTask<>("Statistics");
        stringDataLoadTask.setAsyncLoad((player, consumer) -> {
            try {
                if (!PlayerStatsDao.exist(player.getUniqueId())) {
                    new SyncThenAsyncTask<>(() -> StatsSerializeUtils.getStats(player), stats -> {
                        try {
                            PlayerStatsDao.insert(player.getUniqueId(), stats);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }).run();
                }

                String lock = null;
                for (int i = 0; i < 15; i++) {
                    //尝试拿锁
                    lock = PlayerStatsDao.getLock(player.getUniqueId());
                    if (lock != null) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (lock == null) {
                    lock = PlayerStatsDao.getLockForce(player.getUniqueId());
                }
                if (lock == null) {
                    throw new RuntimeException("Unexpected error! Force get lock failed.");
                }
                passwdMap.put(player.getUniqueId(), lock);
                String stats = PlayerStatsDao.query(player.getUniqueId(), lock);
                if (stats == null) {
                    throw new RuntimeException("Unexpected get player data failed!");
                }
                consumer.accept(stats);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        stringDataLoadTask.setSyncRestore((player, s, consumer) -> {
            StatsSerializeUtils.loadStats(player, s);
            consumer.accept(player);
        });
        playerManager.registerDataLoading(stringDataLoadTask);
        playerManager.registerRoundUpdateTask(player -> {
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
        }, 20);
        playerManager.registerQuitUpdateTask(player -> {
            if (passwdMap.containsKey(player.getUniqueId())) {
                String passwd = passwdMap.get(player.getUniqueId());
                passwdMap.remove(player.getUniqueId());
                String stats = StatsSerializeUtils.getStats(player);
                Tasks.INSTANCE.async(() -> {
                    try {
                        if (!PlayerStatsDao.complete(player.getUniqueId(), stats, passwd)) {
                            throw new RuntimeException("Unexpected complete player stats error!");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }


}

