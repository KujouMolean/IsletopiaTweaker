package com.molean.isletopia.utils;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EntityFreezer {
    public static void freezeEntity(LivingEntity entity) {
        entity.setAI(false);
    }

    public static void unfreezeEntity(LivingEntity entity) {
        entity.setAI(true);
        entity.setVelocity(new Vector());
    }

    public static boolean isFrozen(LivingEntity entity) {
        return !entity.hasAI() && !(entity instanceof Player) && !(entity instanceof ArmorStand);
    }
}
