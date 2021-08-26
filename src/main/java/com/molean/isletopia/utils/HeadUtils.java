package com.molean.isletopia.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.infrastructure.individual.IslandInfoUpdater;
import com.molean.isletopia.shared.utils.RedisUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class HeadUtils {

    public static ItemStack getSkullWithIslandInfo(String name) {
        ItemStack skull = getSkull(name);
        ItemMeta itemMeta = skull.getItemMeta();
        UUID uuid = UUIDUtils.get(name);
        List<Component> componentList = new ArrayList<>();
        componentList.add(Component.text("§f岛屿状态: " + IslandInfoUpdater.getIslandStatus(uuid)));
        long area = IslandInfoUpdater.getArea(uuid);
        if (area == -1) {
            componentList.add(Component.text("§f岛屿面积: 未知"));
        } else {
            componentList.add(Component.text("§f岛屿面积: " + area));
        }
        String creation = IslandInfoUpdater.getCreation(uuid);
        if (creation == null) {
            componentList.add(Component.text("§f创建日期: 未知"));
        } else {
            componentList.add(Component.text("§f创建日期: " + creation));
        }

        componentList.add(Component.text("§fPvP: " + IslandInfoUpdater.isEnablePvP(uuid)));
        componentList.add(Component.text("§f防火: " + IslandInfoUpdater.isAntiFire(uuid)));

        itemMeta.lore(componentList);
        skull.setItemMeta(itemMeta);
        return skull;
    }

    public static String getSkullValue(ItemStack head) {

        try {
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            assert headMeta != null;
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile gameProfile = (GameProfile) profileField.get(headMeta);
            Collection<Property> textures = gameProfile.getProperties().get("textures");
            for (Property texture : textures) {
                String value = texture.getValue();
                if (value != null) {
                    return value;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static ItemStack getSkull(String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;
        headMeta.displayName(Component.text("§f" + name));
        GameProfile profile = new GameProfile(UUIDUtils.get(name), null);
        String skinValue;

        try (Jedis jedis = RedisUtils.getJedis()) {
            if (jedis.exists("SkinValue-" + name)) {
                skinValue = jedis.get("SkinValue-" + name);
            } else {
                skinValue = UniversalParameter.getParameter(name, "skinValue");
                if (skinValue != null && !skinValue.isEmpty()) {

                    jedis.setex("SkinValue-" + name, 60 * 5L, skinValue);
                }
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
        GameProfile profile = new GameProfile(UUIDUtils.get(name), null);
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
