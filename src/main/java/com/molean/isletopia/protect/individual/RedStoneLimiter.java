package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.implementations.RedstoneFlag;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class RedStoneLimiter implements Listener, CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (!PlotUtils.hasCurrentPlotPermission(player)) {
            return false;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        assert currentPlot != null;
        currentPlot.setFlag(RedstoneFlag.REDSTONE_TRUE);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    private static class TimeLimitedCount {
        long firstTime;
        int count;
    }

    public static final Map<Plot, TimeLimitedCount> redStoneDataMap = new HashMap<>();

    public RedStoneLimiter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("redstone")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("redstone")).setTabCompleter(this);
    }

    //redstone
    @EventHandler
    public void on(BlockRedstoneEvent event) {
        Plot currentPlot = PlotUtils.getCurrentPlot(event.getBlock().getLocation());
        TimeLimitedCount redStoneData = redStoneDataMap.getOrDefault(currentPlot, new TimeLimitedCount());
        if (System.currentTimeMillis() - redStoneData.firstTime > 3000) {
            redStoneData.firstTime = System.currentTimeMillis();
            redStoneData.count = 1;
        } else {
            redStoneData.count++;
            if (redStoneData.count > 5000) {
//                currentPlot.setFlag(RedstoneFlag.REDSTONE_FALSE);
                for (PlotPlayer<?> plotPlayer : currentPlot.getPlayersInPlot()) {
                    Player player = Bukkit.getPlayer(plotPlayer.getName());
                    if (PlotUtils.hasCurrentPlotPermission(player)) {
                        player.sendMessage("§c所在岛屿单位时间激活了太多的红石，请减少使用量。");
                    }
                }
            }
        }
        redStoneDataMap.put(currentPlot, redStoneData);
    }

//    //dispenser
//    @EventHandler
//    public void on(BlockDispenseEvent event) {
//        //todo
//    }
//
//
//    //piston
//    @EventHandler
//    public void on(BlockPistonEvent event) {
//        //todo
//    }


//    //hopper
//    @EventHandler
//    public void on(InventoryMoveItemEvent event) {
//        if (!event.getInitiator().equals(event.getDestination())) {
//            return;
//        }
//        if (!event.getInitiator().getType().equals(InventoryType.HOPPER)) {
//            return;
//        }
//        //todo
//    }
}
