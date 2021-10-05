package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlayTimeStatisticsDao;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TransformIsland implements CommandExecutor {

    public TransformIsland() {
        Objects.requireNonNull(Bukkit.getPluginCommand("transform")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Island island = IslandManager.INSTANCE.getCurrentIsland(player);

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (island == null) {
                player.sendMessage("§c失败!岛屿不存在.");

                return;
            }
            if (args.length < 1) {
                player.sendMessage("§c/transform 玩家ID");
                return;
            }

            if (!args[0].matches("[0-9a-zA-z_]{3,16}")) {
                player.sendMessage("§c失败!用户名不合法");
                return;
            }

            String server = UniversalParameter.getParameter(args[0], "server");
            if (server != null && !server.isEmpty()) {
                player.sendMessage("§c失败!玩家已拥有岛屿");
                return;
            }

            long lastPlayTimestamp = PlayTimeStatisticsDao.getLastPlayTimestamp(island.getOwner());

            if (System.currentTimeMillis() - lastPlayTimestamp < 1000L * 60 * 60 * 24 * 30) {
                player.sendMessage("§c失败!岛主最近一个月有上线记录.");
                return;
            }
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                World world = Bukkit.getWorld("SkyWorld");
                assert world != null;
                Location bot = island.getBottomLocation();
                Location top = island.getTopLocation();
                int areaCount = 0;
                for (int i = bot.getBlockX(); i < top.getBlockX(); i++) {
                    for (int j = bot.getBlockZ(); j < top.getBlockZ(); j++) {
                        int highestBlockYAt = world.getHighestBlockYAt(i, j);
                        if (highestBlockYAt > 1) {
                            areaCount++;
                        }
                    }
                }
                if (areaCount > 1000) {
                    player.sendMessage("§c失败!岛屿面积大于1000");
                    return;
                }
                CommonResponseObject commonResponseObject = new CommonResponseObject();
                commonResponseObject.setMessage("玩家 " + player.getName() + " 已将 " + island.getOwner()+ "的岛屿转让给 " + args[0] + " !");
                ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                island.setOwner(args[0]);
                UniversalParameter.setParameter(args[0], "server", ServerInfoUpdater.getServerName());
                player.sendMessage("§a成功!已修改岛屿新主人为: " + args[0]);

            });
        });
        return true;


    }
}
