package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Piston;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.ArrayList;
import java.util.List;

public class EdgeBlockDetect implements Listener {

    public EdgeBlockDetect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    //disable piston push edge block
    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonExtendEvent event) {
        {
            int fromX = event.getBlock().getLocation().getBlockX() >> 9;
            int fromZ = event.getBlock().getLocation().getBlockZ() >> 9;
            Block relative = event.getBlock().getRelative(event.getDirection());
            int toX = relative.getLocation().getBlockX() >> 9;
            int toZ = relative.getLocation().getBlockZ() >> 9;
            if (!(fromX == toX && fromZ == toZ)){
                event.setCancelled(true);
            }
        }

        for (Block block : event.getBlocks()) {
            int fromX = block.getLocation().getBlockX() >> 9;
            int fromZ = block.getLocation().getBlockZ() >> 9;
            Block relative = block.getRelative(event.getDirection());
            int toX = relative.getLocation().getBlockX() >> 9;
            int toZ = relative.getLocation().getBlockZ() >> 9;
            if (fromX == toX && fromZ == toZ) {
                continue;
            }
            event.setCancelled(true);
        }
    }


    //disable piston pull edge block
    @EventHandler(ignoreCancelled = true)
    public void on(BlockPistonRetractEvent event) {

        for (Block block : event.getBlocks()) {
            int fromX = block.getLocation().getBlockX() >> 9;
            int fromZ = block.getLocation().getBlockZ() >> 9;
            Block relative = block.getRelative(event.getDirection());
            int toX = relative.getLocation().getBlockX() >> 9;
            int toZ = relative.getLocation().getBlockZ() >> 9;
            if (fromX == toX && fromZ == toZ) {
                continue;
            }
            event.setCancelled(true);
        }
    }



    //disable liquid flow to other island
    @EventHandler(ignoreCancelled = true)
    public void on(BlockFromToEvent event) {
        int fromX = event.getBlock().getLocation().getBlockX() >> 9;
        int fromZ = event.getBlock().getLocation().getBlockZ() >> 9;
        int toX = event.getToBlock().getLocation().getBlockX() >> 9;
        int toZ = event.getToBlock().getLocation().getBlockZ() >> 9;
        if (fromX == toX && fromZ == toZ) {
            return;
        }
        event.setCancelled(true);
    }

    //disable tree grow to other island
    @EventHandler(ignoreCancelled = true)
    public void on(StructureGrowEvent event) {
        int fromX = event.getLocation().getBlockX() >> 9;
        int fromZ = event.getLocation().getBlockZ() >> 9;
        for (BlockState block : new ArrayList<>(event.getBlocks())) {
            int toX = block.getLocation().getBlockX() >> 9;
            int toZ = block.getLocation().getBlockZ() >> 9;
            if (fromX == toX && fromZ == toZ) {
                continue;
            }
            event.getBlocks().remove(block);
        }
    }

    //disable multi block placed to other island
    @EventHandler(ignoreCancelled = true)
    public void on(BlockMultiPlaceEvent event) {
        int fromX = event.getBlock().getLocation().getBlockX() >> 9;
        int fromZ = event.getBlock().getLocation().getBlockZ() >> 9;
        for (BlockState replacedBlockState : event.getReplacedBlockStates()) {
            int toX = replacedBlockState.getLocation().getBlockX() >> 9;
            int toZ = replacedBlockState.getLocation().getBlockZ() >> 9;
            if (fromX == toX && fromZ == toZ) {
                continue;
            }
            event.setCancelled(true);

        }
    }

    //disable fire pass to other island
    @EventHandler(ignoreCancelled = true)
    public void on(BlockIgniteEvent event) {
        Block ignitingBlock = event.getIgnitingBlock();

        if (ignitingBlock == null) {
            return;
        }
        int fromX = event.getBlock().getLocation().getBlockX() >> 9;
        int fromZ = event.getBlock().getLocation().getBlockZ() >> 9;
        int toX = event.getIgnitingBlock().getLocation().getBlockX() >> 9;
        int toZ = event.getIgnitingBlock().getLocation().getBlockZ() >> 9;
        if (fromX == toX && fromZ == toZ) {
            return;
        }
        event.setCancelled(true);
    }

    //disable sponge absorb other island water
    @EventHandler
    public void on(SpongeAbsorbEvent event) {
        int fromX = event.getBlock().getLocation().getBlockX() >> 9;
        int fromZ = event.getBlock().getLocation().getBlockZ() >> 9;
        for (BlockState block : new ArrayList<>(event.getBlocks())) {
            int toX = block.getLocation().getBlockX() >> 9;
            int toZ = block.getLocation().getBlockZ() >> 9;
            if (fromX == toX && fromZ == toZ) {
                continue;
            }
            event.getBlocks().remove(block);
        }
    }

    //disable entity explosion break block in other island
    @EventHandler
    public void on(EntityExplodeEvent event) {
        Location location = event.getEntity().getLocation();
        int fromX = location.getBlockX() >> 9;
        int fromZ = location.getBlockZ() >> 9;
        for (Block block : new ArrayList<>(event.blockList())) {
            int toX = block.getLocation().getBlockX() >> 9;
            int toZ = block.getLocation().getBlockZ() >> 9;
            if (fromX == toX && fromZ == toZ) {
                continue;
            }
            event.blockList().remove(block);
        }
    }

    //disable block explosion break block in other island
    @EventHandler
    public void on(BlockExplodeEvent event) {
        Location location = event.getBlock().getLocation();
        int fromX = location.getBlockX() >> 9;
        int fromZ = location.getBlockZ() >> 9;
        for (Block block : new ArrayList<>(event.blockList())) {
            int toX = block.getLocation().getBlockX() >> 9;
            int toZ = block.getLocation().getBlockZ() >> 9;
            if (fromX == toX && fromZ == toZ) {
                continue;
            }
            event.blockList().remove(block);
        }
    }


    // disable block explosion damage entity in other island
    @EventHandler
    public void on(EntityDamageByBlockEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            return;
        }
        if (event.getDamager() == null) {
            return;
        }

        Location location = event.getDamager().getLocation();
        int fromX = location.getBlockX() >> 9;
        int fromZ = location.getBlockZ() >> 9;
        int toX = event.getEntity().getLocation().getBlockX() >> 9;
        int toZ = event.getEntity().getLocation().getBlockZ() >> 9;
        if (fromX == toX && fromZ == toZ) {
            return;
        }
        event.setCancelled(true);
    }

    // disable entity explosion damage entity in other island
    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            return;
        }

        Location location = event.getDamager().getLocation();
        int fromX = location.getBlockX() >> 9;
        int fromZ = location.getBlockZ() >> 9;
        int toX = event.getEntity().getLocation().getBlockX() >> 9;
        int toZ = event.getEntity().getLocation().getBlockZ() >> 9;
        if (fromX == toX && fromZ == toZ) {
            return;
        }
        event.setCancelled(true);
    }

}
