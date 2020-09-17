package com.molean.isletopia.story.action;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class ClearEffect implements Action {
    @Override
    public void play(Player player) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            for (PotionEffectType value : PotionEffectType.values()) {
                player.removePotionEffect(value);
            }
        });

    }
}
