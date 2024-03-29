package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.database.PlayerDataDao;
import com.molean.isletopia.utils.BukkitPlayerUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PlayerSerializeUtils;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class PlayerDataSync implements Listener {

    private final Map<UUID, String> passwdMap = new HashMap<>();

    public PlayerDataSync(PlayerManager playerManager) {


        DataLoadTask<byte[]> dataLoadTask = new DataLoadTask<>("Data");
        dataLoadTask.setAsyncLoad((player, consumer) -> {
            try {
                if (!PlayerDataDao.exist(player.getUniqueId())) {
                    PluginUtils.getLogger().info("Prepare first join for " + player.getName() + "...");

                    PlayerSerializeUtils.serialize(player, bytes -> {
                        try {
                            PlayerDataDao.insert(player.getUniqueId(), bytes);
                            PluginUtils.getLogger().info("Prepare first join for " + player.getName() + " successfully.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }

                String lock = null;
                for (int i = 0; i < 15; i++) {
                    //尝试拿锁

                    lock = PlayerDataDao.getLock(player.getUniqueId());
                    if (lock != null) {
                        PluginUtils.getLogger().info("Got player data lock for " + player.getName() + " successfully.");
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                if (lock == null) {
                    PluginUtils.getLogger().warning("Got player data lock for " + player.getName() + " failed, force try...");
                    lock = PlayerDataDao.getLockForce(player.getUniqueId());

                }
                if (lock == null) {
                    BukkitPlayerUtils.kickAsync(player, "#Error when loading your data, please contact server administrator！");
                    throw new RuntimeException("Unexpected get player data failed!");
                }
                passwdMap.put(player.getUniqueId(), lock);
                byte[] query = PlayerDataDao.query(player.getUniqueId(), lock);
                consumer.accept(query);

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        dataLoadTask.setSyncRestore((player, bytes, consumer) -> {
            PluginUtils.getLogger().info("Deserialize player data for " + player.getName() + " ...");
            PlayerSerializeUtils.deserialize(player, bytes, () -> {
                PluginUtils.getLogger().info("Deserialize player data for " + player.getName() + " successfully!");
                consumer.accept(player);
            });
        });
        playerManager.registerDataLoading(dataLoadTask);
        playerManager.registerRoundUpdateTask(player -> {
            PlayerSerializeUtils.serialize(player, bytes -> {
                if (bytes == null) {
                    MessageUtils.warn(player, "player.data.saveFailed");
                    return;
                }
                try {
                    if (!PlayerDataDao.update(player.getUniqueId(), bytes, passwdMap.get(player.getUniqueId()))) {
                        throw new RuntimeException("Unexpected complete player data error!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    MessageUtils.warn(player, "player.data.saveFailed");
                }
            });

        }, 20);
        playerManager.registerQuitUpdateTask(player -> {
            if (passwdMap.containsKey(player.getUniqueId())) {
                String passwd = passwdMap.get(player.getUniqueId());
                passwdMap.remove(player.getUniqueId());
                PluginUtils.getLogger().info("Serialize player data for " + player.getName() + "...");
                PlayerSerializeUtils.serialize(player, bytes -> {
                    try {
                        if (!PlayerDataDao.complete(player.getUniqueId(), bytes, passwd)) {
                            throw new RuntimeException("Unexpected complete player data error!");
                        }
                        PluginUtils.getLogger().info("Serialize for " + player.getName() + " successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

        });
    }
}
