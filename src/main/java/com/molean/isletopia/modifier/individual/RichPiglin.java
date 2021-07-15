package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.utils.HeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.bukkit.Material.*;

public class RichPiglin implements Listener {
    public RichPiglin() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        {
            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(HeadUtils.getSkullFromValue("猪灵", PlayerHeadDrop.drops.get(EntityType.PIGLIN)));
            icons.add(new ItemStack(SMALL_AMETHYST_BUD));
            icons.add(new ItemStack(MEDIUM_AMETHYST_BUD));
            icons.add(new ItemStack(LARGE_AMETHYST_BUD));
            icons.add(new ItemStack(AMETHYST_CLUSTER));
            result.add(new ItemStack(SMALL_AMETHYST_BUD));
            result.add(new ItemStack(MEDIUM_AMETHYST_BUD));
            result.add(new ItemStack(LARGE_AMETHYST_BUD));
            result.add(new ItemStack(AMETHYST_CLUSTER));
            List<ItemStack[]> sources = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                ItemStack[] itemStacks = new ItemStack[9];
                for (int j = 0; j < itemStacks.length; j++) {
                    itemStacks[j] = new ItemStack(AIR);
                }
                itemStacks[4] = new ItemStack(GOLD_BLOCK);
                sources.add(itemStacks);
            }
            LocalRecipe.addRecipe(icons, types, sources, result);
        }
        {
            List<ItemStack> icons = new ArrayList<>();
            List<ItemStack> result = new ArrayList<>();
            List<ItemStack> types = new ArrayList<>();
            types.add(HeadUtils.getSkullFromValue("猪灵", PlayerHeadDrop.drops.get(EntityType.PIGLIN)));
            icons.add(new ItemStack(ENCHANTED_GOLDEN_APPLE));
            result.add(new ItemStack(ENCHANTED_GOLDEN_APPLE));
            result.add(new ItemStack(APPLE));
            List<ItemStack[]> sources = new ArrayList<>();
            ItemStack[] itemStacks = new ItemStack[9];
            for (int j = 0; j < itemStacks.length; j++) {
                itemStacks[j] = new ItemStack(AIR);
            }
            itemStacks[4] = new ItemStack(GOLDEN_APPLE);
            sources.add(itemStacks);

            LocalRecipe.addRecipe(icons, types, sources, result);
        }
    }

    @EventHandler
    public void on(EntitySpawnEvent event) {
        EntityType entityType = event.getEntityType();
        if (entityType.equals(EntityType.PIGLIN)) {
            Piglin entity = (Piglin) event.getEntity();
            entity.addBarterMaterial(GOLD_BLOCK);
            entity.addBarterMaterial(GOLDEN_APPLE);
            entity.addBarterMaterial(YELLOW_DYE);
        }
    }

    @EventHandler
    public void on(PiglinBarterEvent event) {
        if (event.getInput().getType().equals(GOLDEN_APPLE)) {
            List<ItemStack> outcome = event.getOutcome();
            outcome.clear();
            Random random = new Random();
            if (random.nextInt(1000) < 1) {
                outcome.add(new ItemStack(ENCHANTED_GOLDEN_APPLE));
            } else {
                outcome.add(new ItemStack(APPLE));
            }
        }
        if (event.getInput().getType().equals(YELLOW_DYE)) {
            List<ItemStack> outcome = event.getOutcome();
            outcome.clear();
            Collection<LivingEntity> livingEntities = event.getEntity().getLocation().getNearbyLivingEntities(15);
            for (LivingEntity livingEntity : livingEntities) {
                if (livingEntity instanceof Player) {
                    livingEntity.sendMessage("<Piglin> 敢用染料骗老子, 你死定了!");
                    event.getEntity().setTarget(livingEntity);
                }
            }
            Location location = event.getEntity().getLocation();
            event.getEntity().remove();
            PiglinBrute entity = (PiglinBrute) location.getWorld().spawnEntity(location, EntityType.PIGLIN_BRUTE);
            entity.setImmuneToZombification(true);
        }
        if (event.getInput().getType().equals(GOLD_BLOCK)) {
            List<ItemStack> outcome = event.getOutcome();
            outcome.clear();
            Random random = new Random();
            switch (random.nextInt(4)) {
                case 0:
                    outcome.add(new ItemStack(SMALL_AMETHYST_BUD));
                    break;
                case 1:
                    outcome.add(new ItemStack(MEDIUM_AMETHYST_BUD));
                    break;
                case 2:
                    outcome.add(new ItemStack(LARGE_AMETHYST_BUD));
                    break;
                case 3:
                    outcome.add(new ItemStack(AMETHYST_CLUSTER));
                    break;
            }
        }

    }
}
