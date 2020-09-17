package com.molean.isletopia.prompter.prompter;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.molean.isletopia.parameter.UniversalParameter;
import com.molean.isletopia.utils.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerPrompter extends ChestPrompter {

    public PlayerPrompter(Player argPlayer, String argTtile, List<String> playernames) {
        super(argPlayer, argTtile);
        Collections.sort(playernames);
        for (String player : playernames) {
            ItemStack skull;
            skull = getSkull(player);
            addItemStacks(new Pair<>(skull, player));
        }
    }

    public ItemStack getSkull(String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        assert headMeta != null;
        headMeta.setDisplayName("§f" + name);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        String skinValue = UniversalParameter.getParameter(name, "skinValue");
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
}
