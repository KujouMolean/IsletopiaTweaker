package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlayerDataDao;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PlayerSerializeUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class PlayerDataSync implements Listener {

    private final Map<String, String> passwdMap = new HashMap<>();

    public PlayerDataSync() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        // check table
        try {
            PlayerDataDao.checkTable();
        } catch (SQLException e) {
            e.printStackTrace();
            //stop server if check has error
            Logger.getAnonymousLogger().severe("Database check error!");
            Bukkit.shutdown();
        }

        // add shutdown task

        IsletopiaTweakers.addDisableTask("Save player data to database", () -> {
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
            byte[] serialize = PlayerSerializeUtils.serialize(player);

            if (!PlayerDataDao.update(player.getName(), serialize, passwdMap.get(player.getName()))) {
                throw new RuntimeException("Unexpected complete player data error!");
            }

            //update game mode
            RedisUtils.getCommand().set("GameMode:" + player.getName(), player.getGameMode().name());

        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.warn(player, "你的背包数据保存失败，请尽快联系管理员处理！");
        }
    }

    public void onLeft(Player player) {
        try {
            byte[] serialize = PlayerSerializeUtils.serialize(player);

            if (!PlayerDataDao.complete(player.getName(), serialize, passwdMap.get(player.getName()))) {
                throw new RuntimeException("Unexpected complete player data error!");
            }
            passwdMap.remove(player.getName());


            //store game mode
            RedisUtils.getCommand().set("GameMode:" + player.getName(), player.getGameMode().name());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void onJoin(Player player) {
        Location location = player.getLocation().clone();
        GameMode gameMode = player.getGameMode();
        try {
            if (!PlayerDataDao.exist(player.getName())) {
                //插入数据
                byte[] serialize = PlayerSerializeUtils.serialize(player);
                PlayerDataDao.insert(player.getName(), serialize);
            }

            player.setGameMode(GameMode.SPECTATOR);
            //拿锁
            String passwd = PlayerDataDao.getLock(player.getName());
            if (passwd != null) {
                loadData(player, passwd, gameMode, location);
                // end
            } else {
                //没拿到, 开始等锁
                Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), new Consumer<>() {
                    private int times = 0;

                    @Override
                    public void accept(BukkitTask task) {
                        try {
                            //尝试拿锁
                            String lock = PlayerDataDao.getLock(player.getName());

                            if (lock != null) {
                                loadData(player, lock, gameMode, location);
                                task.cancel();

                                //end
                                return;
                            }

                            times++;
                            if (times > 15) {
                                task.cancel();
                                //等待超时, 可能是上个服务器崩了, 强制拿锁
                                String lockForce = PlayerDataDao.getLockForce(player.getName());
                                if (lockForce == null) {
                                    //强制拿锁失败, 出大问题
                                    throw new RuntimeException("Unexpected error! Force get lock failed.");
                                    //end (failed)
                                }

                                loadData(player, lockForce, gameMode, location);


                                //end (success)
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                task.cancel();
                                player.setGameMode(GameMode.SURVIVAL);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            } finally {
                                player.kick(Component.text("#读取玩家数据出错，请联系管理员！"));
                            }
                        }
                    }
                }, 20, 20);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                player.setGameMode(GameMode.SURVIVAL);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                player.kick(Component.text("#读取玩家数据出错，请联系管理员！"));
            }
        }
    }

    private void loadData(Player player, String passwd, GameMode gameMode, Location location) throws SQLException, IOException {
        passwdMap.put(player.getName(), passwd);
        //强制拿到锁了, 加载数据

        byte[] query = PlayerDataDao.query(player.getName(), passwd);


        if (query == null) {
            throw new RuntimeException("Unexpected get player data failed!");
            //end (failed)
        }

        PlayerSerializeUtils.deserialize(player, query);
        //deserialize player from db
        player.teleport(location);
        player.setGameMode(gameMode);

        if (RedisUtils.getCommand().exists("GameMode:" + player.getName()) > 0) {

            String s = RedisUtils.getCommand().get("GameMode:" + player.getName());
            try {
                GameMode realGameMode = GameMode.valueOf(s);
                player.setGameMode(realGameMode);
            } catch (IllegalArgumentException ignored) {
            }
        }

        PlayerDataSyncCompleteEvent playerDataSyncCompleteEvent = new PlayerDataSyncCompleteEvent(player);
        Bukkit.getPluginManager().callEvent(playerDataSyncCompleteEvent);
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
}
