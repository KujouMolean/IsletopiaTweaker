package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.List;
import java.util.UUID;

import static org.bukkit.Material.*;

public class RegistRecipe {

    public RegistRecipe() {
        //萤石
        Bukkit.resetRecipes();
        registerShaped(new ItemStack(Material.GLOWSTONE),
                TORCH, TORCH, TORCH,
                TORCH, STONE, TORCH,
                TORCH, TORCH, TORCH);

        //合成粘土
        registerShaped(new ItemStack(CLAY_BALL, 32),
                DIRT, DIRT, DIRT,
                DIRT, SLIME_BALL, DIRT,
                DIRT, DIRT, DIRT);
        //合成凋零头
        registerShaped(new ItemStack(WITHER_SKELETON_SKULL),
                BLACK_DYE, BLACK_DYE, BLACK_DYE,
                BLACK_DYE, SKELETON_SKULL, BLACK_DYE,
                BLACK_DYE, BLACK_DYE, BLACK_DYE);

//        末地石
        registerSmithingRecipie(new ItemStack(END_STONE), STONE, ENDER_PEARL);
//        绯红菌岩
        registerSmithingRecipie(new ItemStack(CRIMSON_NYLIUM), NETHERRACK, CRIMSON_FUNGUS);
//        诡异菌岩
        registerSmithingRecipie(new ItemStack(WARPED_NYLIUM), NETHERRACK, WARPED_FUNGUS);
//        钻石
        registerCampfire(new ItemStack(DIAMOND), POISONOUS_POTATO, 1.0F, 600);
//        灵魂沙
        registerSmoking(new ItemStack(SOUL_SAND), SAND, 1.0F, 150);
//        地狱岩
        registerSmoking(new ItemStack(NETHERRACK), COBBLESTONE, 1.0F, 150);
//        灵魂土
        registerSmoking(new ItemStack(SOUL_SOIL), DIRT, 1.0F, 150);
//        砂砾
        registerStonecutting(new ItemStack(GRAVEL), COBBLESTONE);
//        砂砾
        registerBlasting(new ItemStack(GRAVEL), COBBLESTONE, 1.0F, 150);
//        沙子
        registerBlasting(new ItemStack(SAND), GRAVEL, 1.0F, 150);
//        石英
        registerFurnace(new ItemStack(QUARTZ), GLASS, 1.0F, 150);
//        烈焰粉
        registerBlasting(new ItemStack(BLAZE_POWDER), REDSTONE, 1.0F, 150);
        //转换树苗
        registerShapeless(new ItemStack(OAK_SAPLING), DARK_OAK_SAPLING);
        registerShapeless(new ItemStack(SPRUCE_SAPLING), OAK_SAPLING);
        registerShapeless(new ItemStack(BIRCH_SAPLING), SPRUCE_SAPLING);
        registerShapeless(new ItemStack(JUNGLE_SAPLING), BIRCH_SAPLING);
        registerShapeless(new ItemStack(ACACIA_SAPLING), JUNGLE_SAPLING);
        registerShapeless(new ItemStack(DARK_OAK_SAPLING), ACACIA_SAPLING);
        //合成烈焰棒
        registerShapeless(new ItemStack(BLAZE_ROD), BLAZE_POWDER, BLAZE_POWDER);
        //合成煤炭
        registerShapeless(new ItemStack(COAL), CHARCOAL);
        //合成菌丝
        registerSmithingRecipie(new ItemStack(MYCELIUM), DIRT, BROWN_MUSHROOM);

        //合成铁锭
        List<Material> dyeRecipeMaterials = List.of(GOLD_INGOT, IRON_INGOT, NETHERITE_INGOT, BRICK, NETHER_BRICK);
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(IRON_INGOT),
                    WHITE_DYE, WHITE_DYE, WHITE_DYE,
                    WHITE_DYE, material, WHITE_DYE,
                    WHITE_DYE, WHITE_DYE, WHITE_DYE);
        });
        //合成金锭
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(GOLD_INGOT),
                    YELLOW_DYE, YELLOW_DYE, YELLOW_DYE,
                    YELLOW_DYE, material, YELLOW_DYE,
                    YELLOW_DYE, YELLOW_DYE, YELLOW_DYE);
        });
        //合成下届合金锭
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(NETHERITE_INGOT),
                    BLACK_DYE, BLACK_DYE, BLACK_DYE,
                    BLACK_DYE, material, BLACK_DYE,
                    BLACK_DYE, BLACK_DYE, BLACK_DYE);
        });
        //合成红砖
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(BRICK),
                    RED_DYE, RED_DYE, RED_DYE,
                    RED_DYE, material, RED_DYE,
                    RED_DYE, RED_DYE, RED_DYE);
        });
        //合成下届砖
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(NETHER_BRICK),
                    BROWN_DYE, BROWN_DYE, BROWN_DYE,
                    BROWN_DYE, material, BROWN_DYE,
                    BROWN_DYE, BROWN_DYE, BROWN_DYE);
        });
    }
    public void registerShaped(ItemStack result, Material... materials) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, result);
        shapedRecipe.shape("ABC", "DEF", "GHI");
        shapedRecipe.setIngredient('A', materials[0]);
        shapedRecipe.setIngredient('B', materials[1]);
        shapedRecipe.setIngredient('C', materials[2]);
        shapedRecipe.setIngredient('D', materials[3]);
        shapedRecipe.setIngredient('E', materials[4]);
        shapedRecipe.setIngredient('F', materials[5]);
        shapedRecipe.setIngredient('G', materials[6]);
        shapedRecipe.setIngredient('H', materials[7]);
        shapedRecipe.setIngredient('I', materials[8]);
        Bukkit.addRecipe(shapedRecipe);
    }

    public void registerShapeless(ItemStack result, Material... materials) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(namespacedKey, result);
        for (Material material : materials) {
            shapelessRecipe.addIngredient(material);
        }
        Bukkit.addRecipe(shapelessRecipe);
    }

    public void registerStonecutting(ItemStack result, Material source) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        StonecuttingRecipe stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, source);
        Bukkit.addRecipe(stonecuttingRecipe);
    }

    public void registerSmithingRecipie(ItemStack result, Material source, Material addition) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        SmithingRecipe smithingRecipe = new SmithingRecipe(namespacedKey, result, new RecipeChoice.MaterialChoice(source), new RecipeChoice.MaterialChoice(addition));
        Bukkit.addRecipe(smithingRecipe);
    }

    public void registerBlasting(ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        BlastingRecipe blastingRecipe = new BlastingRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(blastingRecipe);
    }

    public void registerCampfire(ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        CampfireRecipe campfireRecipe = new CampfireRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(campfireRecipe);
    }

    public void registerFurnace(ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(furnaceRecipe);
    }

    public void registerSmoking(ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        SmokingRecipe smokingRecipe = new SmokingRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(smokingRecipe);
    }

}
