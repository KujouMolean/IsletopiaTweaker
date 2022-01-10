package com.molean.isletopia.charge;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Conduit;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;

public class ConsumeListener implements Listener {
    private static int TICK_SAMPLE_10 = 10;
    private static final Random random = new Random();
    private static final Map<IslandId, UUID> plotOwnerCache = new HashMap<>();
    private static boolean shouldRecord = true;

    public void producePowerAndWater() {
        @NotNull Chunk[] loadedChunks = IsletopiaTweakers.getWorld().getLoadedChunks();
        for (Chunk loadedChunk : loadedChunks) {
            Arrays.stream(loadedChunk.getTileEntities(false))
                    .filter(blockState -> blockState.getType().equals(Material.BEACON))
                    .map(blockState -> (Beacon) blockState)
                    .forEach(beacon -> {
                        if (beacon.getTier() > 0) {
                            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(beacon.getLocation());
                            if (currentIsland == null) {
                                return;
                            }
                            ChargeDetail chargeDetail = ChargeDetailCommitter.get(currentIsland.getIslandId());
                            chargeDetail.setPowerProduceTimes(chargeDetail.getPowerProduceTimes() + 1);
                        }
                    });
            Arrays.stream(loadedChunk.getTileEntities(false))
                    .filter(blockState -> blockState.getType().equals(Material.CONDUIT))
                    .map(blockState -> (Conduit) blockState)
                    .forEach(conduit -> {
                        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(conduit.getLocation());
                        if (currentIsland == null) {
                            return;
                        }
                        ChargeDetail chargeDetail = ChargeDetailCommitter.get(currentIsland.getIslandId());
                        chargeDetail.setWaterProduceTimes(chargeDetail.getWaterProduceTimes() + 1);
                    });
        }
    }

    public void countHopper() {
        @NotNull Chunk[] loadedChunks = IsletopiaTweakers.getWorld().getLoadedChunks();
        for (Chunk loadedChunk : loadedChunks) {
            long count = Arrays.stream(loadedChunk.getTileEntities())
                    .filter(blockState -> blockState.getType().equals(Material.HOPPER))
                    .map(blockState -> (Hopper) blockState)
                    .filter(hopper -> !hopper.isLocked())
                    .count();
            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(loadedChunk);
            UUID s = Objects.requireNonNull(currentIsland).getUuid();
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(currentIsland.getIslandId());
            chargeDetail.setHopper(chargeDetail.getHopper() + count * 60 * 2);
        }
    }


    public void arrearsDetect(LocalIsland localIsland) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (shouldRecord && ChargeDetailUtils.getLeftPower(ChargeDetailCommitter.get(localIsland.getIslandId())) < 0) {
                localIsland.addIslandFlag("DisableRedstone");
                for (Player player : localIsland.getPlayersInIsland()) {
                    if (localIsland.hasPermission(player)) {
                        MessageUtils.warn(player, "当前岛屿已停电，请即使缴纳电费。");
                    }
                }
            } else {
                if (localIsland.containsFlag("DisableRedstone")) {
                    localIsland.removeIslandFlag("DisableRedstone");
                    for (Player player : localIsland.getPlayersInIsland()) {
                        if (localIsland.hasPermission(player)) {
                            MessageUtils.success(player, "电力供应已恢复。");
                        }
                    }
                }
            }
            if (shouldRecord && ChargeDetailUtils.getLeftWater(ChargeDetailCommitter.get(localIsland.getIslandId())) < 0) {
                localIsland.addIslandFlag("DisableWaterFlow");
                for (Player player : localIsland.getPlayersInIsland()) {
                    if (localIsland.hasPermission(player)) {
                        MessageUtils.warn(player, "当前岛屿已停水，请及时缴纳水费。");
                    }
                }
            } else {
                if (localIsland.containsFlag("DisableWaterFlow")) {
                    localIsland.removeIslandFlag("DisableWaterFlow");
                    for (Player player : localIsland.getPlayersInIsland()) {
                        if (localIsland.hasPermission(player)) {
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
    public Set<LocalIsland> getIslandHasPlayerUnique() {
        Set<LocalIsland> owners = new HashSet<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            LocalIsland island = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);
            if (island == null) {
                continue;
            }
            owners.add(island);

        }
        return owners;
    }

    public ConsumeListener() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        updateSample(20);

        //online count
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            //get plot distinct
            producePowerAndWater();
            countHopper();
            for (LocalIsland owner : getIslandHasPlayerUnique()) {
                addOneMinute(owner.getIslandId());
                arrearsDetect(owner);
                warning(owner);
            }
        }, 20 * 60, 20 * 60);

