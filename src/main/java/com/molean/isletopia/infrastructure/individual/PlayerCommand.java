package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerCommand implements TabCompleter, CommandExecutor {
    public PlayerCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("player")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("player")).setTabCompleter(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            return true;
        }
        Tasks.INSTANCE.async(() -> {
            UUID uuid = UUIDManager.get(args[0]);
            if (uuid != null) {
                new PlayerMenu(player,uuid).open();
            }
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>(UUIDManager.INSTANCE.getSnapshot().values());
            playerNames.removeIf(s -> !s.toLowerCase().startsWith(args[0].toLowerCase()));
            return playerNames;
        }
        return new ArrayList<>();
    }

}
