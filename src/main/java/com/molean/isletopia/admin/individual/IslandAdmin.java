package com.molean.isletopia.admin.individual;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.ConvertDao;
import com.molean.isletopia.database.IslandDao;
import com.molean.isletopia.island.*;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.task.PlotChunkTask;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.ResourceUtils;
import com.mysql.cj.result.Field;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class IslandAdmin implements CommandExecutor, TabCompleter {
    public IslandAdmin() {
        Objects.requireNonNull(Bukkit.getPluginCommand("isadmin")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("isadmin")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("参数不足");
            return true;
        }
        Player player = (Player) commandSender;

        switch (strings[0].toLowerCase(Locale.ROOT)) {
            case "setowner" -> {
                if (strings.length < 2) {
                    commandSender.sendMessage("参数不足");
                    return true;
                }
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    player.sendMessage("当前岛屿未被领取");
                    return true;
                }
                currentIsland.setOwner(strings[1]);
                player.sendMessage("应该成功了");
            }
            case "claim" -> {
                IslandId islandId = IslandId.fromLocation(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
                IslandManager.INSTANCE.createNewIsland(islandId, player.getName(), (island) -> {
                    if (island == null) {
                        player.sendMessage("领取失败");
                    } else {
                        island.tp(player);
                        player.sendMessage("应该成功了");
                    }
                });

            }
            case "clear" -> {
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    player.sendMessage("岛屿尚未被领取");
                    break;
                }
                player.sendMessage("开始清空岛屿..");
                currentIsland.clearAndApplyNewIsland(() -> {
                    player.sendMessage("清空成功!");
                }, 60);

            }

            case "add" -> {
                if (strings.length < 2) {
                    commandSender.sendMessage("参数不足");
                    return true;
                }
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    player.sendMessage("当前岛屿未被领取");
                    return true;
                }
                currentIsland.addMember(strings[1]);
                player.sendMessage("应该成功了");
            }
            case "remove" -> {
                if (strings.length < 2) {
                    commandSender.sendMessage("参数不足");
                    return true;
                }
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    player.sendMessage("当前岛屿未被领取");
                    return true;
                }

                currentIsland.removeMember(strings[1]);
                player.sendMessage("应该成功了");
            }
            case "addflag"->{
                if (strings.length < 2) {
                    commandSender.sendMessage("参数不足");
                    return true;
                }
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    commandSender.sendMessage("当前岛屿未被领取");
                    return true;
                }
                currentIsland.addIslandFlag(strings[1]);
                commandSender.sendMessage("添加成功");
            }
            case "removeflag"->{
                if (strings.length < 2) {
                    commandSender.sendMessage("参数不足");
                    return true;
                }
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    commandSender.sendMessage("当前岛屿未被领取");
                    return true;
                }

                if (currentIsland.containsFlag(strings[1])) {
                    currentIsland.removeIslandFlag(strings[1]);
                    commandSender.sendMessage("删除成功");
                }else{
                    commandSender.sendMessage("岛屿不包含该标记");
                }
            }
            case "list"->{
                if (strings.length < 2) {
                    commandSender.sendMessage("参数不足");
                    return true;
                }
                String target = strings[1];

                List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(target);


                MessageUtils.info(player, target + " 共有 " + playerIslands.size() + " 个岛屿");

                for (Island playerIsland : playerIslands) {
                    MessageUtils.info(player, " - " + playerIsland.getIslandId().toLocalString());
                }
            }

            case "delete" ->{
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    commandSender.sendMessage("当前岛屿未被领取");
                    return true;
                }

                IslandManager.INSTANCE.deleteIsland(currentIsland,()->{
                    commandSender.sendMessage("岛屿已被删除");

                });

            }
            case "import" ->{
                if (strings.length < 2) {
                    commandSender.sendMessage("参数不足");
                    return true;
                }
                try {
                    ConvertDao.importFromPlot(strings[1]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case "trim"->{
                int tobeDelete = 0;
                if (strings.length >= 2) {
                    tobeDelete = Integer.parseInt(strings[1]);
                }



                try {
                    HashSet<String> islandStringIds = new HashSet<>();
                    HashSet<String> wantDeleteIds = new HashSet<>();


                    Set<IslandId> localServerIslandIds = IslandDao.getLocalServerIslandIds(ServerInfoUpdater.getServerName());
                    for (IslandId localServerIslandId : localServerIslandIds) {
                        islandStringIds.add(localServerIslandId.getX() + "." + localServerIslandId.getZ());
                    }
                    File file = new File(IsletopiaTweakers.getWorld().getWorldFolder() + "/region/");
                    File[] files = file.listFiles((dir, name) -> {
                        if (name.matches("r\\.[0-9]+\\.[0-9]+\\.mca")) {
                            return true;
                        }
                        return false;
                    });

                    assert files != null;
                    for (File file1 : files) {
                        String[] split = file1.getName().split("\\.");
                        if (split.length < 3) {
                            continue;
                        }
                        int x = Integer.parseInt(split[1]);
                        int z = Integer.parseInt(split[2]);
                        if (!islandStringIds.contains(x + "." + z)) {
                            wantDeleteIds.add(x + "." + z);
                            if (tobeDelete < 0) {
                                commandSender.sendMessage(x + "." + z);
                            }
                        }
                    }

                    if (wantDeleteIds.size() == tobeDelete) {

                        //start delete
                        for (File file1 : files) {
                            String[] split = file1.getName().split("\\.");
                            if (split.length < 3) {
                                continue;
                            }
                            int x = Integer.parseInt(split[1]);
                            int z = Integer.parseInt(split[2]);
                            if (!islandStringIds.contains(x + "." + z)) {
                                file1.delete();
                            }
                        }
                    } else {
                        commandSender.sendMessage("total:" + wantDeleteIds.size());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    commandSender.sendMessage("Trim error!");
                }

            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
