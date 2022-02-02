package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.infrastructure.individual.ClockMenu;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class NewbieOperation implements Listener {

    public NewbieOperation() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }


    public void afterClaim(Player player, LocalIsland island) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            island.tp(player);
            MessageUtils.success(player, "岛屿分配完毕，开始你的游戏！");
            MessageUtils.strong(player, "记住，你没有重开的机会。");
        });
    }
    public void newPlayerItem(Player player) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
            player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
            player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 1));
            player.getInventory().addItem(new ItemStack(Material.APPLE, 64));
            player.getInventory().addItem(ClockMenu.getClock());
        });
    }

    @EventHandler
    public void onJoin(PlayerDataSyncCompleteEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Player player = event.getPlayer();
            int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(player.getUniqueId());
            if (playerIslandCount == 0) {
                newPlayerItem(player);
                IslandManager.INSTANCE.createNewIsland(player.getUniqueId(), (island -> {
                    if (island == null) {
                        PlayerUtils.kickAsync(player, "#岛屿创建失败，请联系管理员。");
                        return;
                    }
                    afterClaim(player, island);
                }));
            }
        });
    }
}
