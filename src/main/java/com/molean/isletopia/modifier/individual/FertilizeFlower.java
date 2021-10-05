package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.bukkit.Material.*;

public class FertilizeFlower implements Listener {
    public FertilizeFlower() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        {
            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            List<ItemStack[]> sources = new ArrayList<>();
            types.add(new ItemStack(BONE_MEAL));

            icons.add(new ItemStack(ROSE_BUSH));
            icons.add(new ItemStack(SUNFLOWER));
            icons.add(new ItemStack(LILAC));
            icons.add(new ItemStack(PEONY));

            result.add(new ItemStack(ROSE_BUSH));
            result.add(new ItemStack(SUNFLOWER));
            result.add(new ItemStack(LILAC));
            result.add(new ItemStack(PEONY));

            ItemStack[] itemStacks = new ItemStack[9];
            for (int i = 0; i < itemStacks.length; i++) {
                itemStacks[i] = new ItemStack(AIR);
            }
            itemStacks[4] = new ItemStack(POPPY);
            sources.add(itemStacks);

            LocalRecipe.addRecipe(icons, types, sources, result);
        }
        {

            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            List<ItemStack[]> sources = new ArrayList<>();
            types.add(new ItemStack(BONE_MEAL));

            icons.add(new ItemStack(WITHER_ROSE));
            icons.add(new ItemStack(POPPY));
            result.add(new ItemStack(WITHER_ROSE));
            result.add(new ItemStack(POPPY));

            ItemStack[] itemStacks = new ItemStack[9];
            for (int i = 0; i < itemStacks.length; i++) {
                itemStacks[i] = new ItemStack(AIR);
            }
            itemStacks[4] = new ItemStack(WITHER_SKELETON_SKULL);
            sources.add(itemStacks);

            LocalRecipe.addRecipe(icons, types, sources, result);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        ItemStack item = event.getItem();
        if (item == null || !item.getType().equals(Material.BONE_MEAL)) {
            return;
        }
        if (clickedBlock == null) {
            return;
        }
        Block relative = clickedBlock.getRelative(BlockFace.UP);
        if (!relative.getType().equals(Material.AIR)) {
            return;
        }
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getPlayer())) {
            return;
        }
        Random random = new Random();
        if (clickedBlock.getType().equals(Material.POPPY)) {
            Material material;
            if (random.nextBoolean()) {
                if (random.nextBoolean()) {
                    material = Material.ROSE_BUSH;
                } else {
                    material = Material.SUNFLOWER;
                }
            } else {
                if (random.nextBoolean()) {
                    material = Material.LILAC;
                } else {
                    material = Material.PEONY;
                }
            }
            setFlower(clickedBlock, material, Bisected.Half.BOTTOM);
            setFlower(relative, material, Bisected.Half.TOP);
            item.setAmount(item.getAmount() - 1);
            event.setCancelled(true);
        } else if (clickedBlock.getType().equals(Material.WITHER_SKELETON_SKULL)) {
            if (random.nextInt(10) == 0) {
                clickedBlock.setType(Material.WITHER_ROSE);
            } else {
                clickedBlock.setType(Material.POPPY);
            }
            event.setCancelled(true);
        }

    }

    public void setFlower(Block block, Material type, Bisected.Half half) {
        block.setType(type, false);
        Bisected data = (Bisected) block.getBlockData();
        data.setHalf(half);
        block.setBlockData(data);
    }
}