        IsletopiaTweakers.addDisableTask("Stop listen player consumer", bukkitTask::cancel);

    }


    private void addOneMinute(IslandId owner) {
        ChargeDetail chargeDetail = ChargeDetailCommitter.get(owner);
        int onlineMinutes = chargeDetail.getOnlineMinutes();
        chargeDetail.setOnlineMinutes(onlineMinutes + 1);
    }

    private void warning(LocalIsland localIsland) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (localIsland == null) {
                return;
            }
            long leftPower = ChargeDetailUtils.getLeftPower(ChargeDetailCommitter.get(localIsland.getIslandId()));
            long leftWater = ChargeDetailUtils.getLeftWater(ChargeDetailCommitter.get(localIsland.getIslandId()));
            if (leftPower < 500 && leftPower > 0) {
                for (Player player : localIsland.getPlayersInIsland()) {
                    if (localIsland.hasPermission(player)) {
                        MessageUtils.warn(player, "岛屿剩余电量较低，即将停电，请及时缴费。");
                    }
                }
            }
            if (leftWater < 500 && leftWater > 0) {
                for (Player player : localIsland.getPlayersInIsland()) {
                    if (localIsland.hasPermission(player)) {
                        MessageUtils.warn(player, "岛屿剩余水量较低，即将停电，请及时缴费。");
                    }
                }
            }
        });
    }


    public static IslandId getPlotId(Location location) {
        return IslandId.fromLocation(ServerInfoUpdater.getServerName(), location.getBlockX(), location.getBlockZ());
    }

    @Nullable
    public static UUID getPlotOwner(Location location) {
        IslandId islandId = getPlotId(location);
        UUID cached = plotOwnerCache.get(islandId);
        if (cached != null) {
            return plotOwnerCache.get(islandId);
        }
        LocalIsland island = IslandManager.INSTANCE.getLocalIsland(islandId);
        plotOwnerCache.put(islandId, null);
        if (island == null) {
            return null;
        }
        plotOwnerCache.put(islandId, island.getUuid());
        return island.getUuid();
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockDispenseEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            IslandId plotOwner = getPlotId(event.getBlock().getLocation());
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(plotOwner);
            long dispenser = chargeDetail.getDispenser();
            chargeDetail.setDispenser(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockRedstoneEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            IslandId plotOwner = getPlotId(event.getBlock().getLocation());
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(plotOwner);
            long dispenser = chargeDetail.getRedstone();
            chargeDetail.setRedstone(dispenser + TICK_SAMPLE_10);
        }
    }


    public void piston(Block block) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            IslandId plotOwner = getPlotId(block.getLocation());
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(plotOwner);
            long dispenser = chargeDetail.getPiston();
            chargeDetail.setPiston(dispenser + TICK_SAMPLE_10);
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
            IslandId plotOwner = getPlotId(event.getBlock().getLocation());
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(plotOwner);
            long dispenser = chargeDetail.getTnt();
            chargeDetail.setTnt(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(FurnaceBurnEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            IslandId plotOwner = getPlotId(event.getBlock().getLocation());
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(plotOwner);
            long dispenser = chargeDetail.getFurnace();
            chargeDetail.setFurnace(dispenser + TICK_SAMPLE_10);
        }
    }

    public void on() {

    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            if (event.getInitiator().getLocation() == null) {
                return;
            }
            IslandId plotOwner = getPlotId(event.getInitiator().getLocation());
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(plotOwner);
            long dispenser = chargeDetail.getHopper();
            chargeDetail.setHopper(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler
    public void on(VehicleMoveEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            IslandId plotOwner = getPlotId(event.getTo());
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(plotOwner);
            long dispenser = chargeDetail.getVehicle();
            chargeDetail.setVehicle(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockFromToEvent event) {
        if (!shouldRecord) {
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            if (!event.getBlock().getType().equals(Material.WATER)) {
                return;
            }

            IslandId plotOwner = getPlotId(event.getBlock().getLocation());
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(plotOwner);
            long dispenser = chargeDetail.getWater();
            chargeDetail.setWater(dispenser + TICK_SAMPLE_10);
        }
    }
}
