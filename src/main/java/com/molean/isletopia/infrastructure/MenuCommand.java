package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.charge.ChargeDetailCommitter;
import com.molean.isletopia.infrastructure.individual.bars.SidebarManager;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.menu.recipe.CraftRecipeMenu;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.menu.recipe.RecipeListMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.task.Tasks;
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

@BukkitCommand("menu")
public class MenuCommand implements CommandExecutor, TabCompleter {

    private PlayerPropertyManager playerPropertyManager;
    private SidebarManager sidebarManager;
    private ChargeDetailCommitter chargeDetailCommitter;
    public MenuCommand(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeDetailCommitter chargeDetailCommitter) {
        this.playerPropertyManager = playerPropertyManager;
        this.sidebarManager = sidebarManager;
        this.chargeDetailCommitter = chargeDetailCommitter;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0) {
            Tasks.INSTANCE.async(() -> new MainMenu(playerPropertyManager, sidebarManager, chargeDetailCommitter, player).open());
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("recipe")) {
                Tasks.INSTANCE.async(() -> new RecipeListMenu(sidebarManager, playerPropertyManager, chargeDetailCommitter, player).open());

            }
            if (args[0].equalsIgnoreCase("close")) {
                player.closeInventory();
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("recipe")) {
                for (LocalRecipe localRecipe : LocalRecipe.localRecipeList) {
                    for (ItemStack result : localRecipe.results) {
                        if (result.getType().name().equalsIgnoreCase(args[1])) {
                            Tasks.INSTANCE.async(() -> new CraftRecipeMenu(playerPropertyManager, sidebarManager, chargeDetailCommitter, player, localRecipe).open());
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
