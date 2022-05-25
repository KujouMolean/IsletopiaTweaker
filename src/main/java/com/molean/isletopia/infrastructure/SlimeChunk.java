package com.molean.isletopia.infrastructure;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.dialog.ConfirmDialog;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

@Singleton
@CommandAlias("slime")
public class SlimeChunk extends BaseCommand {

    @Default
    public void onDefault(Player player) {
        ConfirmDialog confirmDialog = new ConfirmDialog(player, MessageUtils.getMessage(player, "infrastructure.slime.rules"));
        confirmDialog.onConfirm(ignore -> {
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
        });
        confirmDialog.open();
    }
}
