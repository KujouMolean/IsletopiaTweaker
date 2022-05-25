package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

@Singleton
public class DragonHeadExtractBreath implements Listener {
    private final PlayerManager playerManager;
    public DragonHeadExtractBreath(PlayerManager playerManager) {
        this.playerManager = playerManager;
        LocalRecipe.addRecipe(Material.DRAGON_BREATH, Material.DRAGON_HEAD,
                Material.AIR, Material.AIR, Material.AIR,
                Material.AIR, Material.GLASS_BOTTLE, Material.AIR,
                Material.AIR, Material.AIR, Material.AIR);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getPlayer())) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }
        playerManager.validate(event.getPlayer());
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        switch (clickedBlock.getType()) {
            case DRAGON_HEAD, DRAGON_WALL_HEAD:
                break;
            default:
                return;
        }
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (!itemInMainHand.getType().equals(Material.GLASS_BOTTLE)) {
            return;
        }
        if (System.currentTimeMillis() - getMeta(clickedBlock) < 60 * 1000 * 5) {
            MessageUtils.fail(event.getPlayer(), "dragonhead.refill");
            return;
        }
        //success
        setMeta(clickedBlock, System.currentTimeMillis());
        Location location = clickedBlock.getLocation();
        location.getWorld().playEffect(location, Effect.DRAGON_BREATH, null, 5);
        itemInMainHand.setAmount(itemInMainHand.getAmount() - 1);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.DRAGON_BREATH));
    }


    public void setMeta(Block block, long l) {
        block.setMetadata("LastExtract", new FixedMetadataValue(IsletopiaTweakers.getPlugin(), l));
    }

    public long getMeta(Block block) {
        List<MetadataValue> metadataValueList = block.getMetadata("LastExtract");
        if (metadataValueList.size() == 0) {
            return 0L;
        }
        return metadataValueList.get(0).asLong();
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        switch (itemInHand.getType()) {
            case DRAGON_HEAD, DRAGON_WALL_HEAD:
                break;
            default:
                return;
        }
        Block blockPlaced = event.getBlockPlaced();
        switch (blockPlaced.getType()) {
            case DRAGON_HEAD, DRAGON_WALL_HEAD:
                break;
            default:
                return;
        }
        setMeta(blockPlaced, System.currentTimeMillis());
    }
}
