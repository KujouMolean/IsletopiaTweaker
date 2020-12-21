package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.menu.recipe.RecipeListMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MenuCommand implements CommandExecutor, TabCompleter {
    public MenuCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("menu")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("menu")).setTabCompleter(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerMenu(player).open());
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("recipe")) {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    new RecipeListMenu(player, "guide 新合成表").open();
                });
            }
            if (args[0].equalsIgnoreCase("close")) {
                player.closeInventory();
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
