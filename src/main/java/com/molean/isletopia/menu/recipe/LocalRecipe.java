package com.molean.isletopia.menu.recipe;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LocalRecipe {
    static List<LocalRecipe> localRecipeList = new ArrayList<>();

    public static void addRecipe(List<ItemStack> icons, List<ItemStack> types, List<ItemStack[]> sources, List<ItemStack> results) {
        localRecipeList.add(new LocalRecipe(icons, types, sources, results));
    }

    public static void addRecipe(ItemStack icon, ItemStack type, ItemStack[] source, ItemStack result) {
        List<ItemStack> icons = List.of(icon);
        List<ItemStack> types = List.of(type);
        List<ItemStack[]> sources = new ArrayList<>();
        sources.add(source);
        List<ItemStack> results = List.of(result);
        localRecipeList.add(new LocalRecipe(icons, types, sources, results));
    }

    public static void addRecipe(Material icon, Material type, Material[] source, Material result) {
        ItemStack[] itemStacks = new ItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            itemStacks[i] = new ItemStack(source[i]);
        }

        addRecipe(new ItemStack(icon), new ItemStack(type), itemStacks, new ItemStack(result));
    }

    public static void addRecipe(Material icon, Material type, ItemStack result, Material... source) {
        ItemStack[] itemStacks = new ItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            itemStacks[i] = new ItemStack(source[i]);
        }

        addRecipe(new ItemStack(icon), new ItemStack(type), itemStacks, result);
    }

    public static void addRecipe(Material icon, Material type, Material... source) {
        addRecipe(icon, type, new ItemStack(icon), source);
    }


    List<ItemStack> icons;
    List<ItemStack> types;
    List<ItemStack[]> sources;
    List<ItemStack> results;

    public LocalRecipe(List<ItemStack> icons, List<ItemStack> types, List<ItemStack[]> sources, List<ItemStack> results) {
        this.icons = icons;
        this.types = types;
        this.sources = sources;
        this.results = results;
    }

}
