package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.infrastructure.individual.ClockMenu;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.chat.TextComponent;
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

    @EventHandler
    public void onJoin(PlayerDataSyncCompleteEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Player player = event.getPlayer();


            int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(player.getName());

            String server = UniversalParameter.getParameter(player.getName(), "server");

            if (server == null) {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    player.kick(Component.text("#发生错误, 转发器未指定你的岛屿所在服务器."));
                });
                return;
            }

            if (!server.equals(ServerInfoUpdater.getServerName())) {
                if (playerIslandCount == 0) {
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        player.kick(Component.text("#发生错误, 你被转发器发送到了错误的服务器, 请联系管理员."));
                    });
                }
                return;
            }

            if (playerIslandCount == 0) {
                try {
                    IslandManager.INSTANCE.createNewIsland(player.getName(), (island -> {
                        if (island == null) {
                            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                                player.kick(Component.text("#岛屿创建失败，请联系管理员。"));
                            });
                            return;
                        }
                        island.tp(player);
                        MessageUtils.success(player, "岛屿分配完毕，开始你的游戏！");
                        MessageUtils.strong(player, "记住，你没有重开的机会。");
                        player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
                        player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
                        player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 1));
                        player.getInventory().addItem(new ItemStack(Material.APPLE, 64));
                        player.getInventory().addItem(ClockMenu.getClock());
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        player.kick(Component.text("#岛屿创建失败，请联系管理员。"));
                    });
                }
            }

        });
    }
}
