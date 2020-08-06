package com.molean.isletopia.tweakers.tweakers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.*;

public class RegistRecipe {
    public RegistRecipe() {
        //萤石
        Bukkit.resetRecipes();
        RecipeUtils.registerShaped("tweaker_craft_glowstone", new ItemStack(Material.GLOWSTONE),
                TORCH, TORCH, TORCH,
                TORCH, STONE, TORCH,
                TORCH, TORCH, TORCH);

        //合成粘土
        RecipeUtils.registerShaped("tweaker_craft_clay", new ItemStack(CLAY_BALL, 32),
                DIRT, DIRT, DIRT,
                DIRT, BLACK_DYE, DIRT,
                DIRT, DIRT, DIRT);

        //合成金锭
        RecipeUtils.registerShaped("tweaker_craft_gold_ingot", new ItemStack(GOLD_INGOT),
                YELLOW_DYE, YELLOW_DYE, YELLOW_DYE,
                YELLOW_DYE, IRON_INGOT, YELLOW_DYE,
                YELLOW_DYE, YELLOW_DYE, YELLOW_DYE);

        //合成岩浆
        RecipeUtils.registerShaped("tweaker_craft_lava_bucket", new ItemStack(LAVA_BUCKET),
                BLAZE_POWDER, BLAZE_POWDER, BLAZE_POWDER,
                BLAZE_POWDER, BUCKET, BLAZE_POWDER,
                BLAZE_POWDER, BLAZE_POWDER, BLAZE_POWDER);

        RecipeUtils.registerSmithingRecipie("tweaker_smithing_end_stone", new ItemStack(END_STONE), STONE, ENDER_PEARL);
        RecipeUtils.registerSmithingRecipie("tweaker_smithing_crimson_nylium", new ItemStack(CRIMSON_NYLIUM), NETHERRACK, CRIMSON_FUNGUS);
        RecipeUtils.registerSmithingRecipie("tweaker_smithing_warped_nylium", new ItemStack(WARPED_NYLIUM), NETHERRACK, WARPED_FUNGUS);
        RecipeUtils.registerCampfire("tweaker_campfire_warped_nylium", new ItemStack(IRON_NUGGET), CARROT, 1.0F, 60);
        RecipeUtils.registerCampfire("tweaker_campfire_diamond", new ItemStack(DIAMOND), POISONOUS_POTATO, 1.0F, 600);
        RecipeUtils.registerSmoking("tweaker_smoking_soul_sand", new ItemStack(SOUL_SAND), SAND, 1.0F, 150);
        RecipeUtils.registerSmoking("tweaker_smoking_netherrack", new ItemStack(NETHERRACK), COBBLESTONE, 1.0F, 150);
        RecipeUtils.registerSmoking("tweaker_smoking_soul_SOIL", new ItemStack(SOUL_SOIL), DIRT, 1.0F, 150);
        RecipeUtils.registerStonecutting("tweaker_stonecut_gravel", new ItemStack(GRAVEL), COBBLESTONE);
        RecipeUtils.registerBlasting("tweaker_blasting_gravel", new ItemStack(GRAVEL), COBBLESTONE, 1.0F, 150);
        RecipeUtils.registerBlasting("tweaker_blasting_sand", new ItemStack(SAND), GRAVEL, 1.0F, 150);
        RecipeUtils.registerFurnace("tweaker_furnace_quartz", new ItemStack(QUARTZ), GLASS, 1.0F, 150);
        RecipeUtils.registerBlasting("tweaker_blasting_blaze_powder", new ItemStack(BLAZE_POWDER), REDSTONE_BLOCK, 1.0F, 150);
        //转换树苗
        RecipeUtils.registerShapeless("tweaker_craft_oak_sapling", new ItemStack(OAK_SAPLING), DARK_OAK_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_spruce_sapling", new ItemStack(SPRUCE_SAPLING), OAK_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_birch_sapling", new ItemStack(BIRCH_SAPLING), SPRUCE_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_jungle_sapling", new ItemStack(JUNGLE_SAPLING), BIRCH_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_acacia_sapling", new ItemStack(ACACIA_SAPLING), JUNGLE_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_dark_oak_sapling", new ItemStack(DARK_OAK_SAPLING), ACACIA_SAPLING);
        //合成烈焰棒
        RecipeUtils.registerShapeless("tweaker_craft_blaze_rod", new ItemStack(BLAZE_ROD), BLAZE_POWDER, BLAZE_POWDER);
        //合成煤炭
        RecipeUtils.registerShapeless("tweaker_craft_coal", new ItemStack(COAL), CHARCOAL);
    }
}
