package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

public class RecipeUtils {
    public static void registerShaped(String name, ItemStack result, Material... materials) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), name);
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

    public static void registerShapeless(String name, ItemStack result, Material... materials) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), name);
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(namespacedKey, result);
        for (Material material : materials) {
            shapelessRecipe.addIngredient(material);
        }
        Bukkit.addRecipe(shapelessRecipe);
    }

    public static void registerStonecutting(String name, ItemStack result, Material source) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), name);
        StonecuttingRecipe stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, source);
        Bukkit.addRecipe(stonecuttingRecipe);
    }

    public static void registerSmithingRecipie(String name, ItemStack result, Material source, Material addition) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), name);
        SmithingRecipe smithingRecipe = new SmithingRecipe(namespacedKey, result, new RecipeChoice.MaterialChoice(source), new RecipeChoice.MaterialChoice(addition));
        Bukkit.addRecipe(smithingRecipe);
    }

    public static void registerBlasting(String name, ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), name);
        BlastingRecipe blastingRecipe = new BlastingRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(blastingRecipe);
    }

    public static void registerCampfire(String name, ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), name);
        CampfireRecipe campfireRecipe = new CampfireRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(campfireRecipe);
    }

    public static void registerFurnace(String name, ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), name);
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(furnaceRecipe);
    }

    public static void registerSmoking(String name, ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), name);
        SmokingRecipe smokingRecipe = new SmokingRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(smokingRecipe);
    }
    public static MerchantRecipe generateMerchant(Material result, int price, int maxUses){
        MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(result), maxUses);
        merchantRecipe.addIngredient(new ItemStack(Material.EMERALD,price));
        return merchantRecipe;
    }
}
