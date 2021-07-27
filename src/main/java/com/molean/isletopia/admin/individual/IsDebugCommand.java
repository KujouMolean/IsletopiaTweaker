package com.molean.isletopia.admin.individual;

import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class IsDebugCommand implements CommandExecutor {
    public IsDebugCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("isdebug")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        World world = player.getWorld();
        if (currentPlot == null) {
            return false;
        }
        world.save();

        return true;
    }

}
