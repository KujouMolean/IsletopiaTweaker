package com.molean.isletopia.menu.settings.biome;

import org.bukkit.Material;
import java.util.List;

public enum LocalBiome {
    OCEAN("海洋", Material.WATER_BUCKET, List.of("鱿鱼", "鳕鱼", "溺尸"), List.of("")),
    PLAINS("平原", Material.GRASS_BLOCK, List.of("马", "驴"), List.of("茜草花", "郁金香", "滨菊", "矢车菊")),
    DESERT("沙漠", Material.SAND, List.of("兔子", "尸壳"), List.of("")),
    MOUNTAINS("山地", Material.STONE, List.of("羊驼"), List.of("")),
    FOREST("森林", Material.OAK_SAPLING, List.of("狼"), List.of("")),
    TAIGA("针叶林", Material.SPRUCE_SAPLING, List.of("狼", "兔子", "狐狸"), List.of("")),
    SWAMP("沼泽", Material.VINE, List.of("史莱姆", "蘑菇牛"), List.of("兰花")),
    RIVER("河流", Material.WATER_BUCKET, List.of("鲑鱼", "溺尸"), List.of("")),
    NETHER_WASTES("下届", Material.NETHERRACK, List.of("恶魂", "僵尸猪灵", "岩浆怪", "末影人", "炽足兽", "猪灵"), List.of("")),
    THE_END("末地之路", Material.END_STONE, List.of("末影人"), List.of("")),
    FROZEN_OCEAN("冻洋", Material.BLUE_ICE, List.of("鱿鱼", "鲑鱼", "北极熊"), List.of("")),
    FROZEN_RIVER("冻河", Material.ICE, List.of("鲑鱼", "溺尸"), List.of("冰")),
    SNOWY_TUNDRA("积雪的冻原", Material.SNOW, List.of("兔子", "北极熊"), List.of("雪", "降雪", "冰")),
    SNOWY_MOUNTAINS("雪山", Material.SNOW_BLOCK, List.of(""), List.of("")),
    MUSHROOM_FIELDS("蘑菇岛", Material.BROWN_MUSHROOM, List.of("蘑菇牛"), List.of("无攻击型生物")),
    MUSHROOM_FIELD_SHORE("蘑菇岛海滩", Material.RED_MUSHROOM, List.of("蘑菇牛"), List.of("无攻击型生物")),
    BEACH("沙滩", Material.SAND, List.of("海龟"), List.of("")),
    DESERT_HILLS("沙漠丘陵", Material.SANDSTONE, List.of("尸壳"), List.of("")),
    WOODED_HILLS("繁茂的丘陵", Material.OAK_SAPLING, List.of("狼"), List.of("")),
    TAIGA_HILLS("针叶林丘陵", Material.SPRUCE_SAPLING, List.of("兔子", "狐狸", "狼"), List.of("")),
    MOUNTAIN_EDGE("山地边缘", Material.STONE, List.of("猪", "羊", "牛", "狼"), List.of("")),
    JUNGLE("丛林", Material.JUNGLE_SAPLING, List.of("豹猫", "鹦鹉"), List.of("")),
    JUNGLE_HILLS("丛林丘陵", Material.JUNGLE_SAPLING, List.of("豹猫", "鹦鹉"), List.of("")),
    JUNGLE_EDGE("丛林边缘", Material.JUNGLE_SAPLING, List.of("豹猫", "鹦鹉"), List.of("")),
    DEEP_OCEAN("深海", Material.WATER_BUCKET, List.of("守卫者", "远古守卫者", "溺尸", "鳕鱼"), List.of("")),
    STONE_SHORE("石岸", Material.ANDESITE, List.of(""), List.of("")),
    SNOWY_BEACH("积雪的沙滩", Material.ICE, List.of(""), List.of("雪", "降雪", "冰")),
    BIRCH_FOREST("桦木森林", Material.BIRCH_SAPLING, List.of(""), List.of("")),
    BIRCH_FOREST_HILLS("桦木森林丘陵", Material.BIRCH_SAPLING, List.of(""), List.of("")),
    DARK_FOREST("黑森林", Material.DARK_OAK_SAPLING, List.of(""), List.of("")),
    SNOWY_TAIGA("积雪的针叶林", Material.SPRUCE_SAPLING, List.of("狼", "兔子"), List.of("雪", "降雪", "冰")),
    SNOWY_TAIGA_HILLS("积雪的针叶林山地", Material.SPRUCE_SAPLING, List.of("狼", "兔子"), List.of("雪", "降雪")),
    GIANT_TREE_TAIGA("巨型针叶林", Material.SPRUCE_SAPLING, List.of("狼", "兔子"), List.of("")),
    GIANT_TREE_TAIGA_HILLS("巨型针叶林丘陵", Material.SPRUCE_SAPLING, List.of("狼", "兔子"), List.of("")),
    WOODED_MOUNTAINS("繁茂的山地", Material.STONE, List.of(""), List.of("")),
    SAVANNA("热带草原", Material.GRASS_BLOCK, List.of("马", "羊", "牛", "羊驼"), List.of("")),
    SAVANNA_PLATEAU("热带高原", Material.STONE, List.of(""), List.of("")),
    BADLANDS("恶地", Material.DEAD_BUSH, List.of(""), List.of("")),
    WOODED_BADLANDS_PLATEAU("繁茂的恶地高原", Material.DEAD_BUSH, List.of(""), List.of("")),
    BADLANDS_PLATEAU("恶地高原", Material.STONE, List.of(""), List.of("")),
    SMALL_END_ISLANDS("末地小型岛屿", Material.END_STONE_BRICK_WALL, List.of("末影人"), List.of("")),
    END_MIDLANDS("末地中型岛屿", Material.END_STONE_BRICK_SLAB, List.of("末影人"), List.of("")),
    END_HIGHLANDS("末地高岛", Material.END_STONE_BRICK_STAIRS, List.of("末影人"), List.of("")),
    END_BARRENS("末地荒岛", Material.END_STONE_BRICKS, List.of("末影人"), List.of("")),
    WARM_OCEAN("暖水海洋", Material.WATER_BUCKET, List.of("海豚", "河豚", "热带鱼"), List.of("")),
    LUKEWARM_OCEAN("温水海洋", Material.WATER_BUCKET, List.of("海豚", "河豚", "热带鱼", "鳕鱼"), List.of("")),
    COLD_OCEAN("冷水海洋", Material.WATER_BUCKET, List.of("海豚", "鳕鱼", "鲑鱼"), List.of("")),
    DEEP_WARM_OCEAN("暖水深海", Material.WATER_BUCKET, List.of("海豚", "河豚", "热带鱼", "守卫者", "远古守卫者"), List.of("")),
    DEEP_LUKEWARM_OCEAN("温水深海", Material.WATER_BUCKET, List.of("海豚", "河豚", "热带鱼", "守卫者", "远古守卫者"), List.of("")),
    DEEP_COLD_OCEAN("冷水深海", Material.WATER_BUCKET, List.of("海豚", "鳕鱼", "鲑鱼", "守卫者", "远古守卫者"), List.of("")),
    DEEP_FROZEN_OCEAN("封冻深海", Material.BLUE_ICE, List.of("鲑鱼", "守卫者", "远古守卫者", "北极熊"), List.of("")),
    THE_VOID("虚空", Material.BARRIER, List.of(""), List.of("")),
    SUNFLOWER_PLAINS("向日葵平原", Material.SUNFLOWER, List.of("马", "驴"), List.of("茜草花", "郁金香", "滨菊", "矢车菊")),
    DESERT_LAKES("沙漠湖泊", Material.WATER_BUCKET, List.of("尸壳", "兔子"), List.of("")),
    GRAVELLY_MOUNTAINS("沙砾山地", Material.STONE, List.of(""), List.of("")),
    FLOWER_FOREST("繁花森林", Material.POPPY, List.of("兔子", "蜜蜂"), List.of("绒球葱", "茜草花", "郁金香", "滨菊", "矢车菊", "铃兰")),
    TAIGA_MOUNTAINS("针叶林山地", Material.SPRUCE_SAPLING, List.of("狼", "兔子", "狐狸"), List.of("")),
    SWAMP_HILLS("沼泽山丘", Material.VINE, List.of("女巫"), List.of("")),
    ICE_SPIKES("冰刺平原", Material.SNOW, List.of("兔子", "北极熊"), List.of("雪", "降雪", "冰")),
    MODIFIED_JUNGLE("丛林变种", Material.JUNGLE_SAPLING, List.of("豹猫", "鹦鹉"), List.of("")),
    MODIFIED_JUNGLE_EDGE("丛林边缘变种", Material.JUNGLE_SAPLING, List.of("豹猫", "鹦鹉"), List.of("")),
    TALL_BIRCH_FOREST("高大桦木森林", Material.BIRCH_SAPLING, List.of(""), List.of("")),
    TALL_BIRCH_HILLS("高大桦木丘陵", Material.BIRCH_SAPLING, List.of(""), List.of("")),
    DARK_FOREST_HILLS("黑森林丘陵", Material.DARK_OAK_SAPLING, List.of(""), List.of("")),
    SNOWY_TAIGA_MOUNTAINS("积雪的针叶林山地", Material.SPRUCE_SAPLING, List.of(""), List.of("")),
    GIANT_SPRUCE_TAIGA("巨型云杉针叶林", Material.SPRUCE_SAPLING, List.of("狼", "兔子"), List.of("")),
    GIANT_SPRUCE_TAIGA_HILLS("巨型云杉针叶林丘陵", Material.SPRUCE_SAPLING, List.of("兔子", "狼"), List.of("")),
    MODIFIED_GRAVELLY_MOUNTAINS("变种沙砾山地", Material.STONE, List.of(""), List.of("")),
    SHATTERED_SAVANNA("破碎的热带草原", Material.GRASS_BLOCK, List.of("羊驼"), List.of("")),
    SHATTERED_SAVANNA_PLATEAU("破碎的热带高原", Material.STONE, List.of(""), List.of("")),
    ERODED_BADLANDS("被风蚀的恶地", Material.DEAD_BUSH, List.of(""), List.of("")),
    MODIFIED_WOODED_BADLANDS_PLATEAU("繁茂的恶地高原变种", Material.STONE, List.of(""), List.of("")),
    MODIFIED_BADLANDS_PLATEAU("恶地高原变种", Material.STONE, List.of(""), List.of("")),
    BAMBOO_JUNGLE("竹林", Material.BAMBOO, List.of("豹猫", "鹦鹉", "熊猫"), List.of("")),
    BAMBOO_JUNGLE_HILLS("竹林丘陵", Material.BAMBOO, List.of("豹猫", "鹦鹉", "熊猫"), List.of("")),
    SOUL_SAND_VALLEY("灵魂沙峡谷", Material.SOUL_SAND, List.of("骷髅", "恶魂(多)", "末影人", "炽足兽"), List.of("")),
    CRIMSON_FOREST("绯红森林", Material.CRIMSON_ROOTS, List.of("猪灵", "僵尸猪灵", "疣猪兽", "炽足兽"), List.of("")),
    WARPED_FOREST("诡异森林", Material.WARPED_ROOTS, List.of("末影人", "炽足兽"), List.of("")),
    BASALT_DELTAS("玄武岩三角洲", Material.BASALT, List.of("恶魂", "岩浆怪", "炽足兽"), List.of("")),
    DRIPSTONE_CAVES("溶洞", Material.PLAYER_HEAD, List.of("?"), List.of("?")),
    LUSH_CAVES("繁茂洞穴", Material.PLAYER_HEAD, List.of("?"), List.of("?")),
    CUSTOM("未知", Material.PLAYER_HEAD, List.of("?"), List.of("?"));


    private String name;
    private Material icon;
    private List<String> creatures;
    private List<String> environment;


    LocalBiome(String name, Material icon, List<String> creatures, List<String> environment) {
        this.name = name;
        this.icon = icon;
        this.creatures = creatures;
        this.environment = environment;
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
}
