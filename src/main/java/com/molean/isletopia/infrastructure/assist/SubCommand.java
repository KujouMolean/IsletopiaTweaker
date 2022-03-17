package com.molean.isletopia.infrastructure.assist;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class SubCommand {
    private final BiConsumer<Player, List<String>> consumer;
    private final BiFunction<Player, List<String>, List<String>> completer;
    private final String name;


    public String getName() {
        return name;
    }

    public SubCommand(String name, BiConsumer<Player, List<String>> consumer, BiFunction<Player, List<String>, List<String>> completer) {
        this.consumer = consumer;
        this.name = name;
        this.completer = completer;
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
