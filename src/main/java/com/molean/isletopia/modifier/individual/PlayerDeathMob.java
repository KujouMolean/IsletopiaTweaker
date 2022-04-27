package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;

public class PlayerDeathMob implements Listener {
    public PlayerDeathMob() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler
    public void on(PlayerRespawnEvent event) {
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 4));
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getEntity())) {
            return;
        }
        if (PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(event.getEntity(), "DisablePlayerMob")) {
            return;
        }
        Location location = event.getEntity().getLocation();
        String lowerBiomeName = location.getBlock().getBiome().name().toLowerCase(Locale.ROOT);
        boolean hasWither  = false;
        for (PotionEffect activePotionEffect : event.getEntity().getActivePotionEffects()) {
            if (activePotionEffect.getType().equals(PotionEffectType.WITHER)) {
                hasWither = true;
            }
        }
        if (hasWither) {
            WitherSkeleton witherSkeleton = (WitherSkeleton) location.getWorld().spawnEntity(location, EntityType.WITHER_SKELETON,CreatureSpawnEvent.SpawnReason.NATURAL);
            witherSkeleton.setCustomName(event.getEntity().getName());
            return;
        }
        if (event.getEntity().getFireTicks() > 0) {
            Blaze blaze = (Blaze) location.getWorld().spawnEntity(location, EntityType.BLAZE,CreatureSpawnEvent.SpawnReason.NATURAL);
            blaze.setCustomName(event.getEntity().getName());
            return;
        }
        if (lowerBiomeName.contains("end")) {
            Enderman enderman = (Enderman) location.getWorld().spawnEntity(location, EntityType.ENDERMAN,CreatureSpawnEvent.SpawnReason.NATURAL);
            enderman.setCustomName(event.getEntity().getName());
            return;
        }
        if (lowerBiomeName.contains("desert")) {
            Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON, CreatureSpawnEvent.SpawnReason.NATURAL);
            skeleton.setCustomName(event.getEntity().getName());
            return;
        }
        Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE,CreatureSpawnEvent.SpawnReason.NATURAL);
        zombie.setCustomName(event.getEntity().getName());
    }
}
