package com.molean.isletopia.charge;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.model.ChargeDetail;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.task.MassiveChunkTask;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.time.LocalDateTime;
import java.util.*;

public class ConsumeListener implements Listener {
    private static int TICK_SAMPLE_10 = 10;
    private static final Random random = new Random();
    private static boolean shouldRecord = true;

    public void countHopper() {
        if (!shouldRecord) {
            return;
        }
        ArrayList<Chunk> chunks = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            chunks.addAll(List.of(world.getLoadedChunks()));
        }
        new MassiveChunkTask(chunks.toArray(new Chunk[0]), chunk -> {
            if (!chunk.isLoaded()) {
                return;
            }
            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(chunk);

            if (currentIsland == null) {
                return;
            }

            if (currentIsland.containsFlag("PowerOff")) {
                Arrays.stream(chunk.getTileEntities())
                        .filter(blockState -> blockState.getType().equals(Material.HOPPER))
                        .map(blockState -> (Hopper) (blockState.getBlockData()))
                        .forEach(hopper -> hopper.setEnabled(false));

            } else {
                long count = Arrays.stream(chunk.getTileEntities())
                        .filter(blockState -> blockState.getType().equals(Material.HOPPER))
                        .map(blockState -> (Hopper) (blockState.getBlockData()))
                        .filter(Hopper::isEnabled)
                        .count();
                ChargeDetail chargeDetail = ChargeDetailCommitter.get(currentIsland.getIslandId());
                chargeDetail.setHopper(chargeDetail.getHopper() + 200 * count);
            }


        }, 30 * 20).run();

    }

    public void arrearsDetect(LocalIsland localIsland) {
        Tasks.INSTANCE.async(() -> {
            if (shouldRecord && ChargeDetailUtils.getLeftPower(ChargeDetailCommitter.get(localIsland.getIslandId())) < 0) {
                localIsland.addIslandFlag("PowerOff");
                for (Player player : localIsland.getPlayersInIsland()) {
                    if (localIsland.hasPermission(player)) {
                        MessageUtils.warn(player, "island.consumer.powerNotEnough");
                    }
                }
            } else {
                if (localIsland.containsFlag("PowerOff")) {
                    localIsland.removeIslandFlag("PowerOff");
                    for (Player player : localIsland.getPlayersInIsland()) {
                        if (localIsland.hasPermission(player)) {
                            MessageUtils.success(player, "island.consumer.powerRecovery");
                        }
                    }
                }
            }
        });
    }


    //更新取样间隔，均匀取样
    public void updateSample(int perTicks) {
        Tasks.INSTANCE.interval(perTicks, () -> {
            TICK_SAMPLE_10 = random.nextInt(20) + 1;
            LocalDateTime now = LocalDateTime.now();
            shouldRecord = now.getHour() >= 12;
        });
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
        PluginUtils.registerEvents(this);

        updateSample(20);

        Tasks.INSTANCE.interval(20 * 60, () -> {
            countHopper();
            for (LocalIsland owner : getIslandHasPlayerUnique()) {
                addOneMinute(owner.getIslandId());
                arrearsDetect(owner);
            }
        });


    }


    private void addOneMinute(IslandId owner) {
        ChargeDetail chargeDetail = ChargeDetailCommitter.get(owner);
        int onlineMinutes = chargeDetail.getOnlineMinutes();
        chargeDetail.setOnlineMinutes(onlineMinutes + 1);
    }


    public static IslandId getPlotId(Location location) {
        return IslandId.fromLocation(ServerInfoUpdater.getServerName(), location.getBlockX(), location.getBlockZ());
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
