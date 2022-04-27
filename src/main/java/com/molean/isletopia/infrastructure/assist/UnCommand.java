package com.molean.isletopia.infrastructure.assist;

import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UnCommand extends SubCommand{
    public UnCommand(String name) {
        super(name);
    }

    public UnCommand unConsumer(BiConsumer<Player, String> consumer) {
        super.consumer((player, strings) -> {
            if (strings.size() == 0) {
                MessageUtils.fail(player, "至少需要一个参数.");
                return;
            }
            consumer.accept(player, strings.get(0));

        });
        return this;
    }

}
