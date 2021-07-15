package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.utils.BukkitBungeeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IgnoreCommand implements CommandExecutor, TabCompleter, Listener {
    public IgnoreCommand() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("ignore")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("ignore")).setTabCompleter(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            List<String> ignores = UniversalParameter.getParameterAsList(event.getPlayer().getName(), "ignores");
            BukkitBungeeUtils.updateIgnores(event.getPlayer(), ignores);
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage("/ignore [玩家名称/qq号码] 屏蔽某个玩家的发言");
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            UniversalParameter.addParameter(player.getName(), "ignores", args[0]);
            List<String> ignores = UniversalParameter.getParameterAsList(player.getName(), "ignores");
            BukkitBungeeUtils.updateIgnores(player, ignores);
            player.sendMessage("已屏蔽 " + args[0] + " 的发言");
        });
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
