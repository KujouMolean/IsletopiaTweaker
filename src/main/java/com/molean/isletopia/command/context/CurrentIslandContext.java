package com.molean.isletopia.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import co.aikar.commands.contexts.IssuerAwareContextResolver;
import co.aikar.commands.contexts.IssuerOnlyContextResolver;
import com.molean.isletopia.annotations.Context;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.annotations.Bean;
import org.bukkit.entity.Player;


@Context(LocalIsland.class)
public class CurrentIslandContext implements IssuerOnlyContextResolver<LocalIsland, BukkitCommandExecutionContext> {

    @Override
    public LocalIsland getContext(BukkitCommandExecutionContext bukkitCommandExecutionContext) throws InvalidCommandArgument {

        Player player = bukkitCommandExecutionContext.getPlayer();


        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null) {
            throw new InvalidCommandArgument("当前位置不明确!");
        }

        if (bukkitCommandExecutionContext.hasFlag("owner")) {
            if (!currentIsland.getUuid().equals(player.getUniqueId())) {
                throw new InvalidCommandArgument("你不是岛主。");
            }
        }

        if (bukkitCommandExecutionContext.hasFlag("member")) {
            if (!currentIsland.hasPermission(player)) {
                throw new InvalidCommandArgument("你不是岛员或岛主。");
            }
        }

        return currentIsland;

    }
}
