package com.molean.isletopia.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.database.IslandDao;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

@CommandAlias("isa|isAdmin")
@Singleton
@CommandPermission("isletopia.admin")
public class IslandAdmin extends BaseCommand {
    @Subcommand("setOwner")
    public void setOwner(Player player, String owner) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            MessageUtils.fail(player, "Not claimed");
            return;
        }
        UUID uuid = UUIDManager.get(owner);
        if (uuid == null) {
            MessageUtils.fail(player, "UUID not found");
            return;
        }

        currentIsland.setUuid(uuid);
        MessageUtils.success(player, "Success");
    }


    @Subcommand("claim")
    public void claim(Player player) {
        String serverName = ServerInfoUpdater.getServerName();
        IslandId islandId = IslandId.fromLocation(serverName, player.getLocation().getBlockX(), player.getLocation().getBlockZ());
        IslandManager.INSTANCE.createNewIsland(islandId, player.getUniqueId(), (island) -> {
            if (island == null) {
                MessageUtils.fail(player, "Unexpected failed");
            } else {
                island.tp(player);
                MessageUtils.success(player, "Success");
            }
        });

    }

    @Subcommand("clear")
    public void clear(Player player) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            MessageUtils.fail(player, "Not claimed");
            return;
        }
        MessageUtils.fail(player, "Starting clear..");
        currentIsland.clearAndApplyNewIsland(() -> MessageUtils.success(player, "Done!"), 60);

    }


    @Subcommand("add")
    public void add(Player player, String target) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            MessageUtils.fail(player, "Not claimed");
            return;
        }
        Tasks.INSTANCE.async(() -> {
            UUID uuid = UUIDManager.get(target);//checked
            currentIsland.addMember(uuid);
            MessageUtils.success(player, "Success");
        });
    }

    @Subcommand("remove")
    public void remove(Player player, String target) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            MessageUtils.fail(player, "Not claimed");
            return;
        }
        UUID uuid = UUIDManager.get(target);
        currentIsland.removeMember(uuid);

        MessageUtils.success(player, "Success");

    }


    @Subcommand("addFlag")
    public void addFlag(Player player, String flag) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            MessageUtils.fail(player, "Not claimed");
            return;
        }
        currentIsland.addIslandFlag(flag);
        MessageUtils.success(player, "Success");
    }

    @Subcommand("removeFlag")
    public void removeFlag(Player player, String flag) {

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            MessageUtils.fail(player, "Not claimed");
            return;
        }
        if (currentIsland.containsFlag(flag)) {
            currentIsland.removeIslandFlag(flag);

            MessageUtils.success(player, "Success");
        } else {
            MessageUtils.fail(player, "Island doesn't contain such flag");
        }
    }

    @Subcommand("list")
    public void list(Player player, String target) {

        List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(UUIDManager.get(target));
        MessageUtils.info(player, target + " has " + playerIslands.size() + " islands");
        for (Island playerIsland : playerIslands) {
            MessageUtils.info(player, " - " + playerIsland.getIslandId().toLocalString());
        }
    }

    @Subcommand("delete")
    public void delete(Player player) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            MessageUtils.fail(player, "Not claimed");
            return;
        }

        IslandManager.INSTANCE.deleteIsland(currentIsland, () -> MessageUtils.success(player, "Done!"));

    }

    @Subcommand("trim")
    public void trim(Player player, int tobeDelete) {
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
                        player.sendMessage(x + "." + z);
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
                player.sendMessage("total:" + wantDeleteIds.size());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage("Trim error!");
        }

    }
}
