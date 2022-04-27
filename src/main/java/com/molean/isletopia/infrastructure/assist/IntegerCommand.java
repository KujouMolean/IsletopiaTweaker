package com.molean.isletopia.infrastructure.assist;

import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class IntegerCommand extends SubCommand {

    public IntegerCommand integerConsumer(BiConsumer<Player, Integer> consumer) {
        super.consumer((player, args) -> {
            if (args.size() == 1) {
                int i = 0;
                try {
                    i = Integer.parseInt(args.get(0));
                } catch (NumberFormatException e) {
                    MessageUtils.fail(player, MessageUtils.getMessage(player, "subcommand.int.valid", Pair.of("arg", args.get(0))));
                }
                consumer.accept(player, i);
            } else {
                MessageUtils.fail(player, "subcommand.int.arg");

            }

        });
        return this;
    }

    public IntegerCommand suggest(List<Integer> suggest) {
        super.completer((player, args) -> {
            ArrayList<String> strings = new ArrayList<>();

            if (suggest != null) {
                for (Integer integer : suggest) {
                    strings.add(integer + "");
                }
            }
            return strings;
        });
        return this;
    }

    public IntegerCommand(String name) {
        super(name);
    }

    public IntegerCommand(String name, BiConsumer<Player,Integer> consumer, List<Integer> suggest) {
        super(name);
        this.suggest(suggest);
        this.integerConsumer(consumer);
    }
}
