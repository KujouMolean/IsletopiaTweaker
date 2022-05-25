package com.molean.isletopia.island;

import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.shared.database.IslandDao;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public enum IslandManager {
    INSTANCE;

    private final Map<IslandId, LocalIsland> islandSet = new ConcurrentHashMap<>();
    private final Set<IslandId> tobePersist = new CopyOnWriteArraySet<>();
    private final Set<IslandId> tobeQuery = new CopyOnWriteArraySet<>();

    IslandManager() {
        Tasks.INSTANCE.intervalAsync(50, this::persist);
        Tasks.INSTANCE.intervalAsync(50, this::query);
    }


    private void query() {
        HashSet<IslandId> islandIds = new HashSet<>(tobeQuery);
        for (IslandId islandId : islandIds) {
            queryIsland(islandId);
            tobeQuery.remove(islandId);
        }
    }

    @Nullable
    @Deprecated
    public LocalIsland getLocalIsland(IslandId islandId) {
        if (!islandId.getServer().equals(ServerInfoUpdater.getServerName())) {
            throw new RuntimeException("island not in current server");
        }
        if (!islandSet.containsKey(islandId)) {
            tobeQuery.add(islandId);
            return null;
        }
        return islandSet.get(islandId);
    }

    @Nullable
    public LocalIsland getLocalIslandIfLoaded(IslandId islandId) {
        if (!islandId.getServer().equals(ServerInfoUpdater.getServerName())) {
            throw new RuntimeException("island not in current server");
        }
        if (!islandSet.containsKey(islandId)) {
            tobeQuery.add(islandId);
            return null;
        }
        return islandSet.get(islandId);
    }


    public LocalIsland getLocalIslandSync(IslandId islandId) {
        if (!islandId.getServer().equals(ServerInfoUpdater.getServerName())) {
            throw new RuntimeException("island not in current server");
        }
        if (!islandSet.containsKey(islandId)) {
            LocalIsland localIsland = queryIsland(islandId);
            islandSet.put(islandId, localIsland);
            return localIsland;
        }
        return islandSet.get(islandId);
    }

    public void getLocalIsland(IslandId islandId, Consumer<LocalIsland> consumer) {
        if (!islandId.getServer().equals(ServerInfoUpdater.getServerName())) {
            throw new RuntimeException("island not in current server");
        }
        if (!islandSet.containsKey(islandId)) {
            Tasks.INSTANCE.async(() -> {
                LocalIsland localIsland = queryIsland(islandId);
                consumer.accept(localIsland);
            });
        } else {
            consumer.accept(islandSet.get(islandId));

        }
    }

    @Nullable
    private LocalIsland queryIsland(IslandId islandId) {
        try {
            PluginUtils.getLogger().info("Query local island " + islandId);
            Island islandByIslandId = IslandDao.getIslandByIslandId(islandId);
            if (islandByIslandId != null) {
                LocalIsland localIsland = new LocalIsland(islandByIslandId);
                islandSet.put(islandByIslandId.getIslandId(), localIsland);
                PluginUtils.getLogger().info(islandId + " landing successfully!");
                Tasks.INSTANCE.sync(() -> {
                    for (Player player : localIsland.getPlayersInIsland()) {
                        PlayerIslandChangeEvent playerIslandChangeEvent = new PlayerIslandChangeEvent(player, null, localIsland);
                        PluginUtils.callEvent(playerIslandChangeEvent);
                    }
                });
            } else {
                islandSet.put(islandId, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return islandSet.get(islandId);
    }

    @Nullable
    public Island getIsland(IslandId islandId) {
        try {
            return IslandDao.getIslandByIslandId(islandId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Island getIsland(int id) {
        try {
            return IslandDao.getIslandById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void persist() {
        ArrayList<IslandId> islandIds = new ArrayList<>(tobePersist);
        for (IslandId islandId : islandIds) {
            try {
                IslandDao.updateIsland(islandSet.get(islandId));
                tobePersist.remove(islandId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void persist(LocalIsland island) {
        tobePersist.add(island.getIslandId());
    }


    private static final Random RANDOM = new Random();


    @NotNull
    public IslandId getNextIslandId(String server) {
        try {
            for (int i = 5; true; i++) {
                int x = RANDOM.nextInt(i);
                int z = RANDOM.nextInt(i);
                IslandId islandId = new IslandId(server, x, z);
                Island island = IslandDao.getIslandByIslandId(islandId);
                if (island == null) {
                    return islandId;
                }

                if (i > 1000) {
                    throw new RuntimeException("Can't find a valid IslandId");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error!");
        }
    }

    public void createNewIsland(UUID uuid, Consumer<LocalIsland> consumer) {
        IslandId nextIslandId = getNextIslandId(ServerInfoUpdater.getServerName());
        createNewIsland(nextIslandId, uuid, consumer);

    }

    public void deleteIsland(@NotNull LocalIsland island, @Nullable Runnable runnable) {
        island.persist();
        island.clear(() -> {
            try {
                IslandDao.delete(island.getId());
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Unexpected database error!");
            }
            islandSet.remove(island.getIslandId());
            if (runnable != null) {
                runnable.run();
            }
        }, 60);
    }


    @Nullable
    public Island getPlayerFirstIsland(UUID player) {
        IslandId playerFirstIsland = null;
        try {
            playerFirstIsland = IslandDao.getPlayerFirstIsland(player);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error");
        }
        return getIsland(playerFirstIsland);
    }


    public List<Island> getPlayerIslands(UUID uuid) {
        List<IslandId> playerIslandIds;
        try {
            playerIslandIds = IslandDao.getPlayerIslandIds(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error!");
        }
        List<Island> islands = new ArrayList<>();
        for (IslandId playerIslandId : playerIslandIds) {
            islands.add(getIsland(playerIslandId));
        }
        islands.sort(Comparator.comparingInt(Island::getId));
        return islands;
    }

    public int getPlayerIslandCount(UUID player) {
        try {

            Integer integer = IslandDao.countIslandByPlayer(player);
            if (integer == null) {
                throw new RuntimeException();
            }
            return integer;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error!");
        }
    }

    public void update(LocalIsland island) {
        persist(island);
    }

    public boolean hasCurrentIslandPermission(Player player) {
        LocalIsland currentIsland = getCurrentIsland(player);
        return currentIsland != null && currentIsland.hasPermission(player);
    }

    public boolean hasTargetIslandPermission(Player player, Location location) {
        LocalIsland currentIsland = getCurrentIsland(location);
        return currentIsland != null && currentIsland.hasPermission(player);
    }


    public boolean exists(IslandId islandId) {
        try {
            if (IslandDao.getIslandByIslandId(islandId) != null) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void createNewIsland(IslandId islandId, UUID uuid, Consumer<LocalIsland> runnable) {

        if (!islandId.getServer().equals(ServerInfoUpdater.getServerName())) {
            throw new RuntimeException("Can't create an island from other server!");
        }

        if (exists(islandId)) {
            throw new RuntimeException("Island already exist!");
        }

        LocalIsland temp = new LocalIsland(0, islandId.getX(), islandId.getZ(), "SkyWorld",
                256, 128, 256, 0f, 0f,
                ServerInfoUpdater.getServerName(), uuid, null,
                new Timestamp(System.currentTimeMillis()),
                new HashSet<>(),
                new HashSet<>(), "GRASS_BLOCK");

        try {
            IslandDao.createIsland(temp);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getLocalIsland(islandId, localIsland -> {
            if (localIsland == null) {
                throw new RuntimeException("Unexpected error, island creation is failed");
            }

            localIsland.applyIsland(() -> {
                if (runnable != null) {
                    runnable.accept(localIsland);
                }
            });

        });
    }


    @Nullable
    public LocalIsland getCurrentIsland(Player player) {
        return getCurrentIsland(player.getLocation());
    }

    @Nullable
    public LocalIsland getCurrentIsland(Chunk chunk) {
        Location location = chunk.getBlock(0, 0, 0).getLocation();
        return getCurrentIsland(location);
    }

    @Nullable
    @Deprecated
    public LocalIsland getCurrentIsland(Location location) {
        IslandId islandId = IslandId.fromLocation(ServerInfoUpdater.getServerName(), location.getBlockX(), location.getBlockZ());
        return getLocalIsland(islandId);
    }

    @Nullable
    public LocalIsland getCurrentIslandIfLoaded(Location location) {
        IslandId islandId = IslandId.fromLocation(ServerInfoUpdater.getServerName(), location.getBlockX(), location.getBlockZ());
        return getLocalIsland(islandId);
    }
}
