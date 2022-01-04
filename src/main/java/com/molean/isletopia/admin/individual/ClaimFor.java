package com.molean.isletopia.admin.individual;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.database.UUIDDao;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.service.UniversalParameter;
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

import java.util.*;

public class ClaimFor implements CommandExecutor, TabCompleter {
    public ClaimFor() {
        Objects.requireNonNull(Bukkit.getPluginCommand("claim")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("claim")).setTabCompleter(this);
    }

    @SuppressWarnings("all")
    private final boolean disable= true;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] strings) {

        if (disable) {
            MessageUtils.info(commandSender, "出于安全考虑该指令已被永久禁用");
            MessageUtils.info(commandSender, "如需建造超大规模建筑群，请另择服务器");
            return true;
        }

        if (strings.length != 3 || !strings[1].equalsIgnoreCase("for")) {
            MessageUtils.info(commandSender, "为一位未注册的玩家预选岛屿为你的邻居");
            MessageUtils.info(commandSender, " - 指令: /claim 方向 for 玩家ID");
            MessageUtils.info(commandSender, " - 方向: North/South/East/West");
            MessageUtils.info(commandSender, " - 例如: /claim North for Molean");
            return true;
        }


        String target = strings[2];
        String direction = strings[0].toLowerCase(Locale.ROOT);

        Player player = (Player) commandSender;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !currentIsland.getUuid().equals(player.getUniqueId())) {
            MessageUtils.fail(player, "你必须在自己岛上执行该命令!");
            return true;
        }


        IslandId islandId = currentIsland.getIslandId();

        int x = islandId.getX();
        int z = islandId.getZ();

        switch (direction) {
            case "north" -> {
                z = z - 1;
            }
            case "south" -> {
                z = z + 1;

            }
            case "west" -> {
                x = x - 1;

            }
            case "east" -> {
                x = x + 1;

            }
            case "default" -> {
                MessageUtils.fail(commandSender, "失败，你指定的方向不存在！");
                return true;
            }
        }
        IslandId newIslandId = new IslandId(islandId.getServer(), x, z);
        if (IslandManager.INSTANCE.getLocalIsland(newIslandId) != null) {
            MessageUtils.fail(commandSender, "失败，该方向岛屿已被领取！");
            return true;
        }

        UUID targetUUID;
        if (target.startsWith("#")) {
            if (!target.substring(1).matches("[a-zA-Z0-9_]{3,16}")) {
                MessageUtils.fail(commandSender, "失败，用户名不合法！");
                return true;
            }

            targetUUID = UUIDUtils.getOffline(target);
            if (UUIDDao.query(targetUUID) != null) {
                UUIDDao.update(targetUUID, target);
            } else {
                UUIDDao.insert(targetUUID, target);
            }
        } else {
            UUID uuidInDB = UUIDUtils.get(target);
            if (uuidInDB == null) {
                UUIDUtils.getOnline(target, uuid -> {
                    if (uuid == null) {
                        MessageUtils.fail(commandSender, "失败，你输入了正版ID，但该正版ID未注册。");
                        return;
                    }
                    if (UUIDDao.query(uuid) != null) {
                        UUIDDao.update(uuid, target);
                    } else {
                        UUIDDao.insert(uuid, target);
                    }
                    MessageUtils.info(commandSender, "注意，你指定了一个正版ID，如果确认无误请再输入一次指令。");
                });
                return true;
            } else {
                targetUUID = uuidInDB;
            }
        }
        if (IslandManager.INSTANCE.getPlayerIslandCount(targetUUID) > 0) {
            MessageUtils.fail(commandSender, "失败，该ID已有岛屿！");
            return true;
        }
        UniversalParameter.setParameter(targetUUID, "server", newIslandId.getServer());
        IslandManager.INSTANCE.createNewIsland(newIslandId, targetUUID, (newIsland) -> {
            if (newIsland == null) {
                MessageUtils.fail(commandSender, "失败，未知错误请联系管理员！");
            } else {
                MessageUtils.success(commandSender, "操作成功，该玩家加入服务器将会成为你的邻居！");
                MessageUtils.notify(commandSender, "请勿在两岛交界处建造生物农场规避生物上限，否则你会被封禁！");
                UniversalParameter.setParameter(targetUUID, "ManualClaim", "true");
            }
        });


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 1) {
            strings.add("north");
            strings.add("south");
            strings.add("east");
            strings.add("west");
            return strings;
        }
        if (args.length == 2) {
            strings.add("for");
            return strings;
        }
        return strings;
    }
}
