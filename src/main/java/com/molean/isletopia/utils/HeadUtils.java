package com.molean.isletopia.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.molean.isletopia.bungee.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.utils.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class HeadUtils {

    private static final Map<String, Pair<String, Long>> cache = new HashMap<>();

    public static ItemStack getSkull(String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;
        headMeta.displayName(Component.text("§f" + name));
        GameProfile profile = new GameProfile(ServerInfoUpdater.getUUID(name), null);
        Pair<String, Long> stringLongPair = cache.get(name);

        String skinValue;
        if (stringLongPair == null) {
            skinValue = UniversalParameter.getParameter(name, "skinValue");
            cache.put(name, new Pair<>(skinValue, System.currentTimeMillis()));
        } else {
            long t = System.currentTimeMillis() - stringLongPair.getValue();
            if (t > 10 * 1000 * 60) {
                skinValue = UniversalParameter.getParameter(name, "skinValue");
                cache.put(name, new Pair<>(skinValue, System.currentTimeMillis()));
            } else {
                skinValue = stringLongPair.getKey();
            }

        }

        if (skinValue != null && !"".equalsIgnoreCase(skinValue)) {
            profile.getProperties().put("textures", new Property("textures", skinValue));
            try {
                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
                error.printStackTrace();
            }
        }
        head.setItemMeta(headMeta);
        return head;
    }

    public static ItemStack getSkullFromValue(String name, String value) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;
        headMeta.displayName(Component.text("§f" + name));
        GameProfile profile = new GameProfile(ServerInfoUpdater.getUUID(name), null);
        profile.getProperties().put("textures", new Property("textures", value));
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
}
