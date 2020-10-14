package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.ArrayList;
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

        LocalRecipe.addRecipe(GLOWSTONE, CRAFTING_TABLE, new ItemStack(GLOWSTONE),
                TORCH, TORCH, TORCH,
                TORCH, STONE, TORCH,
                TORCH, TORCH, TORCH);

        //合成粘土
        registerShaped(new ItemStack(CLAY_BALL, 32),
                DIRT, DIRT, DIRT,
                DIRT, SLIME_BALL, DIRT,
                DIRT, DIRT, DIRT);

        LocalRecipe.addRecipe(CLAY_BALL, CRAFTING_TABLE, new ItemStack(CLAY_BALL, 32),
                DIRT, DIRT, DIRT,
                DIRT, SLIME_BALL, DIRT,
                DIRT, DIRT, DIRT);

        //合成凋零头
        registerShaped(new ItemStack(WITHER_SKELETON_SKULL),
                BLACK_DYE, BLACK_DYE, BLACK_DYE,
                BLACK_DYE, SKELETON_SKULL, BLACK_DYE,
                BLACK_DYE, BLACK_DYE, BLACK_DYE);

        LocalRecipe.addRecipe(WITHER_SKELETON_SKULL, CRAFTING_TABLE, new ItemStack(WITHER_SKELETON_SKULL),
                BLACK_DYE, BLACK_DYE, BLACK_DYE,
                BLACK_DYE, SKELETON_SKULL, BLACK_DYE,
                BLACK_DYE, BLACK_DYE, BLACK_DYE);
        //合成蜘蛛丝
        registerShaped(new ItemStack(COBWEB),
                STRING, STRING, STRING,
                STRING, STRING, STRING,
                STRING, STRING, STRING);

        LocalRecipe.addRecipe(COBWEB, CRAFTING_TABLE, new ItemStack(COBWEB),
                STRING, STRING, STRING,
                STRING, STRING, STRING,
                STRING, STRING, STRING);


//        末地石
        registerSmithingRecipie(new ItemStack(END_STONE), STONE, ENDER_PEARL);

        LocalRecipe.addRecipe(END_STONE, SMITHING_TABLE,
                AIR, AIR, AIR,
                STONE, AIR, ENDER_PEARL,
                AIR, AIR, AIR);

//        绯红菌岩
        registerSmithingRecipie(new ItemStack(CRIMSON_NYLIUM), NETHERRACK, CRIMSON_FUNGUS);
        LocalRecipe.addRecipe(CRIMSON_NYLIUM, SMITHING_TABLE,
                AIR, AIR, AIR,
                NETHERRACK, AIR, CRIMSON_FUNGUS,
                AIR, AIR, AIR);
//        诡异菌岩
        registerSmithingRecipie(new ItemStack(WARPED_NYLIUM), NETHERRACK, WARPED_FUNGUS);
        LocalRecipe.addRecipe(WARPED_NYLIUM, SMITHING_TABLE,
                AIR, AIR, AIR,
                NETHERRACK, AIR, WARPED_FUNGUS,
                AIR, AIR, AIR);
//        钻石
        registerCampfire(new ItemStack(DIAMOND), POISONOUS_POTATO, 1.0F, 600);
        LocalRecipe.addRecipe(DIAMOND, CAMPFIRE,
                AIR, AIR, AIR,
                AIR, POISONOUS_POTATO, AIR,
                AIR, AIR, AIR);
//        灵魂沙
        registerSmoking(new ItemStack(SOUL_SAND), SAND, 1.0F, 150);
        LocalRecipe.addRecipe(SOUL_SAND, SMOKER,
                AIR, AIR, AIR,
                AIR, SAND, AIR,
                AIR, AIR, AIR);
//        地狱岩
        registerSmoking(new ItemStack(NETHERRACK), COBBLESTONE, 1.0F, 150);
        LocalRecipe.addRecipe(NETHERRACK, SMOKER,
                AIR, AIR, AIR,
                AIR, COBBLESTONE, AIR,
                AIR, AIR, AIR);
//        灵魂土
        registerSmoking(new ItemStack(SOUL_SOIL), DIRT, 1.0F, 150);
        LocalRecipe.addRecipe(SOUL_SOIL, SMOKER,
                AIR, AIR, AIR,
                AIR, DIRT, AIR,
                AIR, AIR, AIR);
