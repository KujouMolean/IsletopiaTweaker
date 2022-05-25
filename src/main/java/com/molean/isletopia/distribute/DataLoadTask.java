package com.molean.isletopia.distribute.individual;


import com.molean.isletopia.task.Tasks;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class DataLoadTask<T> {

    private BiConsumer<Player, Consumer<T>> asyncLoad = null;
    private TriConsumer<Player, T, Consumer<Player>> syncRestore = null;

    private String name;

    public DataLoadTask(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BiConsumer<Player, Consumer<T>> getAsyncLoad() {
        return asyncLoad;
    }

    public void setAsyncLoad(BiConsumer<Player, Consumer<T>> asyncLoad) {
        this.asyncLoad = asyncLoad;
    }

    public TriConsumer<Player, T, Consumer<Player>> getSyncRestore() {
        return syncRestore;
    }

    public void setSyncRestore(TriConsumer<Player, T, Consumer<Player>> syncRestore) {
        this.syncRestore = syncRestore;
    }

    public void load(Player player, BiConsumer<Boolean, Exception> result) {
        Tasks.INSTANCE.async(() -> {
            try {
                asyncLoad.accept(player, t -> {
                    Tasks.INSTANCE.sync(() -> {
                        try {
                            syncRestore.accept(player, t, p -> {
                                result.accept(true, null);
                            });
                        } catch (Exception e) {
                            result.accept(false, e);
                        }
                    });
                });
            } catch (Exception e) {
                Tasks.INSTANCE.sync(() -> result.accept(false, e));
            }
        });
    }
}
