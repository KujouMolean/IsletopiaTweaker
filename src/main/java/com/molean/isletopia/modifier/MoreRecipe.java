package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Material.*;

@Singleton
public class MoreRecipe {

    public MoreRecipe() {
        Bukkit.resetRecipes();

        //发光浆果
        registerSmithingRecipie(new ItemStack(GLOW_BERRIES), SWEET_BERRIES, GLOWSTONE_DUST);
        LocalRecipe.addRecipe(GLOW_BERRIES, SMITHING_TABLE,
                AIR, AIR, AIR,
                SWEET_BERRIES, AIR, GLOWSTONE_DUST,
                AIR, AIR, AIR);

        //方解石
        registerFurnace(new ItemStack(CALCITE), DIORITE, 1.0F, 150);

        LocalRecipe.addRecipe(CALCITE, FURNACE,
                AIR, AIR, AIR,
                AIR, DIORITE, AIR,
                AIR, AIR, AIR);


        //凝灰岩
        registerStonecutting(new ItemStack(TUFF), BASALT);

        LocalRecipe.addRecipe(TUFF, STONECUTTER,
                AIR, AIR, AIR,
                AIR, BASALT, AIR,
                AIR, AIR, AIR);

        //潜声传感器
        registerShaped(new ItemStack(SCULK_SENSOR),
                GRASS_BLOCK, GRASS_BLOCK, GRASS_BLOCK,
                CRYING_OBSIDIAN, COMPARATOR, CRYING_OBSIDIAN,
                AIR, DAYLIGHT_DETECTOR, AIR);

        LocalRecipe.addRecipe(SCULK_SENSOR, CRAFTING_TABLE, new ItemStack(SCULK_SENSOR),
                GRASS_BLOCK, GRASS_BLOCK, GRASS_BLOCK,
                CRYING_OBSIDIAN, COMPARATOR, CRYING_OBSIDIAN,
                AIR, DAYLIGHT_DETECTOR, AIR);
//        //海绵
//        registerShaped(new ItemStack(SPONGE),
//                PRISMARINE_CRYSTALS, PRISMARINE_SHARD, PRISMARINE_CRYSTALS,
//                PRISMARINE_SHARD, HEART_OF_THE_SEA, PRISMARINE_SHARD,
//                PRISMARINE_CRYSTALS, PRISMARINE_SHARD, PRISMARINE_CRYSTALS);
//
//        LocalRecipe.addRecipe(SPONGE, CRAFTING_TABLE, new ItemStack(SPONGE),
//                PRISMARINE_CRYSTALS, PRISMARINE_SHARD, PRISMARINE_CRYSTALS,
//                PRISMARINE_SHARD, HEART_OF_THE_SEA, PRISMARINE_SHARD,
//                PRISMARINE_CRYSTALS, PRISMARINE_SHARD, PRISMARINE_CRYSTALS);


//        //紫水晶
//        registerShaped(new ItemStack(AMETHYST_SHARD),
//                PURPLE_DYE, QUARTZ, PURPLE_DYE,
//                QUARTZ, QUARTZ, QUARTZ,
//                PURPLE_DYE, QUARTZ, PURPLE_DYE);
//
//        LocalRecipe.addRecipe(AMETHYST_SHARD, CRAFTING_TABLE, new ItemStack(AMETHYST_SHARD),
//                PURPLE_DYE, QUARTZ, PURPLE_DYE,
//                QUARTZ, QUARTZ, QUARTZ,
//                PURPLE_DYE, QUARTZ, PURPLE_DYE);

        //远古残骸
        registerShaped(new ItemStack(ANCIENT_DEBRIS),
                BONE_BLOCK, BONE_BLOCK, BONE_BLOCK,
                BONE_BLOCK, NETHERITE_BLOCK, BONE_BLOCK,
                BONE_BLOCK, BONE_BLOCK, BONE_BLOCK);

        LocalRecipe.addRecipe(ANCIENT_DEBRIS, CRAFTING_TABLE, new ItemStack(ANCIENT_DEBRIS),
                BONE_BLOCK, BONE_BLOCK, BONE_BLOCK,
                BONE_BLOCK, NETHERITE_BLOCK, BONE_BLOCK,
                BONE_BLOCK, BONE_BLOCK, BONE_BLOCK);

        //深板岩
        registerSmithingRecipie(new ItemStack(DEEPSLATE), STONE_BRICKS, COAL);
        LocalRecipe.addRecipe(DEEPSLATE, SMITHING_TABLE,
                AIR, AIR, AIR,
                STONE_BRICKS, AIR, COAL,
                AIR, AIR, AIR);

        //枯萎灌木

        List<Material> saplings = List.of(OAK_SAPLING, SPRUCE_SAPLING, BIRCH_SAPLING, JUNGLE_SAPLING, ACACIA_SAPLING, DARK_OAK_SAPLING);
        for (Material sapling : saplings) {
            registerSmoking(new ItemStack(DEAD_BUSH), sapling, 0, 150);
        }
        {

            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(SMOKER));
            icons.add(new ItemStack(DEAD_BUSH));
            List<ItemStack[]> sources = new ArrayList<>();
            for (Material material : saplings) {
                ItemStack[] itemStacks = new ItemStack[9];
                itemStacks[4] = new ItemStack(material);
                sources.add(itemStacks);
            }
            result.add(new ItemStack(DEAD_BUSH));
            LocalRecipe.addRecipe(icons, types, sources, result);
        }


