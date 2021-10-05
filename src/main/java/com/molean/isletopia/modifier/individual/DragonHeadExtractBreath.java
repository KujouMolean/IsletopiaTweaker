package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DragonHeadExtractBreath implements Listener {
    public DragonHeadExtractBreath() {

        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        LocalRecipe.addRecipe(Material.DRAGON_BREATH, Material.DRAGON_HEAD,
                Material.AIR,Material.AIR,Material.AIR,
                Material.AIR,Material.GLASS_BOTTLE,Material.AIR,
                Material.AIR,Material.AIR,Material.AIR);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getPlayer())) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !clickedBlock.getType().equals(Material.DRAGON_HEAD)) {
            return;
        }
        Location location = clickedBlock.getLocation();
        location.getWorld().playEffect(location, Effect.DRAGON_BREATH, null, 5);
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (!itemInMainHand.getType().equals(Material.GLASS_BOTTLE)) {
            return;
        }
        itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.DRAGON_BREATH));

    }
}