//        砂砾
        registerStonecutting(new ItemStack(GRAVEL), COBBLESTONE);
        registerBlasting(new ItemStack(GRAVEL), COBBLESTONE, 1.0F, 150);

        {
            ItemStack[] itemStacks = new ItemStack[9];
            for (int i = 0; i < itemStacks.length; i++) {
                itemStacks[i] = new ItemStack(AIR);
            }
            itemStacks[4] = new ItemStack(COBBLESTONE);
            List<ItemStack[]> sources = new ArrayList<>();
            sources.add(itemStacks);
            LocalRecipe.addRecipe(
                    List.of(new ItemStack(GRAVEL)),
                    List.of(new ItemStack(BLAST_FURNACE), new ItemStack(STONECUTTER)),
                    sources,
                    List.of(new ItemStack(GRAVEL)));

        }


//        沙子
        registerBlasting(new ItemStack(SAND), GRAVEL, 1.0F, 150);
        LocalRecipe.addRecipe(SAND, BLAST_FURNACE,
                AIR, AIR, AIR,
                AIR, GRAVEL, AIR,
                AIR, AIR, AIR);
//        石英
        registerFurnace(new ItemStack(QUARTZ), GLASS, 1.0F, 150);
        LocalRecipe.addRecipe(QUARTZ, FURNACE,
                AIR, AIR, AIR,
                AIR, GLASS, AIR,
                AIR, AIR, AIR);
