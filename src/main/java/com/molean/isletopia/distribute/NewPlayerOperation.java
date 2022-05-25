package com.molean.isletopia.distribute;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.infrastructure.ClockMenu;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.BukkitPlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

@Singleton
public class NewbieOperation implements Listener {



    public void afterClaim(Player player, LocalIsland island) {
        Tasks.INSTANCE.sync(() -> {
            island.tp(player);
            MessageUtils.success(player, "island.create.complete");
            MessageUtils.strong(player, "island.create.noRemake");
        });

    }
    public void newPlayerItem(Player player) {
        Tasks.INSTANCE.async(() -> {
            player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
            player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
            player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 1));
            player.getInventory().addItem(new ItemStack(Material.APPLE, 64));
            player.getInventory().addItem(ClockMenu.getClock(player));
        });
    }

    @EventHandler
    public void onJoin(PlayerLoggedEvent event) {
        Tasks.INSTANCE.async(() -> {
            Player player = event.getPlayer();
            int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(player.getUniqueId());
            if (playerIslandCount == 0) {
                newPlayerItem(player);
                IslandManager.INSTANCE.createNewIsland(player.getUniqueId(), (island -> {
                    if (island == null) {
                        BukkitPlayerUtils.kickAsync(player, "#Island create failed, please contact server administrator!");
                        return;
                    }
                    afterClaim(player, island);
                }));
            }
        });
    }
}