        //萤石
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

//        合成凋零头
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

//        绯红菌岩 诡异菌岩 菌丝
        registerSmithingRecipie(new ItemStack(CRIMSON_NYLIUM), NETHERRACK, CRIMSON_FUNGUS);
        registerSmithingRecipie(new ItemStack(WARPED_NYLIUM), NETHERRACK, WARPED_FUNGUS);
        registerSmithingRecipie(new ItemStack(MYCELIUM), DIRT, BROWN_MUSHROOM);
        {
            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            for (Material material : List.of(CRIMSON_NYLIUM,WARPED_NYLIUM,MYCELIUM)) {
                icons.add(new ItemStack(material));
                result.add(new ItemStack(material));
            }
            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(SMITHING_TABLE));
            List<ItemStack[]> sources = new ArrayList<>();
            List<Material> originBlocks = List.of(NETHERRACK, NETHERRACK, DIRT);
            List<Material> appendItem = List.of(CRIMSON_FUNGUS, WARPED_FUNGUS, BROWN_MUSHROOM);
            for (int i = 0; i < originBlocks.size(); i++) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int j = 0; j < itemStacks.length; j++) {
                    itemStacks[j] = new ItemStack(AIR);
                }
                itemStacks[3] = new ItemStack(originBlocks.get(i));
                itemStacks[5] = new ItemStack(appendItem.get(i));
                sources.add(itemStacks);
            }
            LocalRecipe.addRecipe(icons, types, sources, result);
        }

//        钻石
        registerCampfire(new ItemStack(DIAMOND), POISONOUS_POTATO, 1.0F, 600);
        LocalRecipe.addRecipe(DIAMOND, CAMPFIRE,
                AIR, AIR, AIR,
                AIR, POISONOUS_POTATO, AIR,
                AIR, AIR, AIR);



