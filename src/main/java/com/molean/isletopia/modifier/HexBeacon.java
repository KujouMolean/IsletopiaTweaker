package com.molean.isletopia.modifier;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.task.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Singleton
public class HexBeacon {

    public HexBeacon() {
        Tasks.INSTANCE.intervalAsync(50, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(onlinePlayer);
                if (currentIsland == null) {
                    continue;
                }
                if (currentIsland.containsFlag("EnableHexBeaconSpeed")) {
                    Tasks.INSTANCE.sync(() -> onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1)));
                }
                if (currentIsland.containsFlag("EnableHexBeaconFastDigging")) {
                    Tasks.INSTANCE.sync(() -> onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 1)));
                }
                if (currentIsland.containsFlag("EnableHexBeaconIncreaseDamage")) {
                    Tasks.INSTANCE.sync(() -> onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1)));
                }
                if (currentIsland.containsFlag("EnableHexBeaconJump")) {
                    Tasks.INSTANCE.sync(() -> onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 1)));
                }
                if (currentIsland.containsFlag("EnableHexBeaconDamageResistance")) {
                    Tasks.INSTANCE.sync(() -> onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 1)));
                }
                if (currentIsland.containsFlag("EnableHexBeaconRegeneration")) {
                    Tasks.INSTANCE.sync(() -> onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0)));
                }
            }

        });
    }
}
