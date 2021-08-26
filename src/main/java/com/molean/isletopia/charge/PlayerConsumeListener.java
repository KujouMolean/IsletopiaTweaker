package com.molean.isletopia.charge;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.molean.isletopia.utils.UUIDUtils;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import com.plotsquared.core.plot.flag.implementations.LiquidFlowFlag;
import com.plotsquared.core.plot.flag.implementations.RedstoneFlag;
import org.bukkit.*;
import org.bukkit.block.Beacon;
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
import java.time.ZoneId;
import java.util.*;

public class PlayerConsumeListener implements Listener {
    private static int TICK_SAMPLE_10 = 10;
    private static final Random random = new Random();
    private static final Map<PlotId, String> plotOwnerCache = new HashMap<>();

    private static boolean shouldRecord = true;


    public PlayerConsumeListener() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        //update sample
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            TICK_SAMPLE_10 = random.nextInt(20) + 1;

            LocalDateTime now = LocalDateTime.now();
            if (now.getHour() < 8) {
                shouldRecord = false;
            } else {
                shouldRecord = true;
            }

        }, 20, 20);


        //online count
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {

            //get plot distinct
            Set<String> owners = new HashSet<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Plot currentPlot = PlotUtils.getCurrentPlot(onlinePlayer);
                if (currentPlot == null) {
                    return;
                }
                UUID owner = currentPlot.getOwner();
                String s = UUIDUtils.get(owner);
                owners.add(s);
            }


            for (String owner : owners) {
                PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(owner);
                int onlineMinutes = playerChargeDetail.getOnlineMinutes();
                playerChargeDetail.setOnlineMinutes(onlineMinutes + 1);
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    Plot plot = PlotUtils.getPlot(owner);
                    if (PlayerChargeDetailCommitter.getLastWeekPlayerChargeDetail(owner) != null) {
//                        plot.setFlag(RedstoneFlag.REDSTONE_FALSE);
//                        plot.setFlag(LiquidFlowFlag.LIQUID_FLOW_DISABLED);
                    } else {
//                        plot.removeFlag(RedstoneFlag.REDSTONE_FALSE);
//                        plot.removeFlag(LiquidFlowFlag.LIQUID_FLOW_DISABLED);
                    }
                });
            }


            World skyWorld = Bukkit.getWorld("SkyWorld");
            assert skyWorld != null;
            @NotNull Chunk[] loadedChunks = skyWorld.getLoadedChunks();
            for (Chunk loadedChunk : loadedChunks) {
                Arrays.stream(loadedChunk.getTileEntities(false))
                        .filter(blockState -> blockState.getType().equals(Material.BEACON))
                        .map(blockState -> (Beacon) blockState)
                        .forEach(beacon -> {
                            if (beacon.getTier() > 0) {
                                String s = UUIDUtils.get(PlotUtils.getCurrentPlot(beacon.getLocation()).getOwner());
                                PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(s);
                                playerChargeDetail.setPowerProduceTimes(playerChargeDetail.getPowerProduceTimes() + 1);
                            }

                        });


                Arrays.stream(loadedChunk.getTileEntities(false))
                        .filter(blockState -> blockState.getType().equals(Material.CONDUIT))
                        .map(blockState -> (Conduit) blockState)
                        .forEach(conduit -> {
                            String s = UUIDUtils.get(PlotUtils.getCurrentPlot(conduit.getLocation()).getOwner());
                            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(s);
                            playerChargeDetail.setWaterProduceTimes(playerChargeDetail.getWaterProduceTimes() + 1);
                        });
            }
        }, 20 * 60, 20 * 60);
    }


    public static PlotId getPlotId(Location location) {
        int plotX = Math.floorDiv(location.getBlockX(), 512) + 1;
        int plotZ = Math.floorDiv(location.getBlockZ(), 512) + 1;
        return PlotId.of(plotX, plotZ);
    }


    @Nullable
    public static String getPlotOwner(Location location) {
        PlotId plotId = getPlotId(location);
        String cached = plotOwnerCache.get(plotId);
        if (cached != null) {
            return plotOwnerCache.get(plotId);
        }
        Plot plot = PlotUtils.getFirstPlotArea().getPlot(plotId);
        plotOwnerCache.put(plotId, null);
        if (plot == null) {
            return null;
        }
        UUID owner = plot.getOwner();
        String s = UUIDUtils.get(owner);
        plotOwnerCache.put(plotId, s);
        return s;
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockDispenseEvent event) {
        if(!shouldRecord){
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
        if(!shouldRecord){
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


    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonExtendEvent event) {
        if(!shouldRecord){
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(event.getBlock().getLocation());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getPiston();
            playerChargeDetail.setPiston(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonRetractEvent event) {
        if(!shouldRecord){
            return;
        }
        if (Bukkit.getCurrentTick() % TICK_SAMPLE_10 == 0) {
            String plotOwner = getPlotOwner(event.getBlock().getLocation());
            if (plotOwner == null) {
                return;
            }
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(plotOwner);
            long dispenser = playerChargeDetail.getPiston();
            playerChargeDetail.setPiston(dispenser + TICK_SAMPLE_10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(TNTPrimeEvent event) {
        if(!shouldRecord){
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
        if(!shouldRecord){
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
        if(!shouldRecord){
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
        if(!shouldRecord){
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
        if(!shouldRecord){
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
