package com.molean.isletopia.story.action;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Blind implements Action {

    private final int duration;

    public Blind(int duration) {
        this.duration = duration;
    }

    @Override
    public void play(Player player) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,duration, 0,false,false,false));
        });

    }
}
