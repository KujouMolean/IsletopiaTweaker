package com.molean.isletopia.statistics.individual;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.entity.EndermanEscapeEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.TurtleLayEggEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import io.papermc.paper.event.block.BellRingEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IslandEnvironment implements Listener, CommandExecutor {

    private static final Map<PlotId, ResourceConsumeStatistics> resourceConsumeStatisticsMap = new HashMap<>();


    public IslandEnvironment() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("consume")).setExecutor(this);
    }

    public static ResourceConsume getResourceConsume(PlotId plotId) {
        if (resourceConsumeStatisticsMap.containsKey(plotId)) {
            return resourceConsumeStatisticsMap.get(plotId).getResourceConsume();
        }

        return new ResourceConsume(0);
    }

    private void addPowerUsage(Location location, String reason, int amount) {
        int plotX = Math.floorDiv(location.getBlockX(), 512) + 1;
        int plotZ = Math.floorDiv(location.getBlockZ(), 512) + 1;
        PlotId plotId = PlotId.of(plotX, plotZ);
        if (!resourceConsumeStatisticsMap.containsKey(plotId)) {
            resourceConsumeStatisticsMap.put(plotId, new ResourceConsumeStatistics());
        }
        ResourceConsumeStatistics resourceConsumeStatistics = resourceConsumeStatisticsMap.get(plotId);
        resourceConsumeStatistics.addPowerUsage(reason, (long) amount);
    }

    private void addWaterUsage(Location location, String reason, int amount) {
        int plotX = Math.floorDiv(location.getBlockX(), 512) + 1;
        int plotZ = Math.floorDiv(location.getBlockZ(), 512) + 1;
        PlotId plotId = PlotId.of(plotX, plotZ);
        if (!resourceConsumeStatisticsMap.containsKey(plotId)) {
            resourceConsumeStatisticsMap.put(plotId, new ResourceConsumeStatistics());
        }
        ResourceConsumeStatistics resourceConsumeStatistics = resourceConsumeStatisticsMap.get(plotId);
        resourceConsumeStatistics.addWaterUsage(reason, (long) amount);
    }

    private void addAirUsage(Location location, String reason, int amount) {
        int plotX = Math.floorDiv(location.getBlockX(), 512) + 1;
        int plotZ = Math.floorDiv(location.getBlockZ(), 512) + 1;
        PlotId plotId = PlotId.of(plotX, plotZ);
        if (!resourceConsumeStatisticsMap.containsKey(plotId)) {
            resourceConsumeStatisticsMap.put(plotId, new ResourceConsumeStatistics());
        }
        ResourceConsumeStatistics resourceConsumeStatistics = resourceConsumeStatisticsMap.get(plotId);
        resourceConsumeStatistics.addAirUsage(reason, (long) amount);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockDispenseEvent event) {
        if (Bukkit.getCurrentTick() % 10 == 0) {
            addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 2 * 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockRedstoneEvent event) {
        if (Bukkit.getCurrentTick() % 10 == 0) {
            addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BeaconEffectEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 5);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonExtendEvent event) {
        if (Bukkit.getCurrentTick() % 10 == 0) {
            addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 3 * 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonRetractEvent event) {
        if (Bukkit.getCurrentTick() % 10 == 0) {
            addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 3 * 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(TNTPrimeEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 120);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(NotePlayEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 5);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockExplodeEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 50);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockShearEntityEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BrewEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 5);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BellRingEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockCookEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 2);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(FurnaceBurnEvent event) {
        addPowerUsage(event.getBlock().getLocation(), event.getEventName(), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent event) {
        if (Bukkit.getCurrentTick() % 100 == 0) {
            addPowerUsage(Objects.requireNonNull(event.getSource().getLocation()), event.getEventName(), 10);
        }
    }

    //water

    @EventHandler(ignoreCancelled = true)
    public void on(BlockFromToEvent event) {
        if (Bukkit.getCurrentTick() % 10 == 0) {
            addWaterUsage(event.getBlock().getLocation(), event.getEventName(), 10);
        }
    }


    //entity
    @EventHandler(ignoreCancelled = true)
    public void on(EndermanEscapeEvent event) {
        if (Bukkit.getCurrentTick() % 10 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(SlimeSplitEvent event) {
        if (Bukkit.getCurrentTick() % 10 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(TurtleLayEggEvent event) {
        addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntitySpawnEvent event) {
        if (Bukkit.getCurrentTick() % 10 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityMoveEvent event) {
        if (Bukkit.getCurrentTick() % 100 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityPathfindEvent event) {
        if (Bukkit.getCurrentTick() % 100 == 0) {
            addAirUsage(event.getEntity().getLocation(), event.getEventName(), 10);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        assert currentPlot != null;
        ResourceConsume resourceConsume = getResourceConsume(currentPlot.getId());
        Map<String, Long> powerUsageMap = resourceConsume.getPowerUsageMap();
        long power = 0;
        for (String s : powerUsageMap.keySet()) {
            Long aLong = powerUsageMap.get(s);
            power += aLong;
        }
        Map<String, Long> waterUsageMap = resourceConsume.getWaterUsageMap();
        long water = 0;
        for (String s : waterUsageMap.keySet()) {
            Long aLong = waterUsageMap.get(s);
            water += aLong;
        }
        Map<String, Long> airUsageMap = resourceConsume.getAirUsageMap();
        long air = 0;
        for (String s : airUsageMap.keySet()) {
            Long aLong = airUsageMap.get(s);
            air += aLong;
        }

        com.plotsquared.core.location.Location bot = currentPlot.getBottomAbs();
        com.plotsquared.core.location.Location top = currentPlot.getTopAbs();
        BoundingBox boundingBox = new BoundingBox(bot.getX(), bot.getY(), bot.getZ(), top.getX(), top.getY(), top.getZ());
        Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(boundingBox);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof Animals) {
                air += 10;
            }
            if (nearbyEntity instanceof Monster) {
                air += 50;
            }
            if (nearbyEntity instanceof Villager) {
                air += 500;
            }
            if (nearbyEntity instanceof Minecart) {
                air += 20;
            }
        }


        player.sendMessage("能源消耗: " + (power) + "KJ/min");
        player.sendMessage("水源污染: " + (water) + "t/min");
        player.sendMessage("生物负载: " + (air) + "/min");

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("detail")) {
                {
                    ArrayList<String> strings = new ArrayList<>(powerUsageMap.keySet());
                    strings.sort((o1, o2) -> (int) (powerUsageMap.get(o2) - powerUsageMap.get(o1)));
                    for (int i = 0; i < 10 && i < strings.size(); i++) {
                        player.sendMessage(strings.get(i) + " " + powerUsageMap.get(strings.get(i)));
                    }
                }
                {
                    ArrayList<String> strings = new ArrayList<>(airUsageMap.keySet());
                    strings.sort((o1, o2) -> (int) (airUsageMap.get(o2) - airUsageMap.get(o1)));
                    for (int i = 0; i < 10 && i < strings.size(); i++) {
                        player.sendMessage(strings.get(i) + " " + airUsageMap.get(strings.get(i)));
                    }
                }

            }
        }
        return true;
    }
}
