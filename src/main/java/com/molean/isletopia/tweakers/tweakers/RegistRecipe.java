package com.molean.isletopia.tweakers.tweakers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static org.bukkit.Material.*;

public class RegistRecipe {

    private static final List<Material> dyeRecipeMaterials = List.of(GOLD_INGOT, IRON_INGOT, NETHERITE_INGOT, BRICK, NETHER_BRICK);

    public RegistRecipe() {
        //萤石
        Bukkit.resetRecipes();
        RecipeUtils.registerShaped(new ItemStack(Material.GLOWSTONE),
                TORCH, TORCH, TORCH,
                TORCH, STONE, TORCH,
                TORCH, TORCH, TORCH);

        //合成粘土
        RecipeUtils.registerShaped(new ItemStack(CLAY_BALL, 32),
                DIRT, DIRT, DIRT,
                DIRT, SLIME_BALL, DIRT,
                DIRT, DIRT, DIRT);
        //合成凋零头
        RecipeUtils.registerShaped(new ItemStack(WITHER_SKELETON_SKULL),
                BLACK_DYE, BLACK_DYE, BLACK_DYE,
                BLACK_DYE, SKELETON_SKULL, BLACK_DYE,
                BLACK_DYE, BLACK_DYE, BLACK_DYE);

//        末地石
        RecipeUtils.registerSmithingRecipie(new ItemStack(END_STONE), STONE, ENDER_PEARL);
//        绯红菌岩
        RecipeUtils.registerSmithingRecipie(new ItemStack(CRIMSON_NYLIUM), NETHERRACK, CRIMSON_FUNGUS);
//        诡异菌岩
        RecipeUtils.registerSmithingRecipie(new ItemStack(WARPED_NYLIUM), NETHERRACK, WARPED_FUNGUS);
//        铁粒
        RecipeUtils.registerCampfire(new ItemStack(IRON_NUGGET), GUNPOWDER, 1.0F, 60);
//        钻石
        RecipeUtils.registerCampfire(new ItemStack(DIAMOND), POISONOUS_POTATO, 1.0F, 600);
//        灵魂沙
        RecipeUtils.registerSmoking(new ItemStack(SOUL_SAND), SAND, 1.0F, 150);
//        地狱岩
        RecipeUtils.registerSmoking(new ItemStack(NETHERRACK), COBBLESTONE, 1.0F, 150);
//        灵魂土
        RecipeUtils.registerSmoking(new ItemStack(SOUL_SOIL), DIRT, 1.0F, 150);
//        砂砾
        RecipeUtils.registerStonecutting(new ItemStack(GRAVEL), COBBLESTONE);
//        砂砾
        RecipeUtils.registerBlasting(new ItemStack(GRAVEL), COBBLESTONE, 1.0F, 150);
//        沙子
        RecipeUtils.registerBlasting(new ItemStack(SAND), GRAVEL, 1.0F, 150);
//        石英
        RecipeUtils.registerFurnace(new ItemStack(QUARTZ), GLASS, 1.0F, 150);
//        烈焰粉
        RecipeUtils.registerBlasting(new ItemStack(BLAZE_POWDER), REDSTONE, 1.0F, 150);
        //转换树苗
        RecipeUtils.registerShapeless(new ItemStack(OAK_SAPLING), DARK_OAK_SAPLING);
        RecipeUtils.registerShapeless(new ItemStack(SPRUCE_SAPLING), OAK_SAPLING);
        RecipeUtils.registerShapeless(new ItemStack(BIRCH_SAPLING), SPRUCE_SAPLING);
        RecipeUtils.registerShapeless(new ItemStack(JUNGLE_SAPLING), BIRCH_SAPLING);
        RecipeUtils.registerShapeless(new ItemStack(ACACIA_SAPLING), JUNGLE_SAPLING);
        RecipeUtils.registerShapeless(new ItemStack(DARK_OAK_SAPLING), ACACIA_SAPLING);
        //合成烈焰棒
        RecipeUtils.registerShapeless(new ItemStack(BLAZE_ROD), BLAZE_POWDER, BLAZE_POWDER);
        //合成煤炭
        RecipeUtils.registerShapeless(new ItemStack(COAL), CHARCOAL);
        //合成菌丝
        RecipeUtils.registerSmithingRecipie(new ItemStack(MYCELIUM), DIRT, BROWN_MUSHROOM);

        //合成铁锭
        dyeRecipeMaterials.forEach(material -> {
            RecipeUtils.registerShaped(new ItemStack(IRON_INGOT),
                    WHITE_DYE, WHITE_DYE, WHITE_DYE,
                    WHITE_DYE, material, WHITE_DYE,
                    WHITE_DYE, WHITE_DYE, WHITE_DYE);
        });
        //合成金锭
        dyeRecipeMaterials.forEach(material -> {
            RecipeUtils.registerShaped(new ItemStack(GOLD_INGOT),
                    YELLOW_DYE, YELLOW_DYE, YELLOW_DYE,
                    YELLOW_DYE, material, YELLOW_DYE,
                    YELLOW_DYE, YELLOW_DYE, YELLOW_DYE);
        });
        //合成下届合金锭
        dyeRecipeMaterials.forEach(material -> {
            RecipeUtils.registerShaped(new ItemStack(NETHERITE_INGOT),
                    BLACK_DYE, BLACK_DYE, BLACK_DYE,
                    BLACK_DYE, material, BLACK_DYE,
                    BLACK_DYE, BLACK_DYE, BLACK_DYE);
        });
        //合成红砖
        dyeRecipeMaterials.forEach(material -> {
            RecipeUtils.registerShaped(new ItemStack(BRICK),
                    RED_DYE, RED_DYE, RED_DYE,
                    RED_DYE, material, RED_DYE,
                    RED_DYE, RED_DYE, RED_DYE);
        });
        //合成下届砖
        dyeRecipeMaterials.forEach(material -> {
            RecipeUtils.registerShaped(new ItemStack(NETHER_BRICK),
                    BROWN_DYE, BROWN_DYE, BROWN_DYE,
                    BROWN_DYE, material, BROWN_DYE,
                    BROWN_DYE, BROWN_DYE, BROWN_DYE);
        });
    }
}