//        灵魂沙 灵魂土 地狱岩
        registerSmoking(new ItemStack(SOUL_SAND), SAND, 1.0F, 150);
        registerSmoking(new ItemStack(SOUL_SOIL), DIRT, 1.0F, 150);
        registerSmoking(new ItemStack(NETHERRACK), COBBLESTONE, 1.0F, 150);
        {
            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            for (Material material : List.of(SOUL_SAND,SOUL_SOIL,NETHERRACK)) {
                icons.add(new ItemStack(material));
                result.add(new ItemStack(material));
            }
            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(SMOKER));
            List<ItemStack[]> sources = new ArrayList<>();
            for (Material material : List.of(SAND,DIRT,COBBLESTONE)) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(AIR);
                }
                itemStacks[4] = new ItemStack(material);
                sources.add(itemStacks);
            }
            LocalRecipe.addRecipe(icons, types, sources, result);
        }


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

        //合成红石矿
        registerShaped(new ItemStack(REDSTONE_ORE),
                REDSTONE_BLOCK, REDSTONE_BLOCK, REDSTONE_BLOCK,
                REDSTONE_BLOCK, STONE, REDSTONE_BLOCK,
                REDSTONE_BLOCK, REDSTONE_BLOCK, REDSTONE_BLOCK);
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
        registerShaped(new ItemStack(NETHER_GOLD_ORE),
                GOLD_INGOT, GOLD_INGOT, GOLD_INGOT,
                GOLD_INGOT, NETHERRACK, GOLD_INGOT,
                GOLD_INGOT, GOLD_INGOT, GOLD_INGOT);
        //合成black金矿石
        registerShaped(new ItemStack(GILDED_BLACKSTONE),
                GOLD_INGOT, GOLD_INGOT, GOLD_INGOT,
                GOLD_INGOT, BLACKSTONE, GOLD_INGOT,
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
        //合成copper ore
        registerShaped(new ItemStack(COPPER_ORE),
                COPPER_INGOT, COPPER_INGOT, COPPER_INGOT,
                COPPER_INGOT, STONE, COPPER_INGOT,
                COPPER_INGOT, COPPER_INGOT, COPPER_INGOT);


        //合成deep coal
        registerShaped(new ItemStack(DEEPSLATE_COAL_ORE),
                COAL, COAL, COAL,
                COAL, DEEPSLATE, COAL,
                COAL, COAL, COAL);
        //合成deep iron
        registerShaped(new ItemStack(DEEPSLATE_IRON_ORE),
                IRON_INGOT, IRON_INGOT, IRON_INGOT,
                IRON_INGOT, DEEPSLATE, IRON_INGOT,
                IRON_INGOT, IRON_INGOT, IRON_INGOT);
        //合成deep copper
        registerShaped(new ItemStack(DEEPSLATE_COPPER_ORE),
                COPPER_INGOT, COPPER_INGOT, COPPER_INGOT,
                COPPER_INGOT, DEEPSLATE, COPPER_INGOT,
                COPPER_INGOT, COPPER_INGOT, COPPER_INGOT);
        //合成gold copper
        registerShaped(new ItemStack(DEEPSLATE_GOLD_ORE),
                GOLD_INGOT, GOLD_INGOT, GOLD_INGOT,
                GOLD_INGOT, DEEPSLATE, GOLD_INGOT,
                GOLD_INGOT, GOLD_INGOT, GOLD_INGOT);
        //合成deep redstone
        registerShaped(new ItemStack(DEEPSLATE_REDSTONE_ORE),
                REDSTONE_BLOCK, REDSTONE_BLOCK, REDSTONE_BLOCK,
                REDSTONE_BLOCK, DEEPSLATE, REDSTONE_BLOCK,
                REDSTONE_BLOCK, REDSTONE_BLOCK, REDSTONE_BLOCK);
        //合成deep emerald
        registerShaped(new ItemStack(DEEPSLATE_EMERALD_ORE),
                EMERALD, EMERALD, EMERALD,
                EMERALD, DEEPSLATE, EMERALD,
                EMERALD, EMERALD, EMERALD);
        //合成deep lapis
        registerShaped(new ItemStack(DEEPSLATE_LAPIS_ORE),
                LAPIS_BLOCK, LAPIS_BLOCK, LAPIS_BLOCK,
                LAPIS_BLOCK, DEEPSLATE, LAPIS_BLOCK,
                LAPIS_BLOCK, LAPIS_BLOCK, LAPIS_BLOCK);
        //合成deep diamond
        registerShaped(new ItemStack(DEEPSLATE_DIAMOND_ORE),
                DIAMOND, DIAMOND, DIAMOND,
                DIAMOND, DEEPSLATE, DIAMOND,
                DIAMOND, DIAMOND, DIAMOND);

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
            icons.add(new ItemStack(GILDED_BLACKSTONE));
            icons.add(new ItemStack(LAPIS_ORE));
            icons.add(new ItemStack(DIAMOND_ORE));
            icons.add(new ItemStack(EMERALD_ORE));
            icons.add(new ItemStack(NETHER_QUARTZ_ORE));
            icons.add(new ItemStack(COPPER_ORE));
            icons.add(new ItemStack(DEEPSLATE_COAL_ORE));
            icons.add(new ItemStack(DEEPSLATE_IRON_ORE));
            icons.add(new ItemStack(DEEPSLATE_COPPER_ORE));
            icons.add(new ItemStack(DEEPSLATE_GOLD_ORE));
            icons.add(new ItemStack(DEEPSLATE_REDSTONE_ORE));
            icons.add(new ItemStack(DEEPSLATE_EMERALD_ORE));
            icons.add(new ItemStack(DEEPSLATE_LAPIS_ORE));
            icons.add(new ItemStack(DEEPSLATE_DIAMOND_ORE));

            result.add(new ItemStack(REDSTONE_ORE));
            result.add(new ItemStack(COAL_ORE));
            result.add(new ItemStack(IRON_ORE));
            result.add(new ItemStack(GOLD_ORE));
            result.add(new ItemStack(NETHER_GOLD_ORE));
            result.add(new ItemStack(GILDED_BLACKSTONE));
            result.add(new ItemStack(LAPIS_ORE));
            result.add(new ItemStack(DIAMOND_ORE));
            result.add(new ItemStack(EMERALD_ORE));
            result.add(new ItemStack(NETHER_QUARTZ_ORE));
            result.add(new ItemStack(COPPER_ORE));
            result.add(new ItemStack(DEEPSLATE_COAL_ORE));
            result.add(new ItemStack(DEEPSLATE_IRON_ORE));
            result.add(new ItemStack(DEEPSLATE_COPPER_ORE));
            result.add(new ItemStack(DEEPSLATE_GOLD_ORE));
            result.add(new ItemStack(DEEPSLATE_REDSTONE_ORE));
            result.add(new ItemStack(DEEPSLATE_EMERALD_ORE));
            result.add(new ItemStack(DEEPSLATE_LAPIS_ORE));
            result.add(new ItemStack(DEEPSLATE_DIAMOND_ORE));
            materials.add(List.of(
                    REDSTONE_BLOCK, REDSTONE_BLOCK, REDSTONE_BLOCK,
                    REDSTONE_BLOCK, STONE, REDSTONE_BLOCK,
                    REDSTONE_BLOCK, REDSTONE_BLOCK, REDSTONE_BLOCK
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
                    GOLD_INGOT, GOLD_INGOT, GOLD_INGOT,
                    GOLD_INGOT, BLACKSTONE, GOLD_INGOT,
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
            materials.add(List.of(
                    COPPER_INGOT, COPPER_INGOT, COPPER_INGOT,
                    COPPER_INGOT, STONE, COPPER_INGOT,
                    COPPER_INGOT, COPPER_INGOT, COPPER_INGOT
            ));


            //合成deep coal
            materials.add(List.of(
                    COAL, COAL, COAL,
                    COAL, DEEPSLATE, COAL,
                    COAL, COAL, COAL));
            //合成deep iron
            materials.add(List.of(
                    IRON_INGOT, IRON_INGOT, IRON_INGOT,
                    IRON_INGOT, DEEPSLATE, IRON_INGOT,
                    IRON_INGOT, IRON_INGOT, IRON_INGOT));
            //合成deep copper
            materials.add(List.of(
                    COPPER_INGOT, COPPER_INGOT, COPPER_INGOT,
                    COPPER_INGOT, DEEPSLATE, COPPER_INGOT,
                    COPPER_INGOT, COPPER_INGOT, COPPER_INGOT));
            //合成gold copper
            materials.add(List.of(
                    GOLD_INGOT, GOLD_INGOT, GOLD_INGOT,
                    GOLD_INGOT, DEEPSLATE, GOLD_INGOT,
                    GOLD_INGOT, GOLD_INGOT, GOLD_INGOT));
            //合成deep redstone
            materials.add(List.of(
                    REDSTONE_BLOCK, REDSTONE_BLOCK, REDSTONE_BLOCK,
                    REDSTONE_BLOCK, DEEPSLATE, REDSTONE_BLOCK,
                    REDSTONE_BLOCK, REDSTONE_BLOCK, REDSTONE_BLOCK));
            //合成deep emerald
            materials.add(List.of(
                    EMERALD, EMERALD, EMERALD,
                    EMERALD, DEEPSLATE, EMERALD,
                    EMERALD, EMERALD, EMERALD));
            //合成deep lapis
            materials.add(List.of(
                    LAPIS_BLOCK, LAPIS_BLOCK, LAPIS_BLOCK,
                    LAPIS_BLOCK, DEEPSLATE, LAPIS_BLOCK,
                    LAPIS_BLOCK, LAPIS_BLOCK, LAPIS_BLOCK));
            //合成deep diamond
            materials.add(List.of(
                    DIAMOND, DIAMOND, DIAMOND,
                    DIAMOND, DEEPSLATE, DIAMOND,
                    DIAMOND, DIAMOND, DIAMOND));

            for (List<Material> materialList : materials) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(materialList.get(i));
                }
                sources.add(itemStacks);
            }
            LocalRecipe.addRecipe(icons, types, sources, result);
        }


        List<Material> dyeRecipeMaterials = List.of(GOLD_INGOT, IRON_INGOT, NETHERITE_INGOT, BRICK, NETHER_BRICK, COPPER_INGOT);
        //合成铁锭
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

        //合成铜
        dyeRecipeMaterials.forEach(material -> {
            registerShaped(new ItemStack(COPPER_INGOT),
                    ORANGE_DYE, ORANGE_DYE, ORANGE_DYE,
                    ORANGE_DYE, material, ORANGE_DYE,
                    ORANGE_DYE, ORANGE_DYE, ORANGE_DYE);
        });

        {

            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(new ItemStack(CRAFTING_TABLE));
            icons.add(new ItemStack(COPPER_INGOT));
            icons.add(new ItemStack(COPPER_INGOT));
            List<ItemStack[]> sources = new ArrayList<>();
            for (Material material : dyeRecipeMaterials) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = new ItemStack(ORANGE_DYE);
                }
                itemStacks[4] = new ItemStack(material);
                sources.add(itemStacks);
            }
            result.add(new ItemStack(COPPER_INGOT));

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
