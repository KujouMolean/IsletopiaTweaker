package com.molean.isletopia.menu.settings.biome;

import org.bukkit.Material;

import java.util.List;

public enum LocalBiome {
    OCEAN("海洋", Material.WATER_BUCKET, List.of("鱿鱼", "鳕鱼", "溺尸","普通动物","敌对生物"), List.of("")),
    PLAINS("平原", Material.GRASS_BLOCK, List.of("马", "驴","普通动物","敌对生物"), List.of("茜草花", "郁金香", "滨菊", "矢车菊")),
    DESERT("沙漠", Material.SAND, List.of("兔子", "尸壳","敌对生物"), List.of("无普通动物")),
    WINDSWEPT_HILLS("山地", Material.STONE, List.of("羊驼","普通动物","敌对生物"), List.of("")),
    FOREST("森林", Material.OAK_SAPLING, List.of("狼","普通动物","敌对生物"), List.of("")),
    TAIGA("针叶林", Material.SPRUCE_SAPLING, List.of("狼", "兔子", "狐狸","普通动物","敌对生物"), List.of("")),
    SWAMP("沼泽", Material.VINE, List.of("史莱姆", "蘑菇牛","普通动物","敌对生物"), List.of("兰花")),
    RIVER("河流", Material.WATER_BUCKET, List.of("鲑鱼", "溺尸","敌对生物"), List.of("无普通动物")),
    NETHER_WASTES("下界荒地", Material.NETHERRACK, List.of("恶魂", "僵尸猪灵", "岩浆怪", "末影人", "炽足兽", "猪灵"), List.of("")),
    THE_END("末地之路", Material.END_STONE, List.of("末影人"), List.of("")),
    FROZEN_OCEAN("冻洋", Material.BLUE_ICE, List.of("鱿鱼", "鲑鱼", "北极熊","兔子","流浪者","普通动物","敌对生物"), List.of("")),
    FROZEN_RIVER("冻河", Material.ICE, List.of("鲑鱼", "溺尸","兔子","北极熊","流浪者","敌对生物"), List.of("冰","无普通动物")),
    SNOWY_PLAINS("积雪的平原", Material.SNOW, List.of("兔子", "北极熊","流浪者","普通动物","敌对生物"), List.of("雪", "降雪", "冰")),
    MUSHROOM_FIELDS("蘑菇岛", Material.BROWN_MUSHROOM, List.of("蘑菇牛"), List.of("无敌对生物","无普通动物")),
    BEACH("沙滩", Material.SAND, List.of("海龟","敌对生物"), List.of("无普通动物")),
    JUNGLE("丛林", Material.JUNGLE_SAPLING, List.of("豹猫", "鹦鹉","普通动物","敌对生物"), List.of("")),
    SPARSE_JUNGLE("丛林边缘", Material.JUNGLE_SAPLING, List.of("豹猫", "鹦鹉","普通动物","敌对生物"), List.of("")),
    DEEP_OCEAN("深海", Material.WATER_BUCKET, List.of("守卫者", "远古守卫者", "溺尸", "鳕鱼","普通动物","敌对生物"), List.of("")),
    STONY_SHORE("石岸", Material.ANDESITE, List.of("普通动物","敌对生物"), List.of("")),
    SNOWY_BEACH("积雪的沙滩", Material.ICE, List.of("普通动物","敌对生物"), List.of("雪", "降雪", "冰","兔子")),
    BIRCH_FOREST("桦木森林", Material.BIRCH_SAPLING, List.of("普通动物","敌对生物"), List.of("")),
    DARK_FOREST("黑森林", Material.DARK_OAK_SAPLING, List.of("普通动物","敌对生物"), List.of("")),
    SNOWY_TAIGA("积雪的针叶林", Material.SPRUCE_SAPLING, List.of("狼", "兔子","狐狸","普通动物","敌对生物"), List.of("雪", "降雪", "冰")),
    OLD_GROWTH_PINE_TAIGA("原始松木针叶林", Material.SPRUCE_SAPLING, List.of("狼", "兔子","普通动物","敌对生物"), List.of("")),
    WINDSWEPT_FOREST("繁茂的山地", Material.STONE, List.of("普通动物","敌对生物"), List.of("")),
    SAVANNA("热带草原", Material.GRASS_BLOCK, List.of("马", "羊驼","普通动物","敌对生物"), List.of("")),
    SAVANNA_PLATEAU("热带高原", Material.STONE, List.of("普通动物","敌对生物"), List.of("")),
    BADLANDS("恶地", Material.DEAD_BUSH, List.of("敌对生物"), List.of("无普通动物")),
    WOODED_BADLANDS("繁茂的恶地高原", Material.DEAD_BUSH, List.of("敌对生物"), List.of("无普通动物")),
    SMALL_END_ISLANDS("末地小型岛屿", Material.END_STONE_BRICK_WALL, List.of("末影人"), List.of("")),
    END_MIDLANDS("末地中型岛屿", Material.END_STONE_BRICK_SLAB, List.of("末影人"), List.of("")),
    END_HIGHLANDS("末地高岛", Material.END_STONE_BRICK_STAIRS, List.of("末影人"), List.of("")),
    END_BARRENS("末地荒岛", Material.END_STONE_BRICKS, List.of("末影人"), List.of("")),
    WARM_OCEAN("暖水海洋", Material.WATER_BUCKET, List.of("海豚", "河豚", "热带鱼","溺尸","普通动物","敌对生物"), List.of("")),
    LUKEWARM_OCEAN("温水海洋", Material.WATER_BUCKET, List.of("海豚", "河豚", "热带鱼", "鳕鱼","溺尸","普通动物","敌对生物"), List.of("")),
    COLD_OCEAN("冷水海洋", Material.WATER_BUCKET, List.of("海豚", "鳕鱼", "鲑鱼","溺尸","普通动物","敌对生物"), List.of("")),
    DEEP_LUKEWARM_OCEAN("温水深海", Material.WATER_BUCKET, List.of("海豚", "河豚", "热带鱼", "守卫者","普通动物","敌对生物"), List.of("")),
    DEEP_COLD_OCEAN("冷水深海", Material.WATER_BUCKET, List.of("海豚", "鳕鱼", "鲑鱼", "守卫者","普通动物","敌对生物"), List.of("")),
    DEEP_FROZEN_OCEAN("封冻深海", Material.BLUE_ICE, List.of("兔子","鲑鱼", "守卫者","北极熊","流浪者","普通动物","敌对生物"), List.of("")),
    THE_VOID("虚空", Material.BARRIER, List.of("?"), List.of("?")),
    SUNFLOWER_PLAINS("向日葵平原", Material.SUNFLOWER, List.of("马", "驴","普通动物","敌对生物"), List.of("茜草花", "郁金香", "滨菊", "矢车菊")),
    WINDSWEPT_GRAVELLY_HILLS("沙砾山地", Material.STONE, List.of("羊驼","普通动物","敌对生物"), List.of("")),
    FLOWER_FOREST("繁花森林", Material.POPPY, List.of("兔子", "蜜蜂","普通动物","敌对生物"), List.of("绒球葱", "茜草花", "郁金香", "滨菊", "矢车菊", "铃兰")),
    ICE_SPIKES("冰刺平原", Material.SNOW, List.of("兔子","北极熊","流浪者"), List.of("雪", "降雪", "冰","无普通动物")),
    OLD_GROWTH_BIRCH_FOREST("原始桦木森林", Material.BIRCH_SAPLING, List.of("普通动物","敌对生物"), List.of("")),
    OLD_GROWTH_SPRUCE_TAIGA("原始松木针叶林", Material.SPRUCE_SAPLING, List.of("狼", "兔子","狐狸","普通动物","敌对生物"), List.of("")),
    WINDSWEPT_SAVANNA("风袭丘陵", Material.GRASS_BLOCK, List.of("羊驼","普通动物","敌对生物"), List.of("")),
    ERODED_BADLANDS("被风蚀的恶地", Material.DEAD_BUSH, List.of(""), List.of("无普通动物")),
    BAMBOO_JUNGLE("竹林", Material.BAMBOO, List.of("豹猫", "鹦鹉", "熊猫","普通动物","敌对生物"), List.of("")),
    SOUL_SAND_VALLEY("灵魂沙峡谷", Material.SOUL_SAND, List.of("骷髅", "恶魂(多)", "末影人", "炽足兽"), List.of("")),
    CRIMSON_FOREST("绯红森林", Material.CRIMSON_ROOTS, List.of("猪灵", "僵尸猪灵", "疣猪兽", "炽足兽"), List.of("")),
    WARPED_FOREST("诡异森林", Material.WARPED_ROOTS, List.of("末影人", "炽足兽"), List.of("")),
    BASALT_DELTAS("玄武岩三角洲", Material.BASALT, List.of("恶魂", "岩浆怪", "炽足兽"), List.of("")),
    DRIPSTONE_CAVES("溶洞", Material.POINTED_DRIPSTONE, List.of("溺尸","普通动物","敌对生物"), List.of("")),
    LUSH_CAVES("繁茂洞穴", Material.GLOW_BERRIES, List.of("美西螈","热带鱼","普通动物","敌对生物"), List.of("")),
    FROZEN_PEAKS("冰封山峰", Material.SNOWBALL, List.of("兔子","山羊","普通动物","敌对生物"), List.of("")),
    GROVE("雪林", Material.SPRUCE_SAPLING, List.of("兔子","狼","狐狸","普通动物","敌对生物"), List.of("")),
    JAGGED_PEAKS("尖峭山峰", Material.STONE, List.of("山羊","兔子","普通动物","敌对生物"), List.of("")),
    MEADOW("草甸", Material.GRASS_BLOCK, List.of("蜂巢","兔子","驴","蜜蜂","普通动物","敌对生物"), List.of("")),
    SNOWY_SLOPES("积雪的山坡", Material.SNOW_BLOCK, List.of("兔子","山羊","敌对生物"), List.of("无普通动物")),
    STONY_PEAKS("裸岩山峰", Material.STONE, List.of("山羊","敌对生物"), List.of("无普通动物"))

    ;
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
