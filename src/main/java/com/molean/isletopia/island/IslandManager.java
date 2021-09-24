package com.molean.isletopia.island;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.IslandDao;
import com.molean.isletopia.island.obj.CuboidRegion;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public enum IslandManager {
    INSTANCE;

    private final Map<IslandId, Island> islandSet = new ConcurrentHashMap<>();

    private final Set<Island> tobeUpdate = new HashSet<>();

    IslandManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Island island : tobeUpdate) {
                persist(island);
            }
            tobeUpdate.clear();

        }, 20, 20);
    }

    public void persist(Island island) {
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

    public Island createNewIsland(String owner, Consumer<Island> consumer) {
        IslandId nextIslandId = getNextIslandId(ServerInfoUpdater.getServerName());
        return createNewIsland(nextIslandId, owner, consumer);

    }

    public void deleteIsland(@NotNull Island island,@Nullable Runnable runnable) {
        island.persist();

        island.clear(() -> {
            try {
                IslandDao.delete(island.getId());
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Unexpected database error!");
            }
            tobeUpdate.remove(island);
            islandSet.remove(island.getIslandId());
            if (runnable != null) {
                runnable.run();
            }
        }, 60);
    }


    public List<Island> getPlayerLocalServerIslands(String player) {
        List<IslandId> localServerIslandIds;
        try {
            localServerIslandIds = IslandDao.getPlayerLocalServerIslands(player);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error");
        }
        List<Island> islands = new ArrayList<>();
        for (IslandId localServerIslandId : localServerIslandIds) {
            islands.add(getIsland(localServerIslandId));
        }
        return islands;
    }


    public Island getPlayerLocalServerFirstIsland(String player) {
        IslandId playerFirstIsland = null;
        try {
            playerFirstIsland = IslandDao.getPlayerLocalServerFirstIsland(player);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error");
        }
        return getIsland(playerFirstIsland);
    }

    @Nullable
    public Island getPlayerFirstIsland(String player) {
        IslandId playerFirstIsland = null;
        try {
            playerFirstIsland = IslandDao.getPlayerFirstIsland(player);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error");
        }
        return getIsland(playerFirstIsland);
    }

    public List<Island> getPlayerIslands(String player) {
        List<IslandId> playerIslandIds = null;
        try {
            playerIslandIds = IslandDao.getPlayerIslandIds(player);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected database error!");
        }
        List<Island> islands = new ArrayList<>();
        for (IslandId playerIslandId : playerIslandIds) {
            islands.add(getIsland(playerIslandId));
        }
        return islands;
    }

    public int getPlayerIslandCount(String player) {
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

    public void update(Island island) {
        tobeUpdate.add(island);
    }

    public boolean hasCurrentIslandPermission(Player player) {
        Island currentIsland = getCurrentIsland(player);
        return currentIsland != null && currentIsland.hasPermission(player);
    }

    public boolean hasTargetIslandPermission(Player player, Location location) {
        Island currentIsland = getCurrentIsland(location);
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

    public @NotNull Island createNewIsland(IslandId islandId, String owner, Consumer<Island> runnable) {

        if (!islandId.getServer().equals(ServerInfoUpdater.getServerName())) {
            throw new RuntimeException("Can't create an island from other server!");
        }

        if (exists(islandId)) {
            throw new RuntimeException("Island already exist!");
        }

        Island temp = new Island(0, islandId.getX(), islandId.getZ(),
                256, 128, 256, 0f, 0f,
                ServerInfoUpdater.getServerName(), owner, null, Biome.PLAINS,
                new Timestamp(System.currentTimeMillis()),
                new HashSet<>(),
                new HashSet<>());

        try {
            IslandDao.createIsland(temp);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Island islandByIslandId = null;
        try {
            islandByIslandId = IslandDao.getIslandByIslandId(islandId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (islandByIslandId == null) {
            throw new RuntimeException("Unexpected error, island creation is failed");
        }

        Island finalIslandByIslandId = islandByIslandId;
        islandByIslandId.applyIsland(() -> {
            if (runnable != null) {
                runnable.accept(finalIslandByIslandId);
            }
        });
        return islandByIslandId;
    }


    @Nullable
    public Island getIsland(IslandId islandId) {
        if (!islandSet.containsKey(islandId)) {
            try {
                Island islandByIslandId = IslandDao.getIslandByIslandId(islandId);
                if (islandByIslandId != null) {
                    islandSet.put(islandByIslandId.getIslandId(), islandByIslandId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return islandSet.get(islandId);
    }

    @Nullable
    public Island getCurrentIsland(Player player) {
        return getCurrentIsland(player.getLocation());
    }

    @Nullable
    public Island getCurrentIsland(Location location) {
        IslandId islandId = IslandId.fromLocation(location.getBlockX(), location.getBlockZ());
        return getIsland(islandId);
    }
}
