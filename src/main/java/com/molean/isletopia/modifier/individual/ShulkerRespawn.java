package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.bukkit.Material.*;

public class ShulkerRespawn implements Listener {
    public ShulkerRespawn() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        LocalRecipe.addRecipe(SHULKER_SPAWN_EGG, PURPUR_BLOCK, new ItemStack(SHULKER_SPAWN_EGG),
                AIR, AIR, AIR,
                AIR, SHULKER_BOX, AIR,
                AIR, AIR, AIR);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (!block.getType().name().toUpperCase(Locale.ROOT).contains("SHULKER_BOX")) {
            return;
        }
        List<Material> relatives = new ArrayList<>();
        relatives.add(block.getRelative(BlockFace.DOWN).getType());
        relatives.add(block.getRelative(BlockFace.UP).getType());
        relatives.add(block.getRelative(BlockFace.NORTH).getType());
        relatives.add(block.getRelative(BlockFace.EAST).getType());
        relatives.add(block.getRelative(BlockFace.WEST).getType());
        relatives.add(block.getRelative(BlockFace.SOUTH).getType());

        boolean hasPurpurBlock = false;
        for (Material relative : relatives) {
            if (relative.equals(Material.PURPUR_BLOCK)) {
                hasPurpurBlock = true;
                break;
            }
        }
        if (!hasPurpurBlock) {
            return;
        }

        ShulkerBox shulkerBox = (ShulkerBox) block.getState();
        DyeColor color = shulkerBox.getColor();
        block.setType(Material.AIR);
        Entity entity = block.getWorld().spawnEntity(block.getLocation(), EntityType.SHULKER);
        Shulker shulker = (Shulker) entity;
        shulker.setColor(color);

    }
}
