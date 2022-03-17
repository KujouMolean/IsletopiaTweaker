package com.molean.isletopia.menu.settings.biome;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import java.util.List;

public enum LocalBiome {
    OCEAN(Biome.OCEAN,Material.WATER_BUCKET),
    PLAINS(Biome.PLAINS,Material.GRASS_BLOCK),
    DESERT(Biome.DESERT,Material.SAND),
    WINDSWEPT_HILLS(Biome.WINDSWEPT_HILLS,Material.STONE),
    FOREST(Biome.FOREST,Material.OAK_SAPLING),
    TAIGA(Biome.TAIGA,Material.SPRUCE_SAPLING),
    SWAMP(Biome.SWAMP,Material.VINE),
    RIVER(Biome.RIVER,Material.WATER_BUCKET),
    NETHER_WASTES(Biome.NETHER_WASTES,Material.NETHERRACK),
    THE_END(Biome.THE_END,Material.END_STONE),
    FROZEN_OCEAN(Biome.FROZEN_OCEAN,Material.BLUE_ICE),
    FROZEN_RIVER(Biome.FROZEN_RIVER,Material.ICE),
    SNOWY_PLAINS(Biome.SNOWY_PLAINS,Material.SNOW),
    MUSHROOM_FIELDS(Biome.MUSHROOM_FIELDS,Material.BROWN_MUSHROOM),
    BEACH(Biome.BEACH,Material.SAND),
    JUNGLE(Biome.JUNGLE,Material.JUNGLE_SAPLING),
    SPARSE_JUNGLE(Biome.SPARSE_JUNGLE,Material.JUNGLE_SAPLING),
    DEEP_OCEAN(Biome.DEEP_OCEAN,Material.WATER_BUCKET),
    STONY_SHORE(Biome.STONY_SHORE,Material.ANDESITE),
    SNOWY_BEACH(Biome.SNOWY_BEACH,Material.ICE),
    BIRCH_FOREST(Biome.BIRCH_FOREST,Material.BIRCH_SAPLING),
    DARK_FOREST(Biome.DARK_FOREST,Material.DARK_OAK_SAPLING),
    SNOWY_TAIGA(Biome.SNOWY_TAIGA,Material.SPRUCE_SAPLING),
    OLD_GROWTH_PINE_TAIGA(Biome.OLD_GROWTH_PINE_TAIGA,Material.SPRUCE_SAPLING),
    WINDSWEPT_FOREST(Biome.WINDSWEPT_FOREST,Material.STONE),
    SAVANNA(Biome.SAVANNA,Material.GRASS_BLOCK),
    SAVANNA_PLATEAU(Biome.SAVANNA_PLATEAU,Material.STONE),
    BADLANDS(Biome.BADLANDS,Material.DEAD_BUSH),
    WOODED_BADLANDS(Biome.WOODED_BADLANDS,Material.DEAD_BUSH),
    SMALL_END_ISLANDS(Biome.SMALL_END_ISLANDS,Material.END_STONE_BRICK_WALL),
    END_MIDLANDS(Biome.END_MIDLANDS,Material.END_STONE_BRICK_SLAB),
    END_HIGHLANDS(Biome.END_HIGHLANDS,Material.END_STONE_BRICK_STAIRS),
    END_BARRENS(Biome.END_BARRENS,Material.END_STONE_BRICKS),
    WARM_OCEAN(Biome.WARM_OCEAN,Material.WATER_BUCKET),
    LUKEWARM_OCEAN(Biome.LUKEWARM_OCEAN,Material.WATER_BUCKET),
    COLD_OCEAN(Biome.COLD_OCEAN,Material.WATER_BUCKET),
    DEEP_LUKEWARM_OCEAN(Biome.DEEP_LUKEWARM_OCEAN,Material.WATER_BUCKET),
    DEEP_COLD_OCEAN(Biome.DEEP_COLD_OCEAN,Material.WATER_BUCKET),
    DEEP_FROZEN_OCEAN(Biome.DEEP_FROZEN_OCEAN,Material.BLUE_ICE),
    THE_VOID(Biome.THE_VOID,Material.BARRIER),
    SUNFLOWER_PLAINS(Biome.SUNFLOWER_PLAINS,Material.SUNFLOWER),
    WINDSWEPT_GRAVELLY_HILLS(Biome.WINDSWEPT_GRAVELLY_HILLS,Material.STONE),
    FLOWER_FOREST(Biome.FLOWER_FOREST,Material.POPPY),
    ICE_SPIKES(Biome.ICE_SPIKES,Material.SNOW),
    OLD_GROWTH_BIRCH_FOREST(Biome.OLD_GROWTH_BIRCH_FOREST,Material.BIRCH_SAPLING),
    OLD_GROWTH_SPRUCE_TAIGA(Biome.OLD_GROWTH_SPRUCE_TAIGA,Material.SPRUCE_SAPLING),
    WINDSWEPT_SAVANNA(Biome.WINDSWEPT_SAVANNA,Material.GRASS_BLOCK),
    ERODED_BADLANDS(Biome.ERODED_BADLANDS,Material.DEAD_BUSH),
    BAMBOO_JUNGLE(Biome.BAMBOO_JUNGLE,Material.BAMBOO),
    SOUL_SAND_VALLEY(Biome.SOUL_SAND_VALLEY,Material.SOUL_SAND),
    CRIMSON_FOREST(Biome.CRIMSON_FOREST,Material.CRIMSON_ROOTS),
    WARPED_FOREST(Biome.WARPED_FOREST,Material.WARPED_ROOTS),
    BASALT_DELTAS(Biome.BASALT_DELTAS,Material.BASALT),
    DRIPSTONE_CAVES(Biome.DRIPSTONE_CAVES,Material.POINTED_DRIPSTONE),
    LUSH_CAVES(Biome.LUSH_CAVES,Material.GLOW_BERRIES),
    FROZEN_PEAKS(Biome.FROZEN_PEAKS,Material.SNOWBALL),
    GROVE(Biome.GROVE,Material.SPRUCE_SAPLING),
    JAGGED_PEAKS(Biome.JAGGED_PEAKS,Material.STONE),
    MEADOW(Biome.MEADOW,Material.GRASS_BLOCK),
    SNOWY_SLOPES(Biome.SNOWY_SLOPES,Material.SNOW_BLOCK),
    STONY_PEAKS(Biome.STONY_PEAKS,Material.STONE),
    ;
    private Biome biome;
    private Material icon;


    LocalBiome(Biome biome, Material icon) {
        this.biome = biome;
        this.icon = icon;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }
}
