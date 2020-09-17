package com.molean.isletopia.story.action;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayEffect implements Action {
    private final PotionEffectType potionEffectType;
    private final int duration;
    private final int amplifier;

    public PlayEffect(PotionEffectType potionEffectType, int duration, int amplifier) {
        this.potionEffectType = potionEffectType;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void play(Player player) {
        player.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier));
    }

    @Override
    public String toString() {
        return "PlayEffect{" +
                "potionEffectType=" + potionEffectType +
                ", duration=" + duration +
                ", amplifier=" + amplifier +
                '}';
    }
}
