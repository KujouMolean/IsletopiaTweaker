package com.molean.isletopia.charge;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandId;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Conduit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;

public class PlayerConsumeListener implements Listener {
    private static int TICK_SAMPLE_10 = 10;
    private static final Random random = new Random();
    private static final Map<IslandId, String> plotOwnerCache = new HashMap<>();
    private static boolean shouldRecord = true;

    public void producePowerAndWater() {
        @NotNull Chunk[] loadedChunks = IsletopiaTweakers.getWorld().getLoadedChunks();
        for (Chunk loadedChunk : loadedChunks) {
            Arrays.stream(loadedChunk.getTileEntities(false))
                    .filter(blockState -> blockState.getType().equals(Material.BEACON))
                    .map(blockState -> (Beacon) blockState)
                    .forEach(beacon -> {
                        if (beacon.getTier() > 0) {
                            String s = Objects.requireNonNull(IslandManager.INSTANCE.getCurrentIsland(beacon.getLocation())).getOwner();
                            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(s);
                            playerChargeDetail.setPowerProduceTimes(playerChargeDetail.getPowerProduceTimes() + 1);
                        }
                    });
            Arrays.stream(loadedChunk.getTileEntities(false))
                    .filter(blockState -> blockState.getType().equals(Material.CONDUIT))
                    .map(blockState -> (Conduit) blockState)
                    .forEach(conduit -> {
                        String s = Objects.requireNonNull(IslandManager.INSTANCE.getCurrentIsland(conduit.getLocation())).getOwner();
                        PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(s);
                        playerChargeDetail.setWaterProduceTimes(playerChargeDetail.getWaterProduceTimes() + 1);
                    });
        }
    }

    public void arrearsDetect(String owner) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Island playerLocalServerFirstIsland = IslandManager.INSTANCE.getPlayerLocalServerFirstIsland(owner);
            if (PlayerChargeDetailUtils.getLeftPower(PlayerChargeDetailCommitter.get(owner)) < 0) {
                playerLocalServerFirstIsland.addIslandFlag("DisableRedstone");
                for (Player player : playerLocalServerFirstIsland.getPlayersInIsland()) {
                    if (playerLocalServerFirstIsland.hasPermission(player)) {
                        MessageUtils.warn(player, "当前岛屿已停电，请即使缴纳电费。");
                    }
                }
            } else {
                if (playerLocalServerFirstIsland.containsFlag("DisableRedstone")) {
                    playerLocalServerFirstIsland.removeIslandFlag("DisableRedstone");
                    for (Player player : playerLocalServerFirstIsland.getPlayersInIsland()) {
                        if (playerLocalServerFirstIsland.hasPermission(player)) {
                            MessageUtils.success(player, "电力供应已恢复。");
                        }
                    }
                }
            }
            if (PlayerChargeDetailUtils.getLeftWater(PlayerChargeDetailCommitter.get(owner)) < 0) {
                playerLocalServerFirstIsland.addIslandFlag("DisableWaterFlow");
                for (Player player : playerLocalServerFirstIsland.getPlayersInIsland()) {
                    if (playerLocalServerFirstIsland.hasPermission(player)) {
                        MessageUtils.warn(player, "当前岛屿已停水，请即使缴纳水费。");
                    }
                }
            } else {
                if (playerLocalServerFirstIsland.containsFlag("DisableWaterFlow")) {
                    playerLocalServerFirstIsland.removeIslandFlag("DisableWaterFlow");
                    for (Player player : playerLocalServerFirstIsland.getPlayersInIsland()) {
                        if (playerLocalServerFirstIsland.hasPermission(player)) {
                            MessageUtils.success(player, "水力供应已恢复。");
                        }
                    }
                }
            }
        });
    }


    //更新取样间隔，均匀取样
    public void updateSample(int perTicks) {
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            TICK_SAMPLE_10 = random.nextInt(20) + 1;
            LocalDateTime now = LocalDateTime.now();
            shouldRecord = now.getHour() >= 8;
        }, perTicks, perTicks);
    }

    //获取岛上有玩家的岛屿, 取这些岛屿的岛主
    public Set<String> getIslandHasPlayerUnique() {
        Set<String> owners = new HashSet<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Island island = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);
            if (island == null) {
                continue;
            }
            owners.add(island.getOwner());
        }
        return owners;
    }

    public PlayerConsumeListener() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        updateSample(20);

        //online count
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            //get plot distinct
            producePowerAndWater();
            for (String owner : getIslandHasPlayerUnique()) {
                addOneMinute(owner);
                arrearsDetect(owner);
                warning(owner);
            }
        }, 20 * 60, 20 * 60);
    }


    private void addOneMinute(String owner) {
        PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(owner);
        int onlineMinutes = playerChargeDetail.getOnlineMinutes();
        playerChargeDetail.setOnlineMinutes(onlineMinutes + 1);
    }

    private void warning(String owner) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Island playerLocalServerFirstIsland = IslandManager.INSTANCE.getPlayerLocalServerFirstIsland(owner);
            if (PlayerChargeDetailUtils.getLeftPower(PlayerChargeDetailCommitter.get(owner)) < 500) {
                for (Player player : playerLocalServerFirstIsland.getPlayersInIsland()) {
                    if (playerLocalServerFirstIsland.hasPermission(player)) {
                        MessageUtils.warn(player, "岛屿剩余电量较低，即将停电，请及时缴费。");
                    }
                }
            }
            if (PlayerChargeDetailUtils.getLeftWater(PlayerChargeDetailCommitter.get(owner)) < 500) {
                for (Player player : playerLocalServerFirstIsland.getPlayersInIsland()) {
                    if (playerLocalServerFirstIsland.hasPermission(player)) {
                        MessageUtils.warn(player, "岛屿剩余水量较低，即将停电，请及时缴费。");
                    }
                }
            }
        });
    }


    public static IslandId getPlotId(Location location) {
        return IslandId.fromLocation(location.getBlockX(), location.getBlockZ());
    }

    @Nullable
    public static String getPlotOwner(Location location) {
        IslandId islandId = getPlotId(location);
        String cached = plotOwnerCache.get(islandId);
        if (cached != null) {
            return plotOwnerCache.get(islandId);
        }
        Island island = IslandManager.INSTANCE.getIsland(islandId);
        plotOwnerCache.put(islandId, null);
        if (island == null) {
            return null;
        }
        plotOwnerCache.put(islandId, island.getOwner());
        return island.getOwner();
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockDispenseEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(event.getBlock().getLocation());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getDispenser();
            playerChargeDetail.setDispenser(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockRedstoneEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(event.getBlock().getLocation());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getRedstone();
            playerChargeDetail.setRedstone(dispenser + TICK_SAMPLE_10);
        }
    }



    public void piston(Block block) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(block.getLocation());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getPiston();
            playerChargeDetail.setPiston(dispenser + TICK_SAMPLE_10);
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonExtendEvent event) {
        piston(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonRetractEvent event) {
        piston(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(TNTPrimeEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(event.getBlock().getLocation());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getTnt();
            playerChargeDetail.setTnt(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(FurnaceBurnEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(event.getBlock().getLocation());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getFurnace();
            playerChargeDetail.setFurnace(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(event.getInitiator().getLocation());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getHopper();
            playerChargeDetail.setHopper(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler
    public void on(VehicleMoveEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(event.getTo());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getVehicle();
            playerChargeDetail.setVehicle(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockFromToEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(event.getBlock().getLocation());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getWater();
            playerChargeDetail.setWater(dispenser + TICK_SAMPLE_10);
        }
    }
}
