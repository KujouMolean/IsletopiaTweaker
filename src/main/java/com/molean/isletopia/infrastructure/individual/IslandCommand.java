package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.BlockLoc;
import com.plotsquared.core.plot.Plot;
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


    public void home(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        visit(source, source);
    }

    public void setHome(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage(I18n.getMessage("error.island.non-owner", player));
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
        player.sendMessage("设置成功");

    }

    public void resetHome(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage(I18n.getMessage("error.island.non-owner", player));
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        currentPlot.setHome(null);
        player.sendMessage(I18n.getMessage("island.setHome", player));
    }

    public void setBiome(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage(I18n.getMessage("error.island.non-owner", player));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            new BiomeMenu(player).open();
        });
    }

    private void visit(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        player.performCommand("visit " + target);
    }

    public void trust(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage(I18n.getMessage("error.island.non-owner", player));
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        UUID uuid = ServerInfoUpdater.getUUID(target);
        currentPlot.addTrusted(uuid);
        PlotSquared.get().getImpromptuUUIDPipeline().storeImmediately(target, uuid);
        player.sendMessage(I18n.getMessage("island.addTrust", player).replace("%1%", target));
    }

    public void distrust(String source, String target) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage(I18n.getMessage("error.island.non-owner", player));
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        UUID uuid = ServerInfoUpdater.getUUID(target);
        currentPlot.removeTrusted(uuid);
        player.sendMessage(I18n.getMessage("island.removeTrust", player).replace("%1%", target));
    }

    public void lock(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage(I18n.getMessage("error.island.non-owner", player));
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        UUID uuid = PlotDao.getAllUUID();
        currentPlot.addTrusted(uuid);
        player.sendMessage(I18n.getMessage("island.lock", player));
    }

    public void unlock(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        if (!PlotUtils.isCurrentPlotOwner(player)) {
            player.sendMessage(I18n.getMessage("error.island.non-owner", player));
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        UUID uuid = PlotDao.getAllUUID();
        currentPlot.removeTrusted(uuid);
        player.sendMessage(I18n.getMessage("isand.unlock", player));
    }

    public void help(String source) {
        Player player = Bukkit.getPlayer(source);
        assert player != null;
        player.sendMessage(I18n.getMessage("island.help.1", player));
        player.sendMessage(I18n.getMessage("island.help.2", player));
        player.sendMessage(I18n.getMessage("island.help.3", player));
        player.sendMessage(I18n.getMessage("island.help.4", player));
        player.sendMessage(I18n.getMessage("island.help.5", player));
        player.sendMessage(I18n.getMessage("island.help.6", player));
        player.sendMessage(I18n.getMessage("island.help.7", player));
        player.sendMessage(I18n.getMessage("island.help.8", player));
        player.sendMessage(I18n.getMessage("island.help.9", player));
        player.sendMessage(I18n.getMessage("island.help.10", player));
        player.sendMessage(I18n.getMessage("island.help.11", player));

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
