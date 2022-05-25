package com.molean.isletopia.infrastructure;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.menu.recipe.RecipeListMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.task.Tasks;
import org.bukkit.entity.Player;

@CommandAlias("menu")
public class MenuCommand extends BaseCommand {

    private final PlayerPropertyManager playerPropertyManager;
    private final SidebarManager sidebarManager;
    private final ChargeCommitter chargeCommitter;

    public MenuCommand(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter) {
        this.playerPropertyManager = playerPropertyManager;
        this.sidebarManager = sidebarManager;
        this.chargeCommitter = chargeCommitter;
    }

    @Default
    public void onDefault(Player player) {
        Tasks.INSTANCE.async(() -> new MainMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open());
    }

    @Subcommand("recipe")
    public void recipe(Player player) {
        Tasks.INSTANCE.async(() -> new RecipeListMenu(sidebarManager, playerPropertyManager, chargeCommitter, player).open());
    }

    @Subcommand("close")
    public void close(Player player) {
        player.closeInventory();
    }
}
