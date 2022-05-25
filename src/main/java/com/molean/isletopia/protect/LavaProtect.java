package com.molean.isletopia.protect;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;

@Singleton
public class LavaProtect implements Listener {


    private final PlayerPropertyManager playerPropertyManager;
    public LavaProtect(PlayerPropertyManager playerPropertyManager) {
        this.playerPropertyManager = playerPropertyManager;
    }

    //allow bucket obsidian to lava
    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void onBucketFillObsidian(PlayerInteractEvent event) {
        if (Action.RIGHT_CLICK_BLOCK != event.getAction())
            return;
        if (!Material.BUCKET.equals(event.getMaterial()))
            return;
        if (playerPropertyManager.getPropertyAsBoolean(event.getPlayer(), "DisableLavaProtect")) {
            return;
        }
        assert event.getClickedBlock() != null;
        if (!Material.OBSIDIAN.equals(event.getClickedBlock().getType()))
            return;
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getPlayer())) {
            return;
        }

        event.getClickedBlock().setType(Material.LAVA);
        event.setCancelled(true);
    }


    //disable replace lava by block
    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void onLavaReplacing(BlockPlaceEvent event) {
        if(event.getPlayer().isOp()){
            return;
        }
        if (playerPropertyManager.getPropertyAsBoolean(event.getPlayer(), "DisableLavaProtect")) {
            return;
        }
        BlockState blockReplacedState = event.getBlockReplacedState();
        BlockData blockData = blockReplacedState.getBlockData();
        String asString = blockData.getAsString();
        if ("minecraft:lava[level=0]".equalsIgnoreCase(asString)) {
            event.setCancelled(true);
            MessageUtils.notify(event.getPlayer(), "lava.protect.block");
        }
    }

    //disable water lava
    @EventHandler
    public void onWateringLava(PlayerBucketEmptyEvent event) {
        if(event.getPlayer().isOp()){
            return;
        }
        if (playerPropertyManager.getPropertyAsBoolean(event.getPlayer(), "DisableLavaProtect")) {
            return;
        }
        String asString = event.getBlock().getBlockData().getAsString();
        if ("minecraft:lava[level=0]".equalsIgnoreCase(asString)) {
            event.setCancelled(true);
            MessageUtils.notify(event.getPlayer(), "lava.protect.water");
        }
    }

    //notify player that lava could cause fire
    @EventHandler
    public void onPlaceLava(PlayerBucketEmptyEvent event) {
        Material bucket = event.getBucket();
        if (!bucket.equals(Material.LAVA_BUCKET)) {
            return;
        }
        int statistic = event.getPlayer().getStatistic(Statistic.USE_ITEM, Material.LAVA_BUCKET);
        if (statistic > 3) {
            return;
        }
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getPlayer())) {
            return;
        }
        MessageUtils.notify(event.getPlayer(), "lava.notify.fire");
    }

    //notify player that empty bucket can restore obsidian to lava
    @EventHandler
    public void onLavaTransferring(BlockFormEvent event) {
        if (!"minecraft:lava[level=0]".equals(event.getBlock().getBlockData().getAsString()))
            return;
        Location location = event.getBlock().getLocation();
        Collection<Entity> nearbyEntities = event.getBlock().getWorld().getNearbyEntities(location, 8, 8, 8);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof Player player) {
                MessageUtils.notify(player, "lava.notify.restore");
            }
        }
    }
}
