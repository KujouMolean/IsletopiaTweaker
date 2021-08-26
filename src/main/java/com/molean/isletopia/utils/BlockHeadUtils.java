package com.molean.isletopia.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sk89q.worldedit.bukkit.fastutil.Hash;
import org.bukkit.Material;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.bukkit.Material.*;
import static org.bukkit.Material.BIG_DRIPLEAF_STEM;

public class BlockHeadUtils {
    public static class HeadBlock {
        private String name;
        private UUID uuid;
        private String value;

        public HeadBlock() {
        }

        public HeadBlock(String name, UUID uuid, String value) {
            this.name = name;
            this.uuid = uuid;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static Map<Material, Set<String>> getBlockHeadMap(){
        Map<Material, Set<String>> blocks = new HashMap<>();
        Gson gson = new Gson();
        InputStream resourceAsStream = BlockHeadUtils.class.getClassLoader().getResourceAsStream("blocks.json");
        assert resourceAsStream != null;
        String blocksJsonString = null;
        try {
            blocksJsonString = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonParser jsonParser = new JsonParser();
        assert blocksJsonString != null;
        JsonElement parse = jsonParser.parse(blocksJsonString);
        JsonArray asJsonArray = parse.getAsJsonArray();
        List<HeadBlock> headBlocks = new ArrayList<>();


        asJsonArray.forEach(jsonElement -> {
            String s1 = jsonElement.toString();
            HeadBlock headBlock = gson.fromJson(s1, HeadBlock.class);
            headBlocks.add(headBlock);
        });

        Map<Material, String> map = new HashMap<>();
        map.put(AIR, null);
        map.put(POLISHED_DEEPSLATE, null);
        map.put(DEEPSLATE_COAL_ORE, null);
        map.put(DEEPSLATE_IRON_ORE, null);
        map.put(DEEPSLATE_COPPER_ORE, null);
        map.put(DEEPSLATE_LAPIS_ORE, "Deepslate Lapis Lazuli Ore");
        map.put(RAW_IRON_BLOCK, null);
        map.put(RAW_GOLD_BLOCK, null);
        map.put(STRIPPED_CRIMSON_HYPHAE, null);
        map.put(STRIPPED_WARPED_HYPHAE, null);
        map.put(COBWEB, null);
        map.put(FERN, null);
        map.put(AZALEA, null);
        map.put(FLOWERING_AZALEA, null);
        map.put(DEAD_BUSH, null);
        map.put(SEAGRASS, null);
        map.put(SEA_PICKLE, null);
        map.put(WHITE_WOOL, "Wool (white)");
        map.put(ORANGE_WOOL, "Wool (orange)");
        map.put(MAGENTA_WOOL, "Wool (magenta)");
        map.put(LIGHT_BLUE_WOOL, "Wool (light blue)");
        map.put(YELLOW_WOOL, "Wool (yellow)");
        map.put(LIME_WOOL, "Wool (lime)");
        map.put(PINK_WOOL, "Wool (pink)");
        map.put(GRAY_WOOL, "Wool (gray)");
        map.put(LIGHT_GRAY_WOOL, "Wool (light gray)");
        map.put(CYAN_WOOL, "Wool (cyan)");
        map.put(PURPLE_WOOL, "Wool (purple)");
        map.put(BLUE_WOOL, "Wool (blue)");
        map.put(BROWN_WOOL, "Wool (brown)");
        map.put(GREEN_WOOL, "Wool (green)");
        map.put(RED_WOOL, "Wool (red)");
        map.put(BLACK_WOOL, "Wool (black)");
        map.put(DANDELION, null);
        map.put(POPPY, null);
        map.put(BLUE_ORCHID, null);
        map.put(ALLIUM, null);
        map.put(AZURE_BLUET, null);
        map.put(RED_TULIP, null);
        map.put(ORANGE_TULIP, null);
        map.put(WHITE_TULIP, null);
        map.put(PINK_TULIP, null);
        map.put(OXEYE_DAISY, null);
        map.put(CORNFLOWER, null);
        map.put(LILY_OF_THE_VALLEY, null);
        map.put(WITHER_ROSE, null);
        map.put(SPORE_BLOSSOM, null);
        map.put(CRIMSON_FUNGUS, null);
        map.put(WARPED_FUNGUS, null);
        map.put(CRIMSON_ROOTS, null);
        map.put(WARPED_ROOTS, null);
        map.put(NETHER_SPROUTS, null);
        map.put(WEEPING_VINES, null);
        map.put(TWISTING_VINES, null);
        map.put(SUGAR_CANE, null);
        map.put(KELP, null);
        map.put(MOSS_CARPET, null);
        map.put(HANGING_ROOTS, null);
        map.put(BIG_DRIPLEAF, null);
        map.put(SMALL_DRIPLEAF, null);
        map.put(BAMBOO, null);
        map.put(BOOKSHELF, null);
        map.put(TORCH, null);
        map.put(END_ROD, null);
        map.put(CHORUS_PLANT, null);
        map.put(SPAWNER, null);
        map.put(CHEST, null);
        map.put(CRAFTING_TABLE, null);
        map.put(FARMLAND, "Dry Farmland");
        map.put(FURNACE, null);
        map.put(LADDER, null);
        map.put(CACTUS, null);
        map.put(JUKEBOX, null);
        map.put(PUMPKIN, null);
        map.put(CARVED_PUMPKIN, null);
        map.put(JACK_O_LANTERN, null);
        map.put(SMOOTH_BASALT, null);
        map.put(SOUL_TORCH, null);
        map.put(INFESTED_STONE, null);
        map.put(INFESTED_COBBLESTONE, null);
        map.put(INFESTED_STONE_BRICKS, null);
        map.put(INFESTED_MOSSY_STONE_BRICKS, null);
        map.put(INFESTED_CRACKED_STONE_BRICKS, null);
        map.put(INFESTED_CHISELED_STONE_BRICKS, null);
        map.put(INFESTED_DEEPSLATE, null);
        map.put(CRACKED_DEEPSLATE_BRICKS, null);
        map.put(DEEPSLATE_TILES, null);
        map.put(CRACKED_DEEPSLATE_TILES, null);
        map.put(CHISELED_DEEPSLATE, null);
        map.put(IRON_BARS, null);
        map.put(CHAIN, null);
        map.put(GLASS_PANE, null);
        map.put(MELON, null);
        map.put(VINE, null);
        map.put(GLOW_LICHEN, null);
        map.put(LILY_PAD, null);
        map.put(CHISELED_NETHER_BRICKS, "Chiseled Nether Brick");
        map.put(ENCHANTING_TABLE, null);
        map.put(END_PORTAL_FRAME, "Endportal Frame");
        map.put(DRAGON_EGG, null);
        map.put(ENDER_CHEST, null);
        map.put(COMMAND_BLOCK, null);
        map.put(BEACON, null);
        map.put(CHISELED_QUARTZ_BLOCK, "Chiseled Quartz");
        map.put(WHITE_TERRACOTTA, "Terracotta (white)");
        map.put(ORANGE_TERRACOTTA, "Terracotta (orange)");
        map.put(MAGENTA_TERRACOTTA, "Terracotta (magenta)");
        map.put(LIGHT_BLUE_TERRACOTTA, "Terracotta (light blue)");
        map.put(YELLOW_TERRACOTTA, "Terracotta (yellow)");
        map.put(LIME_TERRACOTTA, "Terracotta (lime)");
        map.put(PINK_TERRACOTTA, "Terracotta (pink)");
        map.put(GRAY_TERRACOTTA, "Terracotta (gray)");
        map.put(LIGHT_GRAY_TERRACOTTA, "Terracotta (light gray)");
        map.put(CYAN_TERRACOTTA, "Terracotta (cyan)");
        map.put(PURPLE_TERRACOTTA, "Terracotta (purple)");
        map.put(BLUE_TERRACOTTA, "Terracotta (blue)");
        map.put(BROWN_TERRACOTTA, "Terracotta (brown)");
        map.put(GREEN_TERRACOTTA, "Terracotta (green)");
        map.put(RED_TERRACOTTA, "Terracotta (red)");
        map.put(BLACK_TERRACOTTA, "Terracotta (black)");
        map.put(LIGHT, null);
        map.put(HAY_BLOCK, null);
        map.put(DIRT_PATH, "Path Block");
        map.put(SUNFLOWER, null);
        map.put(LILAC, null);
        map.put(ROSE_BUSH, null);
        map.put(PEONY, null);
        map.put(TALL_GRASS, null);
        map.put(LARGE_FERN, null);
        map.put(REPEATING_COMMAND_BLOCK, null);
        map.put(CHAIN_COMMAND_BLOCK, null);
        map.put(WHITE_GLAZED_TERRACOTTA, "Glazed Terracotta (white)");
        map.put(ORANGE_GLAZED_TERRACOTTA, "Glazed Terracotta (orange)");
        map.put(MAGENTA_GLAZED_TERRACOTTA, "Glazed Terracotta (magenta)");
        map.put(LIGHT_BLUE_GLAZED_TERRACOTTA, "Glazed Terracotta (light blue)");
        map.put(YELLOW_GLAZED_TERRACOTTA, "Glazed Terracotta (yellow)");
        map.put(LIME_GLAZED_TERRACOTTA, "Glazed Terracotta (lime)");
        map.put(PINK_GLAZED_TERRACOTTA, "Glazed Terracotta (pink)");
        map.put(GRAY_GLAZED_TERRACOTTA, "Glazed Terracotta (gray)");
        map.put(LIGHT_GRAY_GLAZED_TERRACOTTA, "Glazed Terracotta (light gray)");
        map.put(CYAN_GLAZED_TERRACOTTA, "Glazed Terracotta (cyan)");
        map.put(PURPLE_GLAZED_TERRACOTTA, "Glazed Terracotta (purple)");
        map.put(BLUE_GLAZED_TERRACOTTA, "Glazed Terracotta (blue)");
        map.put(BROWN_GLAZED_TERRACOTTA, "Glazed Terracotta (brown)");
        map.put(GREEN_GLAZED_TERRACOTTA, "Glazed Terracotta (green)");
        map.put(RED_GLAZED_TERRACOTTA, "Glazed Terracotta (red)");
        map.put(BLACK_GLAZED_TERRACOTTA, "Glazed Terracotta (black)");
        map.put(WHITE_CONCRETE, "Concrete (white)");
        map.put(ORANGE_CONCRETE, "Concrete (orange)");
        map.put(MAGENTA_CONCRETE, "Concrete (magenta)");
        map.put(LIGHT_BLUE_CONCRETE, "Concrete (light blue)");
        map.put(YELLOW_CONCRETE, "Concrete (yellow)");
        map.put(LIME_CONCRETE, "Concrete (lime)");
        map.put(PINK_CONCRETE, "Concrete (pink)");
        map.put(GRAY_CONCRETE, "Concrete (gray)");
        map.put(LIGHT_GRAY_CONCRETE, "Concrete (light gray)");
        map.put(CYAN_CONCRETE, "Concrete (cyan)");
        map.put(PURPLE_CONCRETE, "Concrete (purple)");
        map.put(BLUE_CONCRETE, "Concrete (blue)");
        map.put(BROWN_CONCRETE, "Concrete (brown)");
        map.put(GREEN_CONCRETE, "Concrete (green)");
        map.put(RED_CONCRETE, "Concrete (red)");
        map.put(BLACK_CONCRETE, "Concrete (black)");
        map.put(WHITE_CONCRETE_POWDER, "Concrete Powder (white)");
        map.put(ORANGE_CONCRETE_POWDER, "Concrete Powder (orange)");
        map.put(MAGENTA_CONCRETE_POWDER, "Concrete Powder (magenta)");
        map.put(LIGHT_BLUE_CONCRETE_POWDER, "Concrete Powder (light blue)");
        map.put(YELLOW_CONCRETE_POWDER, "Concrete Powder (yellow)");
        map.put(LIME_CONCRETE_POWDER, "Concrete Powder (lime)");
        map.put(PINK_CONCRETE_POWDER, "Concrete Powder (pink)");
        map.put(GRAY_CONCRETE_POWDER, "Concrete Powder (gray)");
        map.put(LIGHT_GRAY_CONCRETE_POWDER, null);
        map.put(CYAN_CONCRETE_POWDER, "Concrete Powder (cyan)");
        map.put(PURPLE_CONCRETE_POWDER, "Concrete Powder (purple)");
        map.put(BLUE_CONCRETE_POWDER, "Concrete Powder (blue)");
        map.put(BROWN_CONCRETE_POWDER, "Concrete Powder (brown)");
        map.put(GREEN_CONCRETE_POWDER, "Concrete Powder (green)");
        map.put(RED_CONCRETE_POWDER, "Concrete Powder (red)");
        map.put(BLACK_CONCRETE_POWDER, "Concrete Powder (black)");
        map.put(TURTLE_EGG, null);
        map.put(STRUCTURE_BLOCK, null);
        map.put(JIGSAW, null);
        map.put(WHEAT, null);
        map.put(CONDUIT, null);
        map.put(SCAFFOLDING, null);
        map.put(REDSTONE_TORCH, null);
        map.put(REPEATER, null);
        map.put(COMPARATOR, null);
        map.put(STICKY_PISTON, null);
        map.put(OBSERVER, null);
        map.put(DISPENSER, null);
        map.put(DROPPER, null);
        map.put(LECTERN, null);
        map.put(TARGET, null);
        map.put(LEVER, null);
        map.put(LIGHTNING_ROD, null);
        map.put(DAYLIGHT_DETECTOR, null);
        map.put(SCULK_SENSOR, null);
        map.put(TRIPWIRE_HOOK, null);
        map.put(TRAPPED_CHEST, null);
        map.put(TNT, null);
        map.put(REDSTONE_LAMP, null);
        map.put(NOTE_BLOCK, null);
        map.put(DRIED_KELP_BLOCK, null);
        map.put(CAKE, null);
        map.put(BREWING_STAND, null);
        map.put(CAULDRON, null);
        map.put(FLOWER_POT, null);
        map.put(SKELETON_SKULL, null);
        map.put(WITHER_SKELETON_SKULL, null);
        map.put(LOOM, null);
        map.put(COMPOSTER, null);
        map.put(BARREL, null);
        map.put(SMOKER, null);
        map.put(BLAST_FURNACE, null);
        map.put(CARTOGRAPHY_TABLE, null);
        map.put(FLETCHING_TABLE, null);
        map.put(GRINDSTONE, null);
        map.put(SMITHING_TABLE, null);
        map.put(STONECUTTER, null);
        map.put(BELL, null);
        map.put(LANTERN, null);
        map.put(SOUL_LANTERN, null);
        map.put(CAMPFIRE, null);
        map.put(SOUL_CAMPFIRE, null);
        map.put(BEE_NEST, null);
        map.put(BEEHIVE, null);
        map.put(HONEYCOMB_BLOCK, null);
        map.put(POLISHED_BLACKSTONE_BRICKS, null);
        map.put(CRACKED_POLISHED_BLACKSTONE_BRICKS, null);
        map.put(RESPAWN_ANCHOR, null);
        map.put(AMETHYST_CLUSTER, null);
        map.put(POINTED_DRIPSTONE, null);
        map.put(TALL_SEAGRASS, null);
        map.put(PISTON_HEAD, null);
        map.put(MOVING_PISTON, null);
        map.put(WALL_TORCH, null);
        map.put(FIRE, null);
        map.put(SOUL_FIRE, null);
        map.put(REDSTONE_WIRE, null);
        map.put(ATTACHED_PUMPKIN_STEM, null);
        map.put(ATTACHED_MELON_STEM, null);
        map.put(PUMPKIN_STEM, null);
        map.put(MELON_STEM, null);
        map.put(WATER_CAULDRON, null);
        map.put(LAVA_CAULDRON, null);
        map.put(POWDER_SNOW_CAULDRON, null);
        map.put(END_PORTAL, null);
        map.put(COCOA, null);
        map.put(TRIPWIRE, null);
        map.put(CARROTS, null);
        map.put(POTATOES, null);
        map.put(BEETROOTS, null);
        map.put(FROSTED_ICE, null);
        map.put(KELP_PLANT, null);
        map.put(POTTED_BAMBOO, null);
        map.put(BUBBLE_COLUMN, null);
        map.put(SWEET_BERRY_BUSH, null);
        map.put(WEEPING_VINES_PLANT, null);
        map.put(TWISTING_VINES_PLANT, null);
        map.put(CANDLE_CAKE, null);
        map.put(CAVE_VINES, null);
        map.put(CAVE_VINES_PLANT, null);
        map.put(BIG_DRIPLEAF_STEM, null);

        outer:
        for (Material value : Material.values()) {
            if (!value.isBlock()) {
                continue;
            }

            if (value.name().toLowerCase(Locale.ROOT).contains("candle_cake")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("pressure_plate")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("air")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("amethyst_bud")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("sign")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("potted")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("bed")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("head")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("banner")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("candle")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("fence_gate")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("rail")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("door")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("sapling")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("button")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("shulker_box")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("glass_pane")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("carpet")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("anvil")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("wall")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("fence")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("leaves")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("waxed")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("slab")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).endsWith("fan")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("stairs")) {
                continue;
            }
            if (value.name().toLowerCase(Locale.ROOT).contains("coral")) {
                continue;
            }

            if (value.name().toLowerCase(Locale.ROOT).contains("legacy")) {
                continue;
            }

            if (map.containsKey(value)) {
                if (map.get(value) == null) {
                    continue;
                } else {
                    for (HeadBlock headBlock : headBlocks) {
                        if (headBlock.getName().equalsIgnoreCase(map.get(value))) {
                            blocks.computeIfAbsent(value, k -> new HashSet<>());
                            blocks.get(value).add(headBlock.getValue());
                        }
                    }
                }

            }
            for (HeadBlock headBlock : headBlocks) {
                if (headBlock.getName().equalsIgnoreCase(value.name().replaceAll("_", " "))) {
                    blocks.computeIfAbsent(value, k -> new HashSet<>());
                    blocks.get(value).add(headBlock.getValue());

                }
                if (headBlock.getName().equalsIgnoreCase((value.name() + "_block").replaceAll("_", " "))) {
                    blocks.computeIfAbsent(value, k -> new HashSet<>());
                    blocks.get(value).add(headBlock.getValue());
                }
            }
        }
        return blocks;
    }

}
