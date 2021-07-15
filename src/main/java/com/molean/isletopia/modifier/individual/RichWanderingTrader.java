package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.utils.HeadUtils;
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

import static org.bukkit.Material.*;

public class RichWanderingTrader implements Listener {
    private static final List<Material> additions = new ArrayList<>();
    public RichWanderingTrader() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        additions.add(COCOA_BEANS);
        additions.add(CHORUS_FLOWER);
        additions.add(NETHER_WART);
        additions.add(CRIMSON_FUNGUS);
        additions.add(WARPED_FUNGUS);
        additions.add(SWEET_BERRIES);
        additions.add(BEETROOT_SEEDS);
        additions.add(GLOW_LICHEN);
        additions.add(SPORE_BLOSSOM);
        {
            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(HeadUtils.getSkullFromValue("流浪商人", PlayerHeadDrop.drops.get(EntityType.WANDERING_TRADER)));
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

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (!event.getEntityType().equals(EntityType.WANDERING_TRADER)) {
            return;
        }
        WanderingTrader wanderingTrader = (WanderingTrader) event.getEntity();
        List<MerchantRecipe> recipes = new ArrayList<>(wanderingTrader.getRecipes());
        for (Material addition : additions) {
            recipes.add(generateMerchant(addition, 3, 5));
        }
        wanderingTrader.setRecipes(recipes);
    }

    public static MerchantRecipe generateMerchant(Material result, int price, int maxUses) {
        MerchantRecipe merchantRecipe = new MerchantRecipe(new ItemStack(result), maxUses);
        merchantRecipe.addIngredient(new ItemStack(Material.EMERALD, price));
        return merchantRecipe;
    }
}
