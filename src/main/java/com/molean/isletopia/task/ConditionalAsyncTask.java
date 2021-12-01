package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Supplier;

public class ConditionalAsyncTask extends BukkitRunnable {
    private final Supplier<Boolean> supplier;
    private Runnable task;
    private Runnable complete;
    private Runnable orElse;

    public ConditionalAsyncTask(Supplier<Boolean> supplier) {
        this.supplier = supplier;
    }

    public ConditionalAsyncTask then(Runnable runnable) {
        this.task = runnable;
        return this;
    }

    public ConditionalAsyncTask complete(Runnable runnable) {
        this.complete = runnable;
        return this;
    }
    public ConditionalAsyncTask orElse(Runnable runnable) {
        this.orElse = runnable;
        return this;
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (supplier.get()) {
                if (task != null) {
                    task.run();
                }
            }else{
                if (orElse != null) {
                    orElse.run();
                }
            }
            if (complete != null) {
                complete.run();

            }
        });
    }
}
