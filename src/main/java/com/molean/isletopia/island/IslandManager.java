package com.molean.isletopia.island;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.database.IslandDao;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.model.IslandId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public enum IslandManager {
    INSTANCE;

    private final Map<IslandId, LocalIsland> islandSet = new ConcurrentHashMap<>();
    private final Map<IslandId, Long> lastQuery = new ConcurrentHashMap<>();


    IslandManager() {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            HashMap<IslandId, LocalIsland> islandIdIslandHashMap = new HashMap<>(islandSet);
            for (IslandId islandId : islandIdIslandHashMap.keySet()) {
                Long l = lastQuery.get(islandId);
                if (l == null || System.currentTimeMillis() - l > 60 * 1000) {
                    lastQuery.remove(islandId);
                    islandSet.remove(islandId);
                }
            }
            for (IslandId islandId : islandSet.keySet()) {
                reloadIsland(islandId);
            }
        }, new Random().nextInt(200), 20 * 60);
        IsletopiaTweakers.addDisableTask("Stop update island data..", bukkitTask::cancel);
    }

    private void reloadIsland(IslandId islandId) {
        try {
            Island islandByIslandId = IslandDao.getIslandByIslandId(islandId);
            if (islandByIslandId != null) {
                islandSet.put(islandByIslandId.getIslandId(), new LocalIsland(islandByIslandId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public LocalIsland getIsland(IslandId islandId) {
        if (!islandSet.containsKey(islandId)) {
            try {
                Island islandByIslandId = IslandDao.getIslandByIslandId(islandId);
                if (islandByIslandId != null) {
                    islandSet.put(islandByIslandId.getIslandId(), new LocalIsland(islandByIslandId));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (islandSet.containsKey(islandId)) {
            lastQuery.put(islandId, System.currentTimeMillis());
        }
        return islandSet.get(islandId);
    }


    public void persist(LocalIsland island) {
        try {
            IslandDao.updateIsland(island);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private final Random RANDOM = new Random();


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

    public LocalIsland createNewIsland( UUID uuid, Consumer<LocalIsland> consumer) {
        IslandId nextIslandId = getNextIslandId(ServerInfoUpdater.getServerName());
        return createNewIsland(nextIslandId, uuid, consumer);

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


    public List<LocalIsland> getPlayerLocalServerIslands(UUID player) {
        List<IslandId> localServerIslandIds;
        try {
            localServerIslandIds = IslandDao.getPlayerLocalServerIslands(ServerInfoUpdater.getServerName(), player);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error");
        }
        List<LocalIsland> islands = new ArrayList<>();
        for (IslandId localServerIslandId : localServerIslandIds) {
            islands.add(getIsland(localServerIslandId));
        }
        return islands;
    }


    public LocalIsland getPlayerLocalServerFirstIsland(UUID player) {
        IslandId playerFirstIsland = null;
        try {
            playerFirstIsland = IslandDao.getPlayerLocalServerFirstIsland(ServerInfoUpdater.getServerName(), player);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error");
        }
        return getIsland(playerFirstIsland);
    }

    @Nullable
    public LocalIsland getPlayerFirstIsland(UUID player) {
        IslandId playerFirstIsland = null;
        try {
            playerFirstIsland = IslandDao.getPlayerFirstIsland(player);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error");
        }
        return getIsland(playerFirstIsland);
    }

    public List<LocalIsland> getPlayerIslands(UUID uuid) {
        List<IslandId> playerIslandIds = null;
        try {
            playerIslandIds = IslandDao.getPlayerIslandIds(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error!");
        }
        List<LocalIsland> islands = new ArrayList<>();
        for (IslandId playerIslandId : playerIslandIds) {
            islands.add(getIsland(playerIslandId));
        }
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
//        tobeUpdate.add(island);
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

    public @NotNull LocalIsland createNewIsland(IslandId islandId, UUID uuid, Consumer<LocalIsland> runnable) {

        if (!islandId.getServer().equals(ServerInfoUpdater.getServerName())) {
            throw new RuntimeException("Can't create an island from other server!");
        }

        if (exists(islandId)) {
            throw new RuntimeException("Island already exist!");
        }

        LocalIsland temp = new LocalIsland(0, islandId.getX(), islandId.getZ(),
                256, 128, 256, 0f, 0f,
                ServerInfoUpdater.getServerName(), uuid, null, Biome.PLAINS.name(),
                new Timestamp(System.currentTimeMillis()),
                new HashSet<>(),
                new HashSet<>());

        try {
            IslandDao.createIsland(temp);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LocalIsland islandByIslandId = null;
        islandByIslandId = getIsland(islandId);

        if (islandByIslandId == null) {
            throw new RuntimeException("Unexpected error, island creation is failed");
        }

        LocalIsland finalIslandByIslandId = islandByIslandId;
        islandByIslandId.applyIsland(() -> {
            if (runnable != null) {
                runnable.accept(finalIslandByIslandId);
            }
        });

        return islandByIslandId;
    }


    @Nullable
    public LocalIsland getCurrentIsland(Player player) {
        return getCurrentIsland(player.getLocation());
    }

    @Nullable
    public LocalIsland getCurrentIsland(Location location) {
        IslandId islandId = IslandId.fromLocation(ServerInfoUpdater.getServerName(), location.getBlockX(), location.getBlockZ());
        return getIsland(islandId);
    }
}
