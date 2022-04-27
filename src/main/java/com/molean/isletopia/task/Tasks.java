package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum Tasks {
    INSTANCE;

    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), runnable);

    }

    private final Map<String, Runnable> shutdownMap = new HashMap<>();

    public Map<String, Runnable> getShutdownMap() {
        return shutdownMap;
    }

    public void addDisableTask(String key, Runnable runnable) {
        shutdownMap.put(key, runnable);
    }

    public void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), runnable);

    }

    public void repeatTaskSync(int interval,int times, Consumer<Integer> consumer) {
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), new Consumer<>() {
            private int cnt = 0;

            @Override
            public void accept(BukkitTask task) {
                consumer.accept(cnt);
                cnt++;
                if (cnt >= times) {
                    task.cancel();
                }
            }
        }, 0, interval);
    }

    public void repeatTaskAsync(int interval,Consumer<Integer> consumer, Supplier<Boolean> supplier) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), new Consumer<BukkitTask>() {
            private int cnt = 0;

            @Override
            public void accept(BukkitTask task) {
                if (!supplier.get()) {
                    task.cancel();
                }
                consumer.accept(cnt);
                cnt++;
            }
        }, 0, interval);
    }

    public void doTaskWhileAsync(Consumer<Integer> consumer, Supplier<Boolean> supplier) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), new Consumer<>() {
            private int cnt = 0;

            @Override
            public void accept(BukkitTask task) {
                consumer.accept(cnt);
                cnt++;
                if (!supplier.get()) {
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

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), runnable, 0, ticks);
        addDisableTask("Cancel interval task...", bukkitTask::cancel);

        return bukkitTask;
    }

    public BukkitTask intervalAsync(int ticks, Runnable runnable) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), runnable, 0, ticks);
        addDisableTask("Cancel interval task...", bukkitTask::cancel);
        return bukkitTask;
    }

    public void timeout(int ticks, Runnable runnable) {
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), runnable, ticks);
    }

    public void timeoutAsync(int ticks, Runnable runnable) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(IsletopiaTweakers.getPlugin(), runnable, ticks);
    }

}
