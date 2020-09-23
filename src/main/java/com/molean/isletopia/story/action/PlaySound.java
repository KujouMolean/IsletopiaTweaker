package com.molean.isletopia.story.action;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySound implements Action {

    private final String sound;

    public PlaySound(String sound) {
        this.sound = sound;
    }

    @Override
    public void play(Player player) {
        player.playSound(player.getLocation(), Sound.valueOf(sound), 1.0F, 1.0F);
    }
}
