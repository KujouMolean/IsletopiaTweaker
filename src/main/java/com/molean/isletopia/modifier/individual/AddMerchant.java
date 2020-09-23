package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class AddMerchant implements Listener {
    public AddMerchant() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntityType().equals(EntityType.WANDERING_TRADER)) {
            WanderingTrader wanderingTrader = (WanderingTrader) event.getEntity();
            List<MerchantRecipe> recipes = new ArrayList<>(wanderingTrader.getRecipes());
//            可可豆
            recipes.add(generateMerchant(Material.COCOA_BEANS, 3, 5));
//            紫颂花
            recipes.add(generateMerchant(Material.CHORUS_FLOWER, 3, 5));
//            地狱疣
            recipes.add(generateMerchant(Material.NETHER_WART, 3, 5));
//            诡异菌
            recipes.add(generateMerchant(Material.CRIMSON_FUNGUS, 3, 5));
//            绯红菌
            recipes.add(generateMerchant(Material.WARPED_FUNGUS, 3, 5));
//            甜浆果
            recipes.add(generateMerchant(Material.SWEET_BERRIES, 3, 5));
//            竹子
            recipes.add(generateMerchant(Material.MELON_SEEDS, 3, 5));
//            甜菜根种子
            recipes.add(generateMerchant(Material.BEETROOT_SEEDS, 3, 5));
            wanderingTrader.setRecipes(recipes);
        }
    }
    public static MerchantRecipe generateMerchant(Material result, int price, int maxUses){
        MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(result), maxUses);
        merchantRecipe.addIngredient(new ItemStack(Material.EMERALD,price));
        return merchantRecipe;
    }
}
