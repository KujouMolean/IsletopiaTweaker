package com.molean.isletopia.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.shared.utils.UUIDUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;

public class HeadUtils {

    public static ItemStack getSkullWithIslandInfo(String name) {
        ItemStack skull = getSkull(name);
        ItemMeta itemMeta = skull.getItemMeta();
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

        String skinValue = null;

        if (RedisUtils.getCommand().exists(name + ":SkinValue") > 0) {
            skinValue = RedisUtils.getCommand().get(name + ":SkinValue");
        }

        return getSkullFromValue(name, skinValue);
    }

    public static ItemStack getSkullFromValue(String name, String value) {

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;
        headMeta.displayName(Component.text("§f" + name));
        if (value != null) {
            UUID uuid = UUIDUtils.getOffline(name);
            GameProfile profile = new GameProfile(uuid, name);
            profile.getProperties().put("textures", new Property("textures", value));
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
}
