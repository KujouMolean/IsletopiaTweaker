package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.utils.BungeeUtils;
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

public class UnignoreCommand implements CommandExecutor, TabCompleter, Listener {
    public UnignoreCommand() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("unignore")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("unignore")).setTabCompleter(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String ignores = UniversalParameter.getParameter(event.getPlayer().getName(), "ignores");
            BungeeUtils.updateIgnores(event.getPlayer(), ignores);
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage("/unignore [玩家名称/qq号码] 取消屏蔽某个玩家的发言");
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (UniversalParameter.getParameterAsList(player.getName(), "ignores").contains(args[0])) {
                UniversalParameter.removeParameter(player.getName(), "ignores", args[0]);
                String ignores = UniversalParameter.getParameter(player.getName(), "ignores");
                BungeeUtils.updateIgnores(player, ignores);
                player.sendMessage("取消对 " + args[0] + " 的屏蔽");
            } else {
                player.sendMessage("你没有屏蔽该玩家");
            }
        });
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
