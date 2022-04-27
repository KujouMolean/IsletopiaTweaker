package com.molean.isletopia.infrastructure.assist;

import com.molean.isletopia.shared.utils.QuConsumer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectPropertyCommand extends SubCommand {
    public ObjectPropertyCommand(String name) {
        super(name);
    }
    //achievement obj add obj(args)
    //achievement obj set obj key value
    //achievement obj get obj key value
    //achievement obj list
    //achievement obj list obj
    //achievement obj delete obj

    public ObjectPropertyCommand ObjectPropertyCommand(QuConsumer<Player, String, String, String> quConsumer) {
        super.consumer((player, strings) -> {
            ArrayList<String> values = new ArrayList<>(strings);
            values.remove(0);
            values.remove(0);
            quConsumer.accept(player, strings.get(0), strings.get(1), String.join(" ", values));

        });
        return this;
    }


    private Supplier<List<String>> supplier = () -> new ArrayList<>();
    private Function<String, List<String>> function = (o) -> new ArrayList<>();

    public ObjectPropertyCommand availableObjects(Supplier<List<String>> supplier) {
        this.supplier = supplier;
        updateCompleter();
        return this;
    }

    public ObjectPropertyCommand availableKeys(Function<String, List<String>> function) {
        this.function = function;
        updateCompleter();
        return this;
    }

    private void updateCompleter() {
        this.completer((player, strings) -> {
            if (strings.size() == 1) {
                return this.supplier.get();
            }
            if (strings.size() == 2) {
                return this.function.apply(strings.get(0));
            }
            return new ArrayList<>();
        });
    }
}
