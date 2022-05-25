package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.database.IslandDao;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
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

@BukkitCommand("isadmin")
public class IslandAdmin implements CommandExecutor, TabCompleter {
    public
    IslandAdmin() {
        Objects.requireNonNull(Bukkit.getPluginCommand("isadmin")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("isadmin")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("Args not enough");
            return true;
        }
        Player player = (Player) commandSender;

        switch (strings[0].toLowerCase(Locale.ROOT)) {
            case "setowner" -> {
                if (strings.length < 2) {

                    MessageUtils.fail(player,"Args not enough");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(player,"Not claimed");
                    return true;
                }
                UUID uuid = UUIDManager.get(strings[1]);
                if (uuid == null) {
                    MessageUtils.fail(player,"UUID not found");
                    return true;
                }
                currentIsland.setUuid(uuid);
                MessageUtils.success(player,"Success");
            }
            case "claim" -> {
                String serverName = ServerInfoUpdater.getServerName();
                IslandId islandId = IslandId.fromLocation(serverName, player.getLocation().getBlockX(), player.getLocation().getBlockZ());
                IslandManager.INSTANCE.createNewIsland(islandId, player.getUniqueId(), (island) -> {
                    if (island == null) {
                        MessageUtils.fail(player,"Unexpected failed");
                    } else {
                        island.tp(player);
                        MessageUtils.success(player,"Success");
                    }
                });

            }
            case "clear" -> {
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(player,"Not claimed");
                    break;
                }
                MessageUtils.fail(player,"Starting clear..");
                currentIsland.clearAndApplyNewIsland(() -> MessageUtils.success(player,"Done!"), 60);

            }

            case "add" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(player,"Args not enough");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(player,"Not claimed");
                    return true;
                }
                String string = strings[1];


                Tasks.INSTANCE.async(()-> {
                    UUID uuid = UUIDManager.get(string);//checked
                    currentIsland.addMember(uuid);
                    MessageUtils.success(player,"Success");
                });


            }
            case "remove" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(player,"Args not enough");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(player,"Not claimed");
                    return true;
                }
                UUID uuid = UUIDManager.get(strings[1]);
                currentIsland.removeMember(uuid);

                MessageUtils.success(player,"Success");
            }
            case "addflag" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(player,"Args not enough");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(player,"Not claimed");
                    return true;
                }
                currentIsland.addIslandFlag(strings[1]);
                MessageUtils.success(player,"Success");
            }
            case "removeflag" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(player,"Args not enough");
                    return true;
                }
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(player,"Not claimed");
                    return true;
                }

                if (currentIsland.containsFlag(strings[1])) {
                    currentIsland.removeIslandFlag(strings[1]);
                    MessageUtils.success(player,"Success");
                } else {
                    MessageUtils.fail(player,"Island doesn't contain such flag");
                }
            }
            case "list" -> {
                if (strings.length < 2) {
                    MessageUtils.fail(player,"Args not enough");
                    return true;
                }
                String target = strings[1];

                List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(UUIDManager.get(target));


                MessageUtils.info(player, target + " has " + playerIslands.size() + " islands");

                for (Island playerIsland : playerIslands) {
                    MessageUtils.info(player, " - " + playerIsland.getIslandId().toLocalString());
                }
            }

            case "delete" -> {
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    MessageUtils.fail(player,"Not claimed");
                    return true;
                }

                IslandManager.INSTANCE.deleteIsland(currentIsland, () -> MessageUtils.success(player,"Done!"));

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
                    File file = new File(Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).getWorldFolder() + "/region/");
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
