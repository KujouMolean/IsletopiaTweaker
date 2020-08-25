package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.parameter.UniversalParameter;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
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
        String verb = null;
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
            case "visit":
                if (args.length < 2) {
                    return true;
                }
                visit(subject, object);
                break;
            case "trust":
                if (args.length < 2) {
                    return true;
                }
                trust(subject, object);
                break;
            case "untrust":
                if (args.length < 2) {
                    return true;
                }
                untrust(subject, object);
                break;
            case "denyall":
                denyall(subject);
                break;
            case "undenyall":
                undenyall(subject);
                break;
            case "help":
                help(subject);
                break;
        }

        return true;
    }


    public void home(String source) {
        Player player = Bukkit.getPlayer(source);
        if (player != null) {
            Bukkit.dispatchCommand(player, "visit " + source);
        }
    }

    private void visit(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        Bukkit.dispatchCommand(player, "visit " + target);
    }

    public void trust(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        Bukkit.dispatchCommand(player, "plot trust " + target);
    }

    public void untrust(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        Bukkit.dispatchCommand(player, "plot remove " + target);
    }

    public void denyall(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        Bukkit.dispatchCommand(player, "plot deny *");
    }

    public void undenyall(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        String server = UniversalParameter.getParameter(source, "server");
        if (server == null) {
            player.kickPlayer("你的岛屿值为空, 这是一个严重的错误.");
            return;
        }
        List<UUID> denied = PlotDao.getDenied(server, source);
        if (!denied.contains(PlotDao.getAllUUID())) {
            return;
        }
        Bukkit.dispatchCommand(player, "plot remove *");
    }

    public void help(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        player.sendMessage("§7§m§l----------§b梦幻之屿§7§m§l----------\n" +
                "§e> 快速回城 /is home\n" +
                "§e> 访问他人 /is visit [玩家]\n" +
                "§e> 给予权限 /is trust [玩家]\n" +
                "§e> 闭关锁岛 /is denyall\n" +
                "§e> 开放岛屿 /is undenyall\n" +
                "§7§m§l--------------------------");
    }

    private static final List<String> subcmd = List.of("home", "visit", "trust", "denyall", "undenyall", "help");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> strings = new ArrayList<>();
        if (args.length == 1) {
            for (String s : subcmd) {
                if (s.startsWith(args[0])) {
                    strings.add(s);
                }
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("trust") || args[0].equalsIgnoreCase("untrust")) {
                List<String> onlinePlayers = IsletopiaTweakers.getOnlinePlayers();
                for (String onlinePlayer : onlinePlayers) {
                    if (args[1].startsWith(onlinePlayer)) {
                        strings.add(onlinePlayer);
                    }
                }
            }
        }
        return strings;
    }
}
