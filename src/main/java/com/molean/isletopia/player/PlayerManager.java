package com.molean.isletopia.player;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.distribute.DataLoadTask;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.BukkitPlayerUtils;
import com.molean.isletopia.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.function.Consumer;

@Singleton
public class PlayerManager implements Listener {

    public enum LoginStatus {
        NOT_FOUND, REQUEST_ACCEPTED, BUKKIT_LOGGED, DATA_LOAD_REQUESTED, DATA_LOAD_COMPLETE, DATA_RESTORE_COMPLETE, LOGGED_IN,
        BAD
    }

    private final Map<UUID, LoginStatus> loginStatusMap = new HashMap<>();
    private final Map<UUID, Integer> loadedDataCount = new HashMap<>();
    private final Map<UUID, UUID> sessionMap = new HashMap<>();
    private final Map<UUID, Location> playerLocation = new HashMap<>();
    private final List<DataLoadTask<?>> dataLoadTasks = new ArrayList<>();

    private final List<Consumer<Player>> quitUpdateTasks = new ArrayList<>();

    public void registerQuitUpdateTask(Consumer<Player> consumer) {
        this.quitUpdateTasks.add(consumer);
    }

    public void validate(Player player) {
        if (!getLoginStatus(player).equals(LoginStatus.LOGGED_IN)) {
            throw new RuntimeException("Player is not logged in!");
        }
    }

    public boolean isLogged(Player player) {
        return getLoginStatus(player).equals(LoginStatus.LOGGED_IN);

    }

