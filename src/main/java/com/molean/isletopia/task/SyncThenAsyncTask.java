package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SyncThenAsyncTask<T> extends BukkitRunnable {
    private final Supplier<T> supplier;
    private final Consumer<T> consumer;
    private Runnable runnable = null;

    public SyncThenAsyncTask(Supplier<T> supplier, Consumer<T> consumer) {
        this.supplier = supplier;
        this.consumer = consumer;

    }

    public SyncThenAsyncTask<T> then(Runnable runnable) {
        this.runnable = runnable;

        return this;
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            T t = supplier.get();
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                consumer.accept(t);
                if (runnable != null) {
                    runnable.run();
                }

            });
        });
    }
}
