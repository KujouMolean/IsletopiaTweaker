package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Supplier;

public class AsyncTryTask extends BukkitRunnable {

    private final Supplier<Boolean> supplier;
    private final int interval;
    private final int maxTry;
    private Runnable onFailed;
    private int currentTry = 0;

    public AsyncTryTask(Supplier<Boolean> supplier, int interval, int maxTry) {
        this.supplier = supplier;
        this.interval = interval;
        this.maxTry = maxTry;
    }

    public AsyncTryTask onFailed(Runnable runnable) {
        this.onFailed = runnable;
        return this;
    }

    public void cancel() {
        currentTry = maxTry;
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), (task) -> {
            if (currentTry++ >= maxTry) {
                task.cancel();
                if (onFailed != null) {
                    onFailed.run();
                }
                return;
            }
            if (supplier.get()) {
                task.cancel();
            }

        }, 0, interval);
    }
}
