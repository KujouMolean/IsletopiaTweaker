package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.utils.HeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.bukkit.Material.*;

public class ShulkerRespawn implements Listener {
    public ShulkerRespawn() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        LocalRecipe.addRecipe(SHULKER_SPAWN_EGG, PURPUR_BLOCK, HeadUtils.getSkullFromValue("shulker.name", PlayerHeadDrop.drops.get(EntityType.SHULKER)),
                AIR, AIR, AIR,
                AIR, SHULKER_BOX, AIR,
                AIR, AIR, AIR);


    }

    public void stainPurpurBlock(Block purpurBlock, Block target) {
        if (!purpurBlock.getType().equals(PURPUR_BLOCK)) {
            return;
        }
        List<Block> relatives = new ArrayList<>();
        relatives.add(target.getRelative(BlockFace.DOWN));
        relatives.add(target.getRelative(BlockFace.UP));
        relatives.add(target.getRelative(BlockFace.NORTH));
        relatives.add(target.getRelative(BlockFace.EAST));
        relatives.add(target.getRelative(BlockFace.WEST));
        relatives.add(target.getRelative(BlockFace.SOUTH));
        for (Block relative : relatives) {
            if (relative.getType().name().toUpperCase(Locale.ROOT).contains("SHULKER_BOX")) {
                ShulkerBox shulkerBox = (ShulkerBox) relative.getState();
                DyeColor color = shulkerBox.getColor();
                ArrayList<ItemStack> itemStacks = new ArrayList<>();
                for (ItemStack content : shulkerBox.getInventory().getContents()) {
                    if (content != null) {
                        itemStacks.add(new ItemStack(content));
                    }
                }
                shulkerBox.getInventory().clear();
                for (ItemStack itemStack : itemStacks) {
                    relative.getWorld().dropItem(relative.getLocation(), itemStack);

                }
                relative.setType(Material.AIR);
                Entity entity = relative.getWorld().spawnEntity(relative.getLocation(), EntityType.SHULKER);
                Shulker shulker = (Shulker) entity;
                shulker.setColor(color);
                break;
            }
        }
    }

    public void stainShulkerBox(Block shulkerBlock, Block target) {
        if (!shulkerBlock.getType().name().toUpperCase(Locale.ROOT).contains("SHULKER_BOX")) {
            return;
        }
        List<Material> relatives = new ArrayList<>();
        relatives.add(target.getRelative(BlockFace.DOWN).getType());
        relatives.add(target.getRelative(BlockFace.UP).getType());
        relatives.add(target.getRelative(BlockFace.NORTH).getType());
        relatives.add(target.getRelative(BlockFace.EAST).getType());
        relatives.add(target.getRelative(BlockFace.WEST).getType());
        relatives.add(target.getRelative(BlockFace.SOUTH).getType());
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

        ShulkerBox shulkerBox = (ShulkerBox) shulkerBlock.getState();
        DyeColor color = shulkerBox.getColor();
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (ItemStack content : shulkerBox.getInventory().getContents()) {
            if (content != null) {
                itemStacks.add(new ItemStack(content));
            }
        }
        shulkerBox.getInventory().clear();
        for (ItemStack itemStack : itemStacks) {
            shulkerBlock.getWorld().dropItem(shulkerBlock.getLocation(), itemStack);

        }
        shulkerBlock.setType(Material.AIR);
        Entity entity = shulkerBlock.getWorld().spawnEntity(shulkerBlock.getLocation(), EntityType.SHULKER);
        Shulker shulker = (Shulker) entity;
        shulker.setColor(color);
    }

    @EventHandler(ignoreCancelled = true)
    public void placeShulkerBox(BlockPlaceEvent event) {
        stainShulkerBox(event.getBlock(), event.getBlock());

    }

    @EventHandler(ignoreCancelled = true)
    public void placePurpurBlock(BlockPlaceEvent event) {
        stainPurpurBlock(event.getBlock(), event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType().equals(PURPUR_BLOCK)) {
                stainPurpurBlock(block, block.getRelative(event.getDirection()));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType().equals(PURPUR_BLOCK)) {
                stainPurpurBlock(block, block.getRelative(event.getDirection().getOppositeFace()));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockDispenseEvent event) {
        if (!(event.getBlock().getBlockData() instanceof Dispenser dispenser)) {
            return;
        }
        Block shulkerBox = event.getBlock().getRelative(dispenser.getFacing());
        if (event.getItem().getType().name().toLowerCase(Locale.ROOT).contains("shulker_box")) {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                stainShulkerBox(shulkerBox, shulkerBox);

            });
        }
    }
}
