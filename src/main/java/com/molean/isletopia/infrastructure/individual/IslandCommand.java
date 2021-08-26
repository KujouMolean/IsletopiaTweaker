package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.menu.settings.member.MemberMenu;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.other.ConfirmDialog;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.molean.isletopia.utils.UUIDUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.BlockLoc;
import com.plotsquared.core.plot.Plot;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
            case "sethome":
                setHome(subject);
                break;
            case "resethome":
                resetHome(subject);
                break;
            case "setbiome":
                setBiome(subject);
                break;
        }
        return true;
    }

    private void name(String subject) {
        Player player = Bukkit.getPlayer(subject);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage("§8[§3岛屿助手§8] §c阁下只能对自己的岛屿进行设置.");
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        assert currentPlot != null;
        currentPlot.setAlias("");
        player.sendMessage("§8[§3岛屿助手§8] §c设置成功.");
    }

    private void name(String subject, String object) {
        Player player = Bukkit.getPlayer(subject);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage("§8[§3岛屿助手§8] §c阁下只能对自己的岛屿进行设置.");
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        assert currentPlot != null;
        currentPlot.setAlias(object);
        player.sendMessage("§8[§3岛屿助手§8] §c设置成功.");
    }


    public void home(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        visit(source, source);
    }

    public void setHome(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage("§8[§3岛屿助手§8] §c阁下只能对自己的岛屿进行设置.");
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        Location location = player.getLocation();
        com.plotsquared.core.location.Location bottomAbs = currentPlot.getBottomAbs();

        currentPlot.setHome(new BlockLoc(
                location.getBlockX() - bottomAbs.getX(),
                location.getBlockY() - bottomAbs.getY(),
                location.getBlockZ() - bottomAbs.getZ(),
                location.getYaw(),
                location.getPitch()));
        player.sendMessage("§8[§3岛屿助手§8] §c设置成功.");
    }

    public void resetHome(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage("§8[§3岛屿助手§8] §c阁下只能对自己的岛屿进行设置.");
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        currentPlot.setHome(null);
        player.sendMessage("§8[§3岛屿助手§8] §6成功更改重生位置.");
    }

    public void setBiome(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage("§8[§3岛屿助手§8] §c阁下只能对自己的岛屿进行设置.");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(),
                () -> new BiomeMenu(player).open());
    }

    private void visit(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        IsletopiaTweakersUtils.universalPlotVisitByMessage(player, target);
    }

    public void trust(String source, String target) {
        Player player = Bukkit.getPlayer(source);


        assert player != null;
        if (!"true".equalsIgnoreCase(UniversalParameter.getParameter(player.getName(), "MemberConfirm"))) {
            new ConfirmDialog(Component.text("添加岛屿成员后，你的岛员将能够随意破坏你的岛屿。\n" +
                    "请不要随意乱加岛员，如果因为乱给权限导致岛屿被破坏，服务器将不给予任何帮助。\n" +
                    "(此消息确认一次后不再出现)" +
                    "\n\n\n\n\n")).accept(player1 -> {
                UniversalParameter.setParameter(player1.getName(), "MemberConfirm", "true");
                trust(source, target);
            }).open(player);
            return;
        }

        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage("§8[§3岛屿助手§8] §c阁下只能对自己的岛屿进行设置.");
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        UUID uuid = UUIDUtils.get(target);
        assert currentPlot != null;
        currentPlot.addTrusted(uuid);
        PlotSquared.get().getImpromptuUUIDPipeline().storeImmediately(target, uuid);
        player.sendMessage("§8[§3岛屿助手§8] §6已经添加 " + target + " 为信任.");
    }

    public void distrust(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage("§8[§3岛屿助手§8] §c阁下只能对自己的岛屿进行设置.");
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        UUID uuid = UUIDUtils.get(target);
        assert currentPlot != null;
        currentPlot.removeTrusted(uuid);
        player.sendMessage("§8[§3岛屿助手§8] §6已经从信任列表中删除 %1%.".replace("%1%", target));
    }

    public void lock(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage("§8[§3岛屿助手§8] §c阁下只能对自己的岛屿进行设置.");

            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        UUID uuid = PlotDao.getAllUUID();
        currentPlot.addDenied(uuid);
        player.sendMessage("§8[§3岛屿助手§8] §6已将岛屿设置为§c锁定§6, 非成员玩家将无法访问.");
    }

    public void unlock(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage("§8[§3岛屿助手§8] §c阁下只能对自己的岛屿进行设置.");
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        UUID uuid = PlotDao.getAllUUID();
        currentPlot.removeDenied(uuid);
        player.sendMessage("§8[§3岛屿助手§8] §6已将岛屿设置为§c开放§6, 所有玩家都可以访问.");
    }

    public void help(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        player.sendMessage("§7§m§l----------§b梦幻之屿§7§m§l----------");
        player.sendMessage("§e> 快速回家 /is home");
        player.sendMessage("§e> 访问他人 /is visit [玩家]");
        player.sendMessage("§e> 给予权限 /is trust [玩家]");
        player.sendMessage("§e> 取消权限 /is distrust [玩家]");
        player.sendMessage("§e> 闭关锁岛 /is lock");
        player.sendMessage("§e> 开放岛屿 /is unlock");
        player.sendMessage("§e> 修改复活位置 /is setHome");
        player.sendMessage("§e> 重置复活位置 /is resetHome");
        player.sendMessage("§e> 修改生物群系 /is setBiome");
        player.sendMessage("§7§m§l--------------------------");

    }

    private static final List<String> subCommand = List.of("home", "visit", "trust", "distrust", "help", "invite", "kick", "lock", "unlock", "setHome", "resetHome");
    private static final List<String> playerCommand = List.of("trust", "distrust", "kick", "invite", "visit");

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
