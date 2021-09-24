package com.molean.isletopia.admin.individual;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.obj.CuboidRegion;
import com.molean.isletopia.island.obj.CuboidShape;
import com.molean.isletopia.utils.ChunkUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class IsDebugCommand implements CommandExecutor {

    public IsDebugCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("isdebug")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        World world = IsletopiaTweakers.getWorld();

        File target = new File(world.getWorldFolder() + "/region/r.0.0.mca");

        try {
            ChunkUtils.hotSwapRegionFile(player.getLocation().getBlockX() >> 9, player.getLocation().getBlockZ() >> 9, target);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                System.out.println(onlinePlayer.getLocation().getBlock().getType());
            }
        }, 200, 200);

        return true;
    }

}
