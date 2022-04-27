package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.dialog.ConfirmDialog;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SlimeChunk implements CommandExecutor {
    public SlimeChunk() {
        Objects.requireNonNull(Bukkit.getPluginCommand("slime")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        new ConfirmDialog(MessageUtils.getMessage((Player) commandSender, "infrastructure.slime.rules")).accept(player -> {
            World world = player.getLocation().getWorld();
            final int x = player.getLocation().getChunk().getX();
            final int blockY = player.getLocation().getBlockY();
            final int z = player.getLocation().getChunk().getZ();

            Tasks.INSTANCE.timeout(30 * 20, () -> {
                if (!player.isOnline()) {
                    return;
                }
                for (int i = x - 4; i < x + 5; i++) {
                    for (int j = z - 4; j < z + 5; j++) {
                        Chunk chunk = world.getChunkAt(i, j);
                        if (chunk.isSlimeChunk()) {
                            for (int a = 0; a < 16; a++) {
                                for (int b = 0; b < 16; b++) {
                                    Block block = chunk.getBlock(a, blockY, b);
                                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                                }
                            }
                        }
                    }
                }
            });
            BlockData blockData = Bukkit.createBlockData(Material.SLIME_BLOCK);
            for (int i = x - 4; i < x + 5; i++) {
                for (int j = z - 4; j < z + 5; j++) {
                    Chunk chunk = world.getChunkAt(i, j);
                    if (chunk.isSlimeChunk()) {
                        for (int a = 0; a < 16; a++) {
                            for (int b = 0; b < 16; b++) {
                                Location location = chunk.getBlock(a, blockY, b).getLocation();
                                player.sendBlockChange(location, blockData);
                            }
                        }
                    }
                }
            }
        }).open((Player) commandSender);
        return true;
    }
}
