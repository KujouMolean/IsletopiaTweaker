package com.molean.isletopia.story.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PerformDelay implements Action {
    private final int delay;

    public PerformDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public void play(Player player) {
        if (Bukkit.getServer().isPrimaryThread()) {
            player.sendMessage("Error!! Delay in main thread!");
        } else {
            try {
                Thread.sleep(delay * 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String toString() {
        return "PerformDelay{" +
                "delay=" + delay +
                '}';
    }
}
