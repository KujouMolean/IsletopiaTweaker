package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import com.molean.isletopia.tweakers.PlotUtils;
import org.bukkit.Bukkit;
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

public class LavaProtect implements Listener {


    public LavaProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBucketFillObsidian(PlayerInteractEvent event) {
        if (Action.RIGHT_CLICK_BLOCK != event.getAction())
            return;
        if (!Material.BUCKET.equals(event.getMaterial()))
            return;
        assert event.getClickedBlock() != null;
        if (!Material.OBSIDIAN.equals(event.getClickedBlock().getType()))
            return;
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            return;
        }
        event.getClickedBlock().setType(Material.LAVA);
        event.setCancelled(true);
    }

    @EventHandler
    public void onLavaReplacing(BlockPlaceEvent event) {
        BlockState blockReplacedState = event.getBlockReplacedState();
        BlockData blockData = blockReplacedState.getBlockData();
        String asString = blockData.getAsString();
        if ("minecraft:lava[level=0]".equalsIgnoreCase(asString)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§8[§3温馨提示§8] §e岩浆受到保护,只能用空桶收起.");
        }
    }

    @EventHandler
    public void onWateringLava(PlayerBucketEmptyEvent event) {
        String asString = event.getBlock().getBlockData().getAsString();
        if ("minecraft:lava[level=0]".equalsIgnoreCase(asString)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§8[§3温馨提示§8] §e岩浆受到保护,只能用空桶收起.");
        }
    }

    @EventHandler
    public void onPlaceLava(PlayerBucketEmptyEvent event) {
        Material bucket = event.getBucket();
        if (!bucket.equals(Material.LAVA_BUCKET)) {
            return;
        }
        int statistic = event.getPlayer().getStatistic(Statistic.USE_ITEM, Material.LAVA_BUCKET);
        if (statistic > 1) {
            return;
        }
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            return;
        }
        event.getPlayer().sendMessage("§8[§3温馨提示§8] §e岩浆会引起火灾,请务必注意.");
    }

    @EventHandler
    public void onLavaTransferring(BlockFormEvent event) {
        if (event.getBlock().getType() != Material.OBSIDIAN)
            return;
        Location location = event.getBlock().getLocation();
        Collection<Entity> nearbyEntities = event.getBlock().getWorld().getNearbyEntities(location, 32, 32, 32);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof Player) {
                nearbyEntity.sendMessage("§8[§3温馨提示§8] §e空桶右键黑曜石可以还原成岩浆.");
            }
        }

    }
}
