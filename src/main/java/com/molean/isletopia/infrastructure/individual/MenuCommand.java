package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.menu.recipe.CraftRecipeMenu;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.menu.recipe.RecipeListMenu;
import com.molean.isletopia.task.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuCommand implements CommandExecutor, TabCompleter {
    public MenuCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("menu")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("menu")).setTabCompleter(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0) {
            Tasks.INSTANCE.async(() -> new MainMenu(player).open());
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("recipe")) {
                Tasks.INSTANCE.async(() -> new RecipeListMenu(player).open());

            }
            if (args[0].equalsIgnoreCase("close")) {
                player.closeInventory();
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("recipe")) {
                for (LocalRecipe localRecipe : LocalRecipe.localRecipeList) {
                    for (ItemStack result : localRecipe.results) {
                        if (result.getType().name().equalsIgnoreCase(args[1])) {
                            Tasks.INSTANCE.async(() -> new CraftRecipeMenu(player, localRecipe).open());
                        }
                    }

                }

            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
