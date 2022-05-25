package com.molean.isletopia.modifier;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.utils.HeadUtils;
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

import static org.bukkit.Material.*;

@Singleton
public class RichWanderingTrader implements Listener {
    private static final List<Material> additions = new ArrayList<>();

    public RichWanderingTrader() {

        additions.add(COCOA_BEANS);
        additions.add(CHORUS_FLOWER);
        additions.add(NETHER_WART);
        additions.add(CRIMSON_FUNGUS);
        additions.add(WARPED_FUNGUS);
        additions.add(SWEET_BERRIES);
        additions.add(BEETROOT_SEEDS);
        additions.add(GLOW_LICHEN);
        additions.add(SPORE_BLOSSOM);
        additions.add(GRASS_BLOCK);
        {
            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(HeadUtils.getSkullFromValue("wanderingTrader.name", PlayerHeadDrop.drops.get(EntityType.WANDERING_TRADER)));
            for (Material addition : additions) {
                icons.add(new ItemStack(addition));
                result.add(new ItemStack(addition));
            }

            List<ItemStack[]> sources = new ArrayList<>();
            ItemStack[] itemStacks = new ItemStack[9];
            for (int i = 0; i < itemStacks.length; i++) {
                itemStacks[i] = new ItemStack(AIR);
            }
            itemStacks[4] = new ItemStack(Material.EMERALD, 3);

            sources.add(itemStacks);

            LocalRecipe.addRecipe(icons, types, sources, result);


        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(EntitySpawnEvent event) {
        if (!event.getEntityType().equals(EntityType.WANDERING_TRADER)) {
            return;
        }
        WanderingTrader wanderingTrader = (WanderingTrader) event.getEntity();
        List<MerchantRecipe> recipes = new ArrayList<>(wanderingTrader.getRecipes());
        for (Material addition : additions) {
            recipes.add(generateMerchant(addition, 1, 3, 5));
        }
        recipes.add(generateMerchant(RED_SAND, 1, SAND, 1, EMERALD, 16, Integer.MAX_VALUE));
        recipes.add(generateMerchant(LAPIS_LAZULI, 1, 16, Integer.MAX_VALUE));
        wanderingTrader.setRecipes(recipes);
    }

    public static MerchantRecipe generateMerchant(Material result, int resultAmount, int price, int maxUses) {
        MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(result, resultAmount), maxUses);
        merchantRecipe.addIngredient(new ItemStack(Material.EMERALD, price));
        return merchantRecipe;
    }

    public static MerchantRecipe generateMerchant(Material result, int resultAmount, Material money, int price, int maxUses) {
        MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(result, resultAmount), maxUses);
        merchantRecipe.addIngredient(new ItemStack(money, price));
        return merchantRecipe;
    }

    public static MerchantRecipe generateMerchant(Material result, int resultAmount, Material money1, int price1, Material money2, int price2, int maxUses) {
        MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(result, resultAmount), maxUses);
        merchantRecipe.addIngredient(new ItemStack(money1, price1));
        merchantRecipe.addIngredient(new ItemStack(money2, price2));
        return merchantRecipe;
    }
}
