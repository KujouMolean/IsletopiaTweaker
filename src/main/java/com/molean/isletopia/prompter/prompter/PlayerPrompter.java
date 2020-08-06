package com.molean.isletopia.prompter.prompter;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.molean.isletopia.network.UniversalParameter;
import com.molean.isletopia.prompter.util.Pair;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerPrompter extends ChestPrompter {

    @Override
    public void freshPage() {

        inventory.clear();
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (int i = 0; i < itemStacks.size() - page * 45; i++) {
                if (i == 45) break;
                Pair<ItemStack, String> pair = itemStacks.get(page * 45 + i);

                inventory.setItem(i, pair.getKey());
                player.updateInventory();
                int finalI = i;
                int finalP = page;
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    ItemStack skull = pair.getKey();
//                    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
//                    skullMeta.setOwner(pair.getValue());
//                    skull.setItemMeta(skullMeta);
                    pair.setKey(skull);
                    if (finalP == page) {
                        inventory.setItem(finalI, pair.getKey());
                        player.updateInventory();
                    }

                });
            }
        });

        //prev page button
        ItemStack prev = new ItemStack(Material.FEATHER);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName("§f<=");
        prev.setItemMeta(prevMeta);
        inventory.setItem(9 * 5 + 2, prev);

        //next page button
        ItemStack next = new ItemStack(Material.FEATHER);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName("§f=>");
        next.setItemMeta(nextMeta);
        inventory.setItem(9 * 5 + 6, next);
    }

    public PlayerPrompter(Player argPlayer, String argTtile, List<String> playernames) {
        super(argPlayer, argTtile);
        Collections.sort(playernames);
        List<String> playerList = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player1 -> {
            playerList.add(player1.getName());
        });
        for (String player : playernames) {
            ItemStack skull;
            skull = getSkull(player);
            addItemStacks(new Pair<>(skull, player));
        }
    }

    public ItemStack getSkull(String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
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
