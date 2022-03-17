package com.molean.isletopia.infrastructure.assist;

import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;

public class BooleanCommand extends SubCommand {
    public BooleanCommand(String name, BiConsumer<Player,Boolean> consumer) {
        super(name, (player, args) -> {
            if (args.size() == 1) {
                if (args.get(0).equalsIgnoreCase("true")) {
                    consumer.accept(player,true);
                } else if (args.get(0).equalsIgnoreCase("false")) {
                    consumer.accept(player,false);
                } else {
                    MessageUtils.fail(player, "subcommand.boolean.valid");
                }

            } else {
                MessageUtils.fail(player, "subcommand.boolean.arg");

            }

        }, (player, args) -> List.of("true", "false"));
    }
}
