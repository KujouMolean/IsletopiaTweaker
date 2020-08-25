package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.UUID;

public class RecipeUtils {
    public static void registerShaped(ItemStack result, Material... materials) {
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

    public static void registerShapeless(ItemStack result, Material... materials) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(namespacedKey, result);
        for (Material material : materials) {
            shapelessRecipe.addIngredient(material);
        }
        Bukkit.addRecipe(shapelessRecipe);
    }

    public static void registerStonecutting(ItemStack result, Material source) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        StonecuttingRecipe stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, source);
        Bukkit.addRecipe(stonecuttingRecipe);
    }

    public static void registerSmithingRecipie(ItemStack result, Material source, Material addition) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        SmithingRecipe smithingRecipe = new SmithingRecipe(namespacedKey, result, new RecipeChoice.MaterialChoice(source), new RecipeChoice.MaterialChoice(addition));
        Bukkit.addRecipe(smithingRecipe);
    }

    public static void registerBlasting(ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        BlastingRecipe blastingRecipe = new BlastingRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(blastingRecipe);
    }

    public static void registerCampfire(ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        CampfireRecipe campfireRecipe = new CampfireRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(campfireRecipe);
    }

    public static void registerFurnace(ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(furnaceRecipe);
    }

    public static void registerSmoking(ItemStack result, Material source, float exp, int time) {
        NamespacedKey namespacedKey = new NamespacedKey(IsletopiaTweakers.getPlugin(), UUID.randomUUID().toString());
        SmokingRecipe smokingRecipe = new SmokingRecipe(namespacedKey, result, source, exp, time);
        Bukkit.addRecipe(smokingRecipe);
    }
    public static MerchantRecipe generateMerchant(Material result, int price, int maxUses){
        MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(result), maxUses);
        merchantRecipe.addIngredient(new ItemStack(Material.EMERALD,price));
        return merchantRecipe;
    }
}
