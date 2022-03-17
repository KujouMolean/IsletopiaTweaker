package com.molean.isletopia.infrastructure.assist;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SimpleCommand extends SubCommand {
    public SimpleCommand(String name, Consumer<Player> consumer) {
        super(name, (player, objects) -> consumer.accept(player), (player, objects) -> new ArrayList<>());
    }
}
