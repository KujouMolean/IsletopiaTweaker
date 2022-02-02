package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.database.IslandDao;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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

                    MessageUtils.fail(commandSender,"参数不足");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(commandSender,"当前岛屿未被领取");
                    return true;
                }
                UUID uuid = UUIDUtils.get(strings[1]);
                if (uuid == null) {
                    MessageUtils.fail(commandSender,"失败,未找到uuid");
                    return true;
                }
                currentIsland.setUuid(uuid);
                MessageUtils.success(commandSender,"应该成功了");
            }
            case "claim" -> {
                String serverName = ServerInfoUpdater.getServerName();
                IslandId islandId = IslandId.fromLocation(serverName, player.getLocation().getBlockX(), player.getLocation().getBlockZ());
                IslandManager.INSTANCE.createNewIsland(islandId, player.getUniqueId(), (island) -> {
                    if (island == null) {
                        MessageUtils.fail(commandSender,"领取失败");
                    } else {
                        island.tp(player);
                        MessageUtils.success(commandSender,"应该成功了");
                    }
                });

            }
            case "clear" -> {
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(commandSender,"岛屿尚未被领取");
                    break;
                }
                MessageUtils.fail(commandSender,"开始清空岛屿..");
                currentIsland.clearAndApplyNewIsland(() -> {
                    MessageUtils.success(commandSender,"清空成功!");
                }, 60);

            }

            case "add" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(commandSender,"参数不足");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(commandSender,"当前岛屿未被领取");
                    return true;
                }
                String string = strings[1];


                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    UUID uuid = UUIDUtils.get(string);//checked
                    currentIsland.addMember(uuid);
                    MessageUtils.success(commandSender,"应该成功了");
                });


            }
            case "remove" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(commandSender,"参数不足");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(commandSender,"当前岛屿未被领取");
                    return true;
                }
                UUID uuid = UUIDUtils.get(strings[1]);
                currentIsland.removeMember(uuid);

                MessageUtils.success(commandSender,"应该成功了");
            }
            case "addflag" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(commandSender,"参数不足");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(commandSender,"当前岛屿未被领取");
                    return true;
                }
                currentIsland.addIslandFlag(strings[1]);
                MessageUtils.success(commandSender,"添加成功");
            }
            case "removeflag" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(commandSender,"参数不足");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(commandSender,"当前岛屿未被领取");
                    return true;
                }

                if (currentIsland.containsFlag(strings[1])) {
                    currentIsland.removeIslandFlag(strings[1]);
                    MessageUtils.success(commandSender,"删除成功");
                } else {
                    MessageUtils.fail(commandSender,"岛屿不包含该标记");
                }
            }
            case "list" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(commandSender,"参数不足");
                    return true;
                }
                String target = strings[1];

                List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(UUIDUtils.get(target));


                MessageUtils.info(player, target + " 共有 " + playerIslands.size() + " 个岛屿");

                for (Island playerIsland : playerIslands) {
                    MessageUtils.info(player, " - " + playerIsland.getIslandId().toLocalString());
                }
            }

            case "delete" -> {
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(commandSender,"当前岛屿未被领取");
                    return true;
                }

                IslandManager.INSTANCE.deleteIsland(currentIsland, () -> {
                    MessageUtils.success(commandSender,"岛屿已被删除");
                });

            }

            case "trim" -> {
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
                    File[] files = file.listFiles((dir, name) -> name.matches("r\\.[0-9]+\\.[0-9]+\\.mca"));

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
