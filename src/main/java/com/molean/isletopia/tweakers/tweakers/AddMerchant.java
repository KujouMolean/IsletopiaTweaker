package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import com.molean.isletopia.tweakers.RecipeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
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
            recipes.add(RecipeUtils.generateMerchant(Material.COCOA_BEANS, 3, 3));
            recipes.add(RecipeUtils.generateMerchant(Material.CHORUS_FLOWER, 3, 3));
            recipes.add(RecipeUtils.generateMerchant(Material.NETHER_WART, 3, 3));
            recipes.add(RecipeUtils.generateMerchant(Material.CRIMSON_FUNGUS, 3, 3));
            recipes.add(RecipeUtils.generateMerchant(Material.WARPED_FUNGUS, 3, 3));
            recipes.add(RecipeUtils.generateMerchant(Material.SWEET_BERRIES, 3, 3));
            recipes.add(RecipeUtils.generateMerchant(Material.BAMBOO, 3, 3));
            recipes.add(RecipeUtils.generateMerchant(Material.MELON_SEEDS, 3, 3));
            recipes.add(RecipeUtils.generateMerchant(Material.BEETROOT_SEEDS, 3, 3));
            recipes.add(RecipeUtils.generateMerchant(Material.PUMPKIN_SEEDS, 3, 3));
            wanderingTrader.setRecipes(recipes);
        }
    }
}
