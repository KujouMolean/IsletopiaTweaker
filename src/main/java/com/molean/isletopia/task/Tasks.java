package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public enum Tasks {
    INSTANCE;

    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), runnable);
    }


    public void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), runnable);
    }

    public void repeatTask(int times, Consumer<Integer> consumer) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), new Consumer<>() {
            private int cnt = 0;

            @Override
            public void accept(BukkitTask task) {
                consumer.accept(cnt);
                cnt++;
                if (cnt >= times) {
                    task.cancel();
                }
            }
        });
    }

    public void repeatTaskAsync(int times, Consumer<Integer> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), new Consumer<>() {
            private int cnt = 0;

            @Override
            public void accept(BukkitTask task) {
                consumer.accept(cnt);
                cnt++;
                if (cnt >= times) {
                    task.cancel();
                }
            }
        });
    }

    public BukkitTask interval(int ticks, Runnable runnable) {

        return Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), runnable, 0, ticks);
    }

    public BukkitTask intervalAsync(int ticks, Runnable runnable) {

        return Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), runnable, 0, ticks);
    }

    public void timeout(int ticks, Runnable runnable) {

        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), runnable, ticks);
    }

    public void timeoutAsync(int ticks, Runnable runnable) {

        Bukkit.getScheduler().runTaskLaterAsynchronously(IsletopiaTweakers.getPlugin(), runnable, ticks);
    }

}
