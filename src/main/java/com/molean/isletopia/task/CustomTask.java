package com.molean.isletopia.task;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;

public class CustomTask extends BukkitRunnable {

    private final Queue<Runnable> runnableList;
    private final int timeoutTicks;
    private final Runnable runnable;


    public CustomTask(Queue<Runnable> runnableList, int timeoutTicks,Runnable runnable) {
        this.runnableList = runnableList;
        this.timeoutTicks = timeoutTicks;
        this.runnable = runnable;
    }

    public void runAsync() {
        int taskPerTick = runnableList.size() / timeoutTicks;
        if (runnableList.size() % timeoutTicks != 0) {
            taskPerTick++;
        }

        int finalTaskPerTick = taskPerTick;
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), (task) -> {
            int count = 0;
            while (count++ < finalTaskPerTick && !runnableList.isEmpty()) {
                Runnable poll = runnableList.poll();
                poll.run();
            }
            if (runnableList.isEmpty()) {
                task.cancel();
                if (runnable != null) {
                    runnable.run();
                }
            }

        }, 0, 1);
    }

    @Override
    public void run() {
        int taskPerTick = runnableList.size() / timeoutTicks;
        if (runnableList.size() % timeoutTicks != 0) {
            taskPerTick++;
        }

        int finalTaskPerTick = taskPerTick;
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), (task) -> {
            int count = 0;
            while (count++ < finalTaskPerTick && !runnableList.isEmpty()) {
                Runnable poll = runnableList.poll();
                poll.run();
            }
            if (runnableList.isEmpty()) {
                task.cancel();
                if (runnable != null) {
                    runnable.run();
                }
            }

        }, 0, 1);
    }

}
