package com.molean.isletopia.infrastructure.assist;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SimpleCommand extends SubCommand {
    public SimpleCommand simpleConsumer(Consumer<Player> consumer) {
        super.consumer((player, objects) -> consumer.accept(player));
        return this;
    }

    public SimpleCommand(String name) {
        super(name);
    }

    public SimpleCommand(String name, Consumer<Player> consumer) {
        super(name);
        this.simpleConsumer(consumer);
    }
}