//        烈焰粉
        registerBlasting(new ItemStack(BLAZE_POWDER), REDSTONE, 1.0F, 150);
        LocalRecipe.addRecipe(BLAZE_POWDER, BLAST_FURNACE,
                AIR, AIR, AIR,
                AIR, REDSTONE, AIR,
                AIR, AIR, AIR);
        //转换树苗
        registerShapeless(new ItemStack(OAK_SAPLING), DARK_OAK_SAPLING);
        registerShapeless(new ItemStack(SPRUCE_SAPLING), OAK_SAPLING);
        registerShapeless(new ItemStack(BIRCH_SAPLING), SPRUCE_SAPLING);
        registerShapeless(new ItemStack(JUNGLE_SAPLING), BIRCH_SAPLING);
        registerShapeless(new ItemStack(ACACIA_SAPLING), JUNGLE_SAPLING);
        registerShapeless(new ItemStack(DARK_OAK_SAPLING), ACACIA_SAPLING);

        {

            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            for (Material material : List.of(DARK_OAK_SAPLING, OAK_SAPLING, SPRUCE_SAPLING, BIRCH_SAPLING, JUNGLE_SAPLING, ACACIA_SAPLING)) {
                icons.add(new ItemStack(material));
                result.add(new ItemStack(material));
            }

            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(CRAFTING_TABLE));

            List<ItemStack[]> sources = new ArrayList<>();
            for (Material material : List.of(OAK_SAPLING, SPRUCE_SAPLING, BIRCH_SAPLING, JUNGLE_SAPLING, ACACIA_SAPLING, DARK_OAK_SAPLING)) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(AIR);
                }
                itemStacks[4] = new ItemStack(material);
                sources.add(itemStacks);
            }


            LocalRecipe.addRecipe(icons, types, sources, result);
        }

        //合成烈焰棒
        registerShapeless(new ItemStack(BLAZE_ROD), BLAZE_POWDER, BLAZE_POWDER);
        LocalRecipe.addRecipe(BLAZE_ROD, CRAFTING_TABLE,
                AIR, BLAZE_POWDER, AIR,
                AIR, BLAZE_POWDER, AIR,
                AIR, AIR, AIR);
        //合成煤炭
        registerShapeless(new ItemStack(COAL), CHARCOAL);
        LocalRecipe.addRecipe(COAL, CRAFTING_TABLE,
                AIR, AIR, AIR,
                AIR, CHARCOAL, AIR,
                AIR, AIR, AIR);
        //合成菌丝
        registerSmithingRecipie(new ItemStack(MYCELIUM), DIRT, BROWN_MUSHROOM);
        LocalRecipe.addRecipe(MYCELIUM, SMITHING_TABLE,
                AIR, AIR, AIR,
                DIRT, AIR, BROWN_MUSHROOM,
                AIR, AIR, AIR);

        //合成红石矿
        registerShaped(new ItemStack(REDSTONE_ORE),
                REDSTONE, REDSTONE, REDSTONE,
                REDSTONE, STONE, REDSTONE,
                REDSTONE, REDSTONE, REDSTONE);
        //合成煤矿
        registerShaped(new ItemStack(COAL_ORE),
                COAL, COAL, COAL,
                COAL, STONE, COAL,
                COAL, COAL, COAL);
        //合成铁矿
        registerShaped(new ItemStack(IRON_ORE),
                IRON_INGOT, IRON_INGOT, IRON_INGOT,
                IRON_INGOT, STONE, IRON_INGOT,
                IRON_INGOT, IRON_INGOT, IRON_INGOT);
        //合成金矿
        registerShaped(new ItemStack(GOLD_ORE),
                GOLD_INGOT, GOLD_INGOT, GOLD_INGOT,
                GOLD_INGOT, STONE, GOLD_INGOT,
                GOLD_INGOT, GOLD_INGOT, GOLD_INGOT);

        //合成下届金矿石
        registerShaped(new ItemStack(GOLD_ORE),
                GOLD_INGOT, GOLD_INGOT, GOLD_INGOT,
                GOLD_INGOT, NETHERRACK, GOLD_INGOT,
                GOLD_INGOT, GOLD_INGOT, GOLD_INGOT);
        //合成青金石矿
        registerShaped(new ItemStack(LAPIS_ORE),
                LAPIS_BLOCK, LAPIS_BLOCK, LAPIS_BLOCK,
                LAPIS_BLOCK, STONE, LAPIS_BLOCK,
                LAPIS_BLOCK, LAPIS_BLOCK, LAPIS_BLOCK);
        //合成青钻石矿
        registerShaped(new ItemStack(DIAMOND_ORE),
                DIAMOND, DIAMOND, DIAMOND,
                DIAMOND, STONE, DIAMOND,
                DIAMOND, DIAMOND, DIAMOND);
        //合成绿宝石矿
        registerShaped(new ItemStack(EMERALD_ORE),
                EMERALD, EMERALD, EMERALD,
                EMERALD, STONE, EMERALD,
                EMERALD, EMERALD, EMERALD);
        //合成下届石英矿
        registerShaped(new ItemStack(NETHER_QUARTZ_ORE),
                QUARTZ, QUARTZ, QUARTZ,
                QUARTZ, NETHERRACK, QUARTZ,
                QUARTZ, QUARTZ, QUARTZ);

        {
            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            List<ItemStack[]> sources = new ArrayList<>();
            List<List<Material>> materials = new ArrayList<>();
            types.add(new ItemStack(CRAFTING_TABLE));

            icons.add(new ItemStack(REDSTONE_ORE));
            icons.add(new ItemStack(COAL_ORE));
            icons.add(new ItemStack(IRON_ORE));
            icons.add(new ItemStack(GOLD_ORE));
            icons.add(new ItemStack(NETHER_GOLD_ORE));
            icons.add(new ItemStack(LAPIS_ORE));
            icons.add(new ItemStack(DIAMOND_ORE));
            icons.add(new ItemStack(EMERALD_ORE));
            icons.add(new ItemStack(NETHER_QUARTZ_ORE));

            result.add(new ItemStack(REDSTONE_ORE));
            result.add(new ItemStack(COAL_ORE));
            result.add(new ItemStack(IRON_ORE));
            result.add(new ItemStack(GOLD_ORE));
            result.add(new ItemStack(NETHER_GOLD_ORE));
            result.add(new ItemStack(LAPIS_ORE));
            result.add(new ItemStack(DIAMOND_ORE));
            result.add(new ItemStack(EMERALD_ORE));
            result.add(new ItemStack(NETHER_QUARTZ_ORE));
            materials.add(List.of(
                    REDSTONE, REDSTONE, REDSTONE,
                    REDSTONE, STONE, REDSTONE,
                    REDSTONE, REDSTONE, REDSTONE

            ));
            materials.add(List.of(

                    COAL, COAL, COAL,
                    COAL, STONE, COAL,
                    COAL, COAL, COAL

            ));

            materials.add(List.of(
                    IRON_INGOT, IRON_INGOT, IRON_INGOT,
                    IRON_INGOT, STONE, IRON_INGOT,
                    IRON_INGOT, IRON_INGOT, IRON_INGOT
            ));
            materials.add(List.of(
                    GOLD_INGOT, GOLD_INGOT, GOLD_INGOT,
                    GOLD_INGOT, STONE, GOLD_INGOT,
                    GOLD_INGOT, GOLD_INGOT, GOLD_INGOT
            ));
            materials.add(List.of(
                    GOLD_INGOT, GOLD_INGOT, GOLD_INGOT,
                    GOLD_INGOT, NETHERRACK, GOLD_INGOT,
                    GOLD_INGOT, GOLD_INGOT, GOLD_INGOT
            ));

            materials.add(List.of(
                    LAPIS_BLOCK, LAPIS_BLOCK, LAPIS_BLOCK,
                    LAPIS_BLOCK, STONE, LAPIS_BLOCK,
                    LAPIS_BLOCK, LAPIS_BLOCK, LAPIS_BLOCK
            ));
            materials.add(List.of(
                    DIAMOND, DIAMOND, DIAMOND,
                    DIAMOND, STONE, DIAMOND,
                    DIAMOND, DIAMOND, DIAMOND
            ));
            materials.add(List.of(
                    EMERALD, EMERALD, EMERALD,
                    EMERALD, STONE, EMERALD,
                    EMERALD, EMERALD, EMERALD
            ));
            materials.add(List.of(
                    QUARTZ, QUARTZ, QUARTZ,
                    QUARTZ, NETHERRACK, QUARTZ,
                    QUARTZ, QUARTZ, QUARTZ
            ));
            for (List<Material> materialList : materials) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(materialList.get(i));
                }
                sources.add(itemStacks);
            }
            LocalRecipe.addRecipe(icons, types, sources, result);
        }

        //合成铁锭
        List<Material> dyeRecipeMaterials = List.of(GOLD_INGOT, IRON_INGOT, NETHERITE_INGOT, BRICK, NETHER_BRICK);
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(IRON_INGOT),
                    WHITE_DYE, WHITE_DYE, WHITE_DYE,
                    WHITE_DYE, material, WHITE_DYE,
                    WHITE_DYE, WHITE_DYE, WHITE_DYE);
        });

        {

            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(CRAFTING_TABLE));
            icons.add(new ItemStack(IRON_INGOT));
            icons.add(new ItemStack(IRON_INGOT));
            List<ItemStack[]> sources = new ArrayList<>();
            for (Material material : dyeRecipeMaterials) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(WHITE_DYE);
                }
                itemStacks[4] = new ItemStack(material);
                sources.add(itemStacks);
            }
            result.add(new ItemStack(IRON_INGOT));


            LocalRecipe.addRecipe(icons, types, sources, result);
        }

        //合成金锭
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(GOLD_INGOT),
                    YELLOW_DYE, YELLOW_DYE, YELLOW_DYE,
                    YELLOW_DYE, material, YELLOW_DYE,
                    YELLOW_DYE, YELLOW_DYE, YELLOW_DYE);
        });

        {

            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(CRAFTING_TABLE));
            icons.add(new ItemStack(GOLD_INGOT));
            icons.add(new ItemStack(GOLD_INGOT));
            List<ItemStack[]> sources = new ArrayList<>();
            for (Material material : dyeRecipeMaterials) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(YELLOW_DYE);
                }
                itemStacks[4] = new ItemStack(material);
                sources.add(itemStacks);
            }

            result.add(new ItemStack(GOLD_INGOT));
            LocalRecipe.addRecipe(icons, types, sources, result);
        }

        //合成下届合金锭
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(NETHERITE_INGOT),
                    BLACK_DYE, BLACK_DYE, BLACK_DYE,
                    BLACK_DYE, material, BLACK_DYE,
                    BLACK_DYE, BLACK_DYE, BLACK_DYE);
        });

        {

            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(CRAFTING_TABLE));
            icons.add(new ItemStack(NETHERITE_INGOT));
            icons.add(new ItemStack(NETHERITE_INGOT));
            List<ItemStack[]> sources = new ArrayList<>();
            for (Material material : dyeRecipeMaterials) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(BLACK_DYE);
                }
                itemStacks[4] = new ItemStack(material);
                sources.add(itemStacks);
            }
            result.add(new ItemStack(NETHERITE_INGOT));

            LocalRecipe.addRecipe(icons, types, sources, result);
        }

        //合成红砖
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(BRICK),
                    RED_DYE, RED_DYE, RED_DYE,
                    RED_DYE, material, RED_DYE,
                    RED_DYE, RED_DYE, RED_DYE);
        });

        {

            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(CRAFTING_TABLE));
            icons.add(new ItemStack(BRICK));
            icons.add(new ItemStack(BRICK));
            List<ItemStack[]> sources = new ArrayList<>();
            for (Material material : dyeRecipeMaterials) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(RED_DYE);
                }
                itemStacks[4] = new ItemStack(material);
                sources.add(itemStacks);
            }
            result.add(new ItemStack(BRICK));

            LocalRecipe.addRecipe(icons, types, sources, result);
        }

        //合成下届砖
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(NETHER_BRICK),
                    BROWN_DYE, BROWN_DYE, BROWN_DYE,
                    BROWN_DYE, material, BROWN_DYE,
                    BROWN_DYE, BROWN_DYE, BROWN_DYE);
        });

        {

            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(CRAFTING_TABLE));
            icons.add(new ItemStack(NETHER_BRICK));
            icons.add(new ItemStack(NETHER_BRICK));
            List<ItemStack[]> sources = new ArrayList<>();
            for (Material material : dyeRecipeMaterials) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(BROWN_DYE);
                }
                itemStacks[4] = new ItemStack(material);
                sources.add(itemStacks);
            }
            result.add(new ItemStack(NETHER_BRICK));

            LocalRecipe.addRecipe(icons, types, sources, result);
        }
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
