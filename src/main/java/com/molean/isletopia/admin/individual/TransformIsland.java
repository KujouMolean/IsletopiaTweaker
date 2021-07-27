package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlayTimeStatisticsDao;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class TransformIsland implements CommandExecutor {

    public TransformIsland() {
        Objects.requireNonNull(Bukkit.getPluginCommand("transform")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Plot currentPlot = PlotUtils.getCurrentPlot(player);

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (currentPlot == null) {
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

            UUID owner = currentPlot.getOwner();
            assert owner != null;
            String single = PlotSquared.get().getImpromptuUUIDPipeline().getSingle(owner, 1000);
            if (single == null) {
                player.sendMessage("§c失败!查询岛主出现错误.");
                return;
            }
            long lastPlayTimestamp = PlayTimeStatisticsDao.getLastPlayTimestamp(single);

            if (System.currentTimeMillis() - lastPlayTimestamp < 1000L * 60 * 60 * 24 * 30) {
                player.sendMessage("§c失败!岛主最近一个月有上线记录.");
                return;
            }
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                World world = Bukkit.getWorld(Objects.requireNonNull(currentPlot.getWorldName()));
                assert world != null;
                Location bot = PlotUtils.fromPlotLocation(currentPlot.getBottomAbs());
                Location top = PlotUtils.fromPlotLocation(currentPlot.getTopAbs());
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
                commonResponseObject.setMessage("玩家 " + player.getName() + " 已将 " + single + "的岛屿转让给 " + args[0] + " !");
                ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);

                currentPlot.setOwner(ServerInfoUpdater.getUUID(args[0]));
                PlotSquared.get().getImpromptuUUIDPipeline().storeImmediately(args[0], ServerInfoUpdater.getUUID(args[0]));
                UniversalParameter.setParameter(args[0], "server", ServerInfoUpdater.getServerName());
                player.sendMessage("§a成功!已修改岛屿新主人为: " + args[0]);

            });
        });
        return true;


    }
}
