package com.molean.isletopia.infrastructure.individual;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class IronElevator implements Listener {


    public IronElevator() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onDown(PlayerToggleSneakEvent event) {
        if (!event.isSneaking())
            return;
        Block relative = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (!relative.getType().equals(Material.IRON_BLOCK)) {
            return;
        }
        World world = event.getPlayer().getWorld();
        Location location = event.getPlayer().getLocation();
        for (int i = location.getBlockY() - 2; i >= 0; i--) {
            location.setY(i);
            Block block = world.getBlockAt(location);
            if (block.getType().equals(Material.IRON_BLOCK)) {
                location.setY(i + 1);
                Block blockAt = world.getBlockAt(location);
                if (!blockAt.getType().isSolid() && !blockAt.getRelative(BlockFace.UP).getType().isSolid()) {
                    event.getPlayer().teleport(location);
                    world.playSound(location, Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onUp(PlayerJumpEvent event) {
        Block relative = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (!relative.getType().equals(Material.IRON_BLOCK)) {
            return;
        }
        World world = event.getPlayer().getWorld();
        Location location = event.getPlayer().getLocation();
        for (int i = location.getBlockY(); i < 256; i++) {
            location.setY(i);
            Block block = world.getBlockAt(location);
            if (block.getType().equals(Material.IRON_BLOCK)) {
                location.setY(i + 1);
                Block blockAt = world.getBlockAt(location);
                if (!blockAt.getType().isSolid() && !blockAt.getRelative(BlockFace.UP).getType().isSolid()) {
                    event.getPlayer().teleport(location);
                    world.playSound(location, Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
                    break;
                }
            }
        }
    }


}
