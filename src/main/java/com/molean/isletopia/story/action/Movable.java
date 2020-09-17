package com.molean.isletopia.story.action;


import com.molean.isletopia.story.listener.MoveListener;
import org.bukkit.entity.Player;

public class Movable implements Action {
    private final boolean movable;

    public Movable(boolean movable) {
        this.movable = movable;
    }

    @Override
    public void play(Player player) {
        MoveListener.setMovable(player.getName(), movable);
    }
}
