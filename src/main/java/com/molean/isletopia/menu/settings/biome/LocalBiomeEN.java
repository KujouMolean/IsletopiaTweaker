package com.molean.isletopia.menu.settings.biome;

import org.bukkit.Material;

import java.util.List;

public enum LocalBiomeEN {
    SNOWY_TUNDRA("Snowy Tundra", Material.SNOW, List.of("Rabbit", "Polar bear"), List.of("Snow", "Snowfall", "Ice"), List.of()),
    ICE_SPIKES("Ice Spikes", Material.SNOW, List.of("Rabbit", "Polar bear"), List.of("Snow", "Snowfall", "Ice"), List.of()),
    SNOWY_TAIGA("Snowy Taiga", Material.SPRUCE_SAPLING, List.of("Wolf", "Rabbit"), List.of("Snow", "Snowfall", "Ice"), List.of()),
    SNOWY_TAIGA_HILLS("Snowy Taiga Hills", Material.SPRUCE_SAPLING, List.of("Wolf", "Rabbit"), List.of("Snow", "Snowfall"), List.of()),
    FROZEN_RIVER("Frozen River", Material.ICE, List.of("Salmon", "Drown"), List.of("Ice"), List.of()),
    SNOWY_BEACH("Snowy Beach", Material.ICE, List.of(), List.of("Snow", "Snowfall", "Ice"), List.of()),
    MOUNTAINS("Mountains", Material.STONE, List.of("Lama"), List.of(), List.of()),
    GRAVELLY_MOUNTAINS("Gravelly Mountains", Material.STONE, List.of(), List.of(), List.of()),
    WOODED_MOUNTAINS("Wooded Mountains", Material.STONE, List.of(), List.of(), List.of()),
    MODIFIED_GRAVELLY_MOUNTAINS("Gravelly Mountains+", Material.STONE, List.of(), List.of(), List.of()),
    TAIGA("Taiga", Material.SPRUCE_SAPLING, List.of("Wolf", "Rabbit", "Fox"), List.of(), List.of()),
    TAIGA_MOUNTAINS("Taiga Mountains", Material.SPRUCE_SAPLING, List.of("Wolf", "Rabbit", "Fox"), List.of(), List.of()),
    GIANT_TREE_TAIGA("Giant Tree Taiga", Material.SPRUCE_SAPLING, List.of("Wolf", "Rabbit"), List.of(), List.of()),
    GIANT_SPRUCE_TAIGA("Giant Spruce Taiga", Material.SPRUCE_SAPLING, List.of("Wolf", "Rabbit"), List.of(), List.of()),
    STONE_SHORE("Stone Shore", Material.ANDESITE, List.of(), List.of(), List.of()),
    PLAINS("Plains", Material.GRASS_BLOCK, List.of("Horse", "Donkey"), List.of("Rubia", "Tulip", "Perianth", "Cornflower"), List.of()),
    SUNFLOWER_PLAINS("Sunflower Plains", Material.SUNFLOWER, List.of("Horse", "Donkey"), List.of("Rubia", "Tulip", "Perianth", "Cornflower"), List.of()),
    FOREST("Forest", Material.OAK_SAPLING, List.of("Wolf"), List.of(), List.of()),
    FLOWER_FOREST("Flower Forest", Material.POPPY, List.of("Rabbit", "Bee"), List.of("Welsh onion", "Rubia", "Tulip", "Chamomile", "Cornflower", "Lily"), List.of()),
    BIRCH_FOREST("Birch Forest", Material.BIRCH_SAPLING, List.of(), List.of(), List.of()),
    TALL_BIRCH_FOREST("Tall Birch Forest", Material.BIRCH_SAPLING, List.of(), List.of(), List.of()),
    DARK_FOREST("Dark Forest", Material.DARK_OAK_SAPLING, List.of(), List.of(), List.of()),
    DARK_FOREST_HILLS("Dark Forest Hills", Material.DARK_OAK_SAPLING, List.of(), List.of(), List.of()),
    SWAMP("Swamp", Material.VINE, List.of("Slime", "Mushroom cow", "Witch"), List.of("Orchid"), List.of()),
    SWAMP_HILLS("Swamp Hills", Material.VINE, List.of("Witch"), List.of(), List.of()),
    JUNGLE("Jungle", Material.JUNGLE_SAPLING, List.of("Ocelot", "Parrot"), List.of(), List.of()),
    MODIFIED_JUNGLE("Modified Jungle", Material.JUNGLE_SAPLING, List.of("Ocelot", "Parrot"), List.of(), List.of()),
    JUNGLE_EDGE("Jungle Edge", Material.JUNGLE_SAPLING, List.of("Ocelot", "Parrot"), List.of(), List.of()),
    MODIFIED_JUNGLE_EDGE("Modified Jungle Edge", Material.JUNGLE_SAPLING, List.of("Ocelot", "Parrot"), List.of(), List.of()),
    BAMBOO_JUNGLE("Bamboo Jungle", Material.BAMBOO, List.of("Ocelot", "Parrot", "Panda"), List.of(), List.of()),
    BAMBOO_JUNGLE_HILLS("Bamboo Jungle Hills", Material.BAMBOO, List.of("Ocelot", "Parrot", "Panda"), List.of(), List.of()),
    RIVER("River", Material.WATER_BUCKET, List.of("Salmon", "Drown"), List.of(), List.of()),
    BEACH("Beach", Material.SAND, List.of("Turtle"), List.of(), List.of()),
    MUSHROOM_FIELDS("Mushroom Fields", Material.BROWN_MUSHROOM, List.of("Mushroom cow"), List.of("Non aggressive creatures"), List.of()),
    MUSHROOM_FIELD_SHORE("Mushroom Field Shore", Material.RED_MUSHROOM, List.of("Mushroom cow"), List.of("Non aggressive creatures"), List.of()),
    THE_END("The End", Material.END_STONE, List.of("Enderman"), List.of(), List.of()),
    SMALL_END_ISLANDS("Small End Islands", Material.END_STONE_BRICK_WALL, List.of("Enderman"), List.of(), List.of()),
    END_MIDLANDS("End Midlands", Material.END_STONE_BRICK_SLAB, List.of("Enderman"), List.of(), List.of()),
    END_HIGHLANDS("End Highlands", Material.END_STONE_BRICK_STAIRS, List.of("Enderman"), List.of(), List.of()),
    END_BARRENS("End Barrens", Material.END_STONE_BRICKS, List.of("Enderman"), List.of(), List.of()),
    DESERT("Desert", Material.SAND, List.of("Rabbit", "Husk"), List.of(), List.of()),
    DESERT_LAKES("Desert Lakes", Material.WATER_BUCKET, List.of("Husk", "Rabbit"), List.of(), List.of()),
    SAVANNA("Savanna", Material.GRASS_BLOCK, List.of("Horse", "Sheep", "Cow", "Lama"), List.of(), List.of()),
    SHATTERED_SAVANNA("Shattered Savanna", Material.GRASS_BLOCK, List.of("Lama"), List.of(), List.of()),
    BADLANDS("Badlands", Material.DEAD_BUSH, List.of(), List.of(), List.of()),
    ERODED_BADLANDS("Eroded Badlands", Material.DEAD_BUSH, List.of(), List.of(), List.of()),
    WOODED_BADLANDS_PLATEAU("Wooded Badlands Plateau", Material.DEAD_BUSH, List.of(), List.of(), List.of()),
    SAVANNA_PLATEAU("Savanna Plateau", Material.STONE, List.of(), List.of(), List.of()),
    BADLANDS_PLATEAU("Badlands Plateau", Material.STONE, List.of(), List.of(), List.of()),
    SHATTERED_SAVANNA_PLATEAU("Shattered Savanna Plateau", Material.STONE, List.of(), List.of(), List.of()),
    MODIFIED_BADLANDS_PLATEAU("Modified Badlands Plateau", Material.STONE, List.of(), List.of(), List.of()),
    MODIFIED_WOODED_BADLANDS_PLATEAU("Modified Wooded Badlands Plateau", Material.STONE, List.of(), List.of(), List.of()),
    NETHER_WASTES("Nether Wastes", Material.NETHERRACK, List.of("Ghast", "Zombie piglin", "Magma Cube", "Enderman", "Strider", "Piglin"), List.of(), List.of()),
    WARM_OCEAN("Warm Ocean", Material.WATER_BUCKET, List.of("Dolphin", "Puffer fish", "Tropical fish"), List.of(), List.of()),
    LUKEWARM_OCEAN("Lukewarm Ocean", Material.WATER_BUCKET, List.of("Dolphin", "Puffer fish", "Tropical fish", "Cod"), List.of(), List.of()),
    DEEP_LUKEWARM_OCEAN("Deep Lukewarm Ocean", Material.WATER_BUCKET, List.of("Dolphin", "Puffer fish", "Tropical fish", "Guardian", "Elder guardian"), List.of(), List.of()),
    OCEAN("Ocean", Material.WATER_BUCKET, List.of("Squid", "Cod", "Drown"), List.of(), List.of()),
    DEEP_OCEAN("Deep Ocean", Material.WATER_BUCKET, List.of("Guardian", "Elder guardian", "Drown", "Cod"), List.of(), List.of()),
    COLD_OCEAN("Cold Ocean", Material.WATER_BUCKET, List.of("Dolphin", "Cod", "Salmon"), List.of(), List.of()),
    DEEP_COLD_OCEAN(" Deep Cold Ocean", Material.WATER_BUCKET, List.of("Dolphin", "Cod", "Salmon", "Guardian", "Elder guardian"), List.of(), List.of()),
    FROZEN_OCEAN("Frozen Ocean", Material.BLUE_ICE, List.of("Squid", "Salmon",  "Polar bear"), List.of(), List.of()),
    DEEP_FROZEN_OCEAN("Deep Frozen Ocean", Material.BLUE_ICE, List.of("Salmon", "Guardian", "Elder guardian", "Polar bear"), List.of(), List.of()),
    THE_VOID("The Void", Material.BARRIER, List.of(), List.of(), List.of()),
    SNOWY_MOUNTAINS("Snowy Mountains", Material.SNOW_BLOCK, List.of(), List.of(), List.of()),
    SNOWY_TAIGA_MOUNTAINS("Snowy Taiga Mountains", Material.SPRUCE_SAPLING, List.of(), List.of(), List.of()),
    TALL_BIRCH_HILLS("Tall Birch Hills", Material.BIRCH_SAPLING, List.of(), List.of(), List.of()),
    GIANT_SPRUCE_TAIGA_HILLS("Giant Spruce Taiga Hills", Material.SPRUCE_SAPLING, List.of("Rabbit", "Wolf"), List.of(), List.of()),
    DESERT_HILLS("Desert Hills", Material.SANDSTONE, List.of("Husk"), List.of(), List.of()),
    WOODED_HILLS("Wooded Hills", Material.OAK_SAPLING, List.of("Wolf"), List.of(), List.of()),
    TAIGA_HILLS("Taiga Hills", Material.SPRUCE_SAPLING, List.of("Rabbit", "Fox", "Wolf"), List.of(), List.of()),
    JUNGLE_HILLS("Jungle Hills", Material.JUNGLE_SAPLING, List.of("Ocelot", "Parrot"), List.of(), List.of()),
    BIRCH_FOREST_HILLS("Birch Forest Hills", Material.BIRCH_SAPLING, List.of(), List.of(), List.of()),
    GIANT_TREE_TAIGA_HILLS("Giant Tree Taiga Hills", Material.SPRUCE_SAPLING, List.of("Wolf", "Rabbit"), List.of(), List.of()),
    MOUNTAIN_EDGE("Mountain Edge", Material.STONE, List.of("Pig", "Sheep", "Cow", "Wolf"), List.of(), List.of()),
    DEEP_WARM_OCEAN("Deep Warm Ocean", Material.WATER_BUCKET, List.of("Dolphin", "Puffer fish", "Tropical fish", "Guardian", "Elder guardian"), List.of(), List.of()),
    SOUL_SAND_VALLEY("Soul Sand Valley", Material.SOUL_SAND, List.of("Skeleton", "Ghast(More)", "Enderman", "Strider"), List.of(), List.of()),
    CRIMSON_FOREST("Crimson Forest", Material.CRIMSON_ROOTS, List.of("Piglin", "Zombie piglin", "Hoglin", "Strider"), List.of(), List.of()),
    WARPED_FOREST("Warped Forest", Material.WARPED_ROOTS, List.of("Enderman", "Strider"), List.of(), List.of()),
    BASALT_DELTAS("Basalt Deltas", Material.BASALT, List.of("Ghast", "Magma Cube", "Strider"), List.of(), List.of());


    private String name;
    private Material icon;
    private List<String> creatures;
    private List<String> environment;
    private List<String> extraInfo;

    LocalBiomeEN(String name, Material icon, List<String> creatures, List<String> environment, List<String> extraInfo) {
        this.name = name;
        this.icon = icon;
        this.creatures = creatures;
        this.environment = environment;
        this.extraInfo = extraInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public List<String> getCreatures() {
        return creatures;
    }

    public void setCreatures(List<String> creatures) {
        this.creatures = creatures;
    }

    public List<String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(List<String> environment) {
        this.environment = environment;
    }

    public List<String> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(List<String> extraInfo) {
        this.extraInfo = extraInfo;
    }
}
