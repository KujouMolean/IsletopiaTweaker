package com.molean.isletopia.infrastructure.assist;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class SubCommand {
    private BiConsumer<Player, List<String>> consumer = (player, strings) -> {};

    private BiFunction<Player, List<String>, List<String>> completer = (player, strings) -> new ArrayList<>();
    private final String name;


    public String getName() {
        return name;
    }

    public SubCommand(String name) {
        this.name = name;
    }

    public SubCommand(String name, BiConsumer<Player, List<String>> consumer, BiFunction<Player, List<String>, List<String>> completer) {
        this.consumer = consumer;
        this.name = name;
        this.completer = completer;
    }

    public SubCommand consumer(BiConsumer<Player, List<String>> consumer) {
        this.consumer = consumer;
        return this;
    }
    public SubCommand completer(BiFunction<Player, List<String>, List<String>> completer) {
        this.completer = completer;
        return this;
    }

    public void run(Player player,String... args) {
        consumer.accept(player, Arrays.asList(args));
    }

    public List<String> getSuggestion(Player player,String... args) {
        if (completer == null) {
            return new ArrayList<>();
        }
        return completer.apply(player, Arrays.asList(args));
    }
}
