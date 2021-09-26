package com.molean.isletopia.island;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.flag.SpectatorVisitor;
import com.molean.isletopia.menu.VisitorMenu;
import com.molean.isletopia.menu.charge.PlayerChargeMenu;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.menu.settings.biome.LocalBiome;
import com.molean.isletopia.menu.visit.VisitMenu;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.other.ConfirmDialog;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class IslandCommand implements CommandExecutor, TabCompleter {
    public IslandCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("is")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("is")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("island")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("island")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("islet")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("islet")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("isletopia")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("isletopia")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player sourcePlayer = (Player) sender;
        String subject = sourcePlayer.getName();
        String verb;
        String object = null;

        if (args.length == 0) {
            home(subject);
            return true;
        }

        verb = args[0].toLowerCase();

        if (args.length > 1) {
            object = args[1];
        }

        switch (verb) {

            case "home":
                home(subject);
                break;
            case "visits":
                visits(subject);
                break;
            case "name":
                if (args.length < 2) {
                    name(subject);
                } else {
                    name(subject, object);
                }

                break;
            case "visit":
            case "tp":
                if (args.length < 2) {
                    help(subject);
                    return true;
                }
                visit(subject, object);
                break;
            case "trust":
            case "invite":
                if (args.length < 2) {
                    help(subject);
                    return true;
                }
                trust(subject, object);
                break;
            case "kick":
            case "distrust":
                if (args.length < 2) {
                    help(subject);
                    return true;
                }
                distrust(subject, object);
                break;
            case "lock":
                lock(subject);
                break;
            case "unlock":
            case "open":
                unlock(subject);
                break;

            default:
            case "help":
                help(subject);
                break;

            case "spectatorvisitor":
                spectatorVisitor(subject);
                break;

            case "sethome":
                setHome(subject);
                break;
            case "resethome":
                resetHome(subject);
                break;
            case "setbiome":
                setBiome(subject);
                break;
            case "info":
                info(subject);
                break;
            case "consume":
                consume(subject);
                break;
            case "trusts":
                trusts(subject);
                break;
            case "visitors":
                visitors(subject);
                break;
            case "stars":
                stars(subject);
                break;
            case "star":
                if (args.length < 2) {
                    help(subject);
                    return true;
                }
                star(subject,object);
                break;
            case "unstar":
                if (args.length < 2) {
                    help(subject);
                    return true;
                }
                unstar(subject,object);
                break;
        }
        return true;
    }

    private void spectatorVisitor(String subject) {
        Player player = Bukkit.getPlayer(subject);
        assert player != null;

        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }

        if (currentIsland.containsFlag("SpectatorVisitor")) {
            currentIsland.removeIslandFlag("SpectatorVisitor");
            MessageUtils.success(player, "已取消访客旁观模式.");
        } else {
            currentIsland.addIslandFlag("SpectatorVisitor");
            MessageUtils.success(player, "已设置访客旁观模式.");
        }

    }

    public static void visitors(String subject) {
        Player player = Bukkit.getPlayer(subject);
        assert player != null;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            new VisitorMenu(player, 0).open();
        });
    }

    public static void consume(String subject) {
        Player player = Bukkit.getPlayer(subject);
        assert player != null;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            new PlayerChargeMenu(player).open();
        });
    }

    public static void info(String subject) {
        Player player = Bukkit.getPlayer(subject);
        assert player != null;
        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            player.sendMessage("当前岛屿尚未被领取");
            return;
        }
        player.sendMessage("===========start===========");
        String localServerName = IsletopiaTweakersUtils.getLocalServerName();
        player.sendMessage("岛屿坐标:" + localServerName + ":" + currentIsland.getX() + "," + currentIsland.getZ());
        player.sendMessage("岛屿主人:" + currentIsland.getOwner());
        List<String> members = currentIsland.getMembers();
        if (members.isEmpty()) {
            player.sendMessage("岛屿成员:" + "无");
        } else {
            player.sendMessage("岛屿成员:");
            for (String member : members) {
                player.sendMessage(" - " + member);
            }
        }

        LocalBiome localBiome = null;
        try {
            localBiome = LocalBiome.valueOf(currentIsland.getBiome().name());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (localBiome == null) {
            player.sendMessage("岛屿群系:" + "未知");
        } else {
            player.sendMessage("岛屿群系:" + localBiome.getName());
        }
        Set<String> islandFlags = currentIsland.getIslandFlags();
        if (islandFlags.isEmpty()) {
            player.sendMessage("岛屿标记:无");
        } else {
            player.sendMessage("岛屿标记:");
            for (String islandFlag : islandFlags) {
                player.sendMessage(" - " + islandFlag);
            }
        }

        Timestamp creation = currentIsland.getCreation();
        LocalDateTime localDateTime = creation.toLocalDateTime();
        String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
        player.sendMessage("创建时间:" + format);

        player.sendMessage("===========end===========");
    }


    private static void name(String subject) {
        name(subject, null);
    }

    private static void name(String subject, String object) {
        Player player = Bukkit.getPlayer(subject);
        assert player != null;

        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        currentIsland.setName(object);
        MessageUtils.success(player, "设置成功.");
    }


    public static void home(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        visit(source, source);
    }

    public static void setHome(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;

        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }

        Location bottomLocation = currentIsland.getBottomLocation();
        currentIsland.setSpawnX(player.getLocation().getX() - bottomLocation.getX());
        currentIsland.setSpawnY(player.getLocation().getY() - bottomLocation.getY());
        currentIsland.setSpawnZ(player.getLocation().getZ() - bottomLocation.getZ());
        currentIsland.setYaw(player.getLocation().getYaw());
        currentIsland.setPitch(player.getLocation().getPitch());

        MessageUtils.success(player, "成功更改重生位置.");
    }

    public static void resetHome(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;

        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }

        currentIsland.setSpawnX(256);
        currentIsland.setSpawnZ(256);
        currentIsland.setSpawnY(128);
        currentIsland.setYaw(0);
        currentIsland.setPitch(0);

        MessageUtils.success(player, "成功更改重生位置.");
    }

    public static void setBiome(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;

        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(),
                () -> new BiomeMenu(player).open());
    }

    private static void visit(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        IsletopiaTweakersUtils.universalPlotVisitByMessage(player, target, 0);
    }

    public static void trust(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        if (currentIsland.getMembers().contains(target) || currentIsland.getOwner().equals(target)) {
            MessageUtils.success(player, "无效,对方已经在你的信任列表中.");
            return;
        }
        if (!target.matches("[a-zA-Z0-9_]{3,16}")) {
            MessageUtils.success(player, "无效,该用户名不合法.");
            return;
        }
        int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(target);
        if (playerIslandCount == 0) {
            MessageUtils.success(player, "无效, 对方未在梦幻之屿注册.");
            return;
        }

        new ConfirmDialog(Component.text("""
                添加岛屿成员后，你的岛员将能够随意破坏你的岛屿。
                请不要随意乱加岛员，如果因为乱给权限导致岛屿被破坏，所有后果自行承担，服务器将不给予任何帮助。
                """)).accept(player1 -> {
            currentIsland.addMember(target);
            MessageUtils.success(player1, "已经添加 " + target + " 为信任.");
        }).open(player);

    }

    public static void distrust(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        if (currentIsland.getMembers().contains(target)) {
            currentIsland.removeMember(target);
            MessageUtils.success(player, "已经取消对 " + target + " 的信任.");
        } else {
            MessageUtils.success(player, "你没有添加 " + target + " 为信任.");
        }
    }

    public static void lock(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;

        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        currentIsland.addIslandFlag("Lock");
        MessageUtils.success(player, "已将岛屿设置为§c锁定§6, 非成员玩家将无法访问.");
    }

    public static void unlock(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;

        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        currentIsland.removeIslandFlag("Lock");
        MessageUtils.success(player, "已将岛屿设置为§c开放§6, 所有玩家都可以访问.");
    }


    public static void trusts(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getOwner().equals(player.getName()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行操作.");
            return;
        }
        List<String> members = currentIsland.getMembers();

        if (members.isEmpty()) {
            MessageUtils.info(player, "你的岛屿没有成员");
        } else {
            MessageUtils.info(player, "你的岛屿成员列表如下:");
            for (String member : members) {
                MessageUtils.info(player, " - " + member);
            }
        }
    }

    public static void visits(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            new VisitMenu(player, ServerInfoUpdater.getOnlinePlayers(), 0).open();
        });
    }


    public static void star(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        //check player exist
        int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(target);
        if (playerIslandCount == 0) {
            MessageUtils.fail(player, "失败, "+target + " 未在梦幻之屿注册!");
            return;
        }
        List<String> collection = UniversalParameter.getParameterAsList(player.getName(), "collection");
        if (collection.contains(target)) {
            MessageUtils.fail(player, "失败, " + target + " 已在你的收藏列表中了!");
            return;
        }
        UniversalParameter.addParameter(player.getName(), "collection", target);
        MessageUtils.success(player, "成功, " + target + " 已添加到你收藏列表!");
    }

    public static void unstar(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        List<String> collection = UniversalParameter.getParameterAsList(player.getName(), "collection");
        if (!collection.contains(target)) {
            MessageUtils.fail(player, "失败, " + target + " 不在你的收藏列表中!");
            return;
        }
        UniversalParameter.removeParameter(player.getName(), "collection", target);
        MessageUtils.success(player, "成功, " + target + " 已从你的收藏列表中删除!");
    }

    public static void stars(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        List<String> collection = UniversalParameter.getParameterAsList(player.getName(), "collection");
        if (collection.isEmpty()) {
            MessageUtils.info(player, "你的收藏夹是空的!");
        } else {
            MessageUtils.info(player, "你的收藏夹里有这些玩家:");
            for (String member : collection) {
                MessageUtils.info(player, " - " + member);
            }
        }
    }

    public void help(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        player.sendMessage("§7§m§l----------§b梦幻之屿§7§m§l----------");
        player.sendMessage("§e> 快速回家 /is home");
        player.sendMessage("§e> 闭关锁岛 /is lock");
        player.sendMessage("§e> 开放岛屿 /is unlock");
        player.sendMessage("§e> 打开访问菜单 /is visits");
        player.sendMessage("§e> 访问某人岛屿 /is visit [玩家]");
        player.sendMessage("§7§m§l---");
        player.sendMessage("§e> 查看岛屿成员 /is trusts");
        player.sendMessage("§e> 添加岛屿成员 /is trust [玩家]");
        player.sendMessage("§e> 删除岛屿成员 /is distrust [玩家]");
        player.sendMessage("§e> 查看近期访客 /is visitors");
        player.sendMessage("§e> 修改复活位置 /is setHome");
        player.sendMessage("§e> 重置复活位置 /is resetHome");
        player.sendMessage("§e> 修改生物群系 /is setBiome");
        player.sendMessage("§e> 缴纳水电费用 /is consume");
        player.sendMessage("§7§m§l---");
        player.sendMessage("§e> 查看收藏列表 /is stars");
        player.sendMessage("§e> 添加收藏列表 /is star [玩家]");
        player.sendMessage("§e> 删除收藏列表 /is unstar [玩家]");
        player.sendMessage("§7§m§l--------------------------");

    }

    private static final List<String> subCommand = List.of("home", "visit", "trust", "distrust", "help", "invite",
            "kick", "lock", "unlock", "setHome", "resetHome",
            "visits","trusts","visitors","consume","stars","star","unstar","spectatorVisitor","setBiome");

    private static final List<String> playerCommand = List.of("trust", "distrust",
            "kick", "invite", "visit","star","unstar");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> strings = new ArrayList<>();
        if (args.length == 1) {
            for (String s : subCommand) {
                if (s.startsWith(args[0])) {
                    strings.add(s);
                }
            }
        }
        if (args.length == 2) {
            if (playerCommand.contains(args[0])) {
                List<String> onlinePlayers = ServerInfoUpdater.getOnlinePlayers();
                for (String onlinePlayer : onlinePlayers) {
                    if (onlinePlayer.toLowerCase().startsWith(args[1].toLowerCase())) {
                        strings.add(onlinePlayer);
                    }
                }
            }
        }
        return strings;
    }
}