    public Set<Player> getLoggedPlayers() {
        HashSet<Player> players = new HashSet<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (getLoginStatus(onlinePlayer).equals(LoginStatus.LOGGED_IN)) {
                players.add(onlinePlayer);
            }
        }
        return players;
    }

    public void registerIntervalUpdateTask(Consumer<Player> consumer, int interval) {
        Tasks.INSTANCE.interval(interval, () -> {
            for (Player loggedPlayer : getLoggedPlayers()) {
                consumer.accept(loggedPlayer);
            }
        });
    }

    public void registerRoundUpdateTask(Consumer<Player> consumer, int interval) {
        Queue<UUID> queue = new ArrayDeque<>();
        Tasks.INSTANCE.interval(interval, () -> {
            if (queue.isEmpty()) {
                for (Player loggedPlayer : getLoggedPlayers()) {
                    queue.add(loggedPlayer.getUniqueId());
                }
                return;
            }
            UUID uuid = queue.poll();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline() && getLoginStatus(player).equals(LoginStatus.LOGGED_IN)) {
                consumer.accept(player);
            }
        });
    }


    public PlayerManager() {
        Tasks.INSTANCE.timeout(20, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                requestLogin(onlinePlayer.getUniqueId());
                bukkitJoin(onlinePlayer);
            }
        });
        Tasks.INSTANCE.addDisableTask("Player quit task..",() -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                quit(onlinePlayer);

            }
        });
    }

    public LoginStatus getLoginStatus(Player player) {
        return loginStatusMap.getOrDefault(player.getUniqueId(), LoginStatus.NOT_FOUND);
    }

    public void updateLoginStatus(Player player, LoginStatus loginStatus) {
        if (getLoginStatus(player).equals(LoginStatus.BAD)) {
            BukkitPlayerUtils.kickAsync(player, "#Login bad status, contact server admin!");
            throw new RuntimeException("Login bad, can't update login status, try terminate.");
        }
        if (getLoginStatus(player).equals(LoginStatus.NOT_FOUND) && loginStatus.equals(LoginStatus.BAD)) {
            return;
        }
        loginStatusMap.put(player.getUniqueId(), loginStatus);
    }

    public boolean requestLogin(UUID uuid) {
        PluginUtils.getLogger().info("UUID of " + uuid.toString() + " request login.");
        if (loginStatusMap.getOrDefault(uuid, LoginStatus.NOT_FOUND).equals(LoginStatus.NOT_FOUND)) {
            loginStatusMap.put(uuid, LoginStatus.REQUEST_ACCEPTED);
            PluginUtils.getLogger().info("UUID of " + uuid + " request accepted!");
            sessionMap.put(uuid, UUID.randomUUID());
            return true;
        }
        PluginUtils.getLogger().info("UUID of " + uuid + " request refused!");
        return false;
    }


    @EventHandler
    public void on(PlayerJoinEvent event) {
        requestLogin(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();
        bukkitJoin(player);
    }

    public void bukkitJoin(Player player) {
        LoginStatus loginStatus = loginStatusMap.get(player.getUniqueId());
        if (loginStatus == LoginStatus.NOT_FOUND) {
            player.kick(Component.text("#Player Not Found"));
            PluginUtils.getLogger().warning("Player " + player.getName() + " was not found, reject login.");
            return;
        }
        if (loginStatus != LoginStatus.REQUEST_ACCEPTED) {
            player.kick(Component.text("#Unexpected login status"));
            PluginUtils.getLogger().warning("Player " + player.getName() + " has an unexpected login status, reject it.");
            return;
        }
        player.setGameMode(GameMode.SPECTATOR);
        playerLocation.put(player.getUniqueId(), player.getLocation());
        loginStatusMap.put(player.getUniqueId(), LoginStatus.BUKKIT_LOGGED);
        dataLoadRequest(player);
    }


    public void quit(Player player) {
        sessionMap.remove(player.getUniqueId());

        if (getLoginStatus(player).equals(LoginStatus.LOGGED_IN)) {
            PluginUtils.getLogger().info("Running quit task for " + player.getName() + " ...");
            for (Consumer<Player> quitUpdateTask : quitUpdateTasks) {
                quitUpdateTask.accept(player);
            }
        }
        PluginUtils.getLogger().info(player.getName() + " quit server complete!");
        updateLoginStatus(player, LoginStatus.NOT_FOUND);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        quit(player);

    }

    public void dataLoadRequest(Player player) {
        loadedDataCount.put(player.getUniqueId(), 0);

        for (DataLoadTask<?> dataLoadTask : dataLoadTasks) {
            dataLoadTask.load(player, (result, exception) -> {
                if (!result) {
                    loginStatusMap.put(player.getUniqueId(), LoginStatus.BAD);
                    PluginUtils.getLogger().warning("Player " + player.getName() + " login failed during data loading!");
                    updateLoginStatus(player, LoginStatus.BAD);
                    exception.printStackTrace();
                }
                loadedDataCount.put(player.getUniqueId(), loadedDataCount.getOrDefault(player.getUniqueId(), 0) + 1);
                PluginUtils.getLogger().info("Player " + player.getName() + " loading data... " + "(" + loadedDataCount.getOrDefault(player.getUniqueId(), 0) + "/" + dataLoadTasks.size() + ")");
                PluginUtils.getLogger().info(player.getName() + "'s " + dataLoadTask.getName() + " loading task complete.");
                if (loadedDataCount.getOrDefault(player.getUniqueId(), 0) == dataLoadTasks.size()) {
                    if (getLoginStatus(player).equals(LoginStatus.BAD)) {
                        BukkitPlayerUtils.kickAsync(player, "#Login bad status, contact server admin!");
                        PluginUtils.getLogger().warning(player.getName() + " data load stage was failed.");
                        return;
                    }
                    dataLoadedComplete(player);
                }

            });
        }
        updateLoginStatus(player, LoginStatus.DATA_LOAD_REQUESTED);
        PluginUtils.getLogger().info("Player " + player.getName() + " start data load.");

    }

    public void dataLoadedComplete(Player player) {
        updateLoginStatus(player, LoginStatus.DATA_LOAD_COMPLETE);
        PluginUtils.getLogger().info("Player " + player.getName() + " data-load complete.");
        restorePlayer(player);
    }

    public void restorePlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(playerLocation.get(player.getUniqueId()));
        updateLoginStatus(player, LoginStatus.DATA_RESTORE_COMPLETE);
        PluginUtils.getLogger().info("Player " + player.getName() + " restore complete.");
        updateLoginStatus(player, LoginStatus.LOGGED_IN);
        PluginUtils.getLogger().info("Player " + player.getName() + " logged in successfully.");
        PlayerLoggedEvent playerLoggedEvent = new PlayerLoggedEvent(player);
        PluginUtils.callEvent(playerLoggedEvent);

    }

    public <T> void registerDataLoading(DataLoadTask<T> dataLoadTask) {
        dataLoadTasks.add(dataLoadTask);
    }
}
