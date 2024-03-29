package com.molean.isletopia.infrastructure.individual;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

@Singleton
public class IronElevator implements Listener {


    private final PlayerPropertyManager playerPropertyManager;

    public IronElevator(PlayerPropertyManager playerPropertyManager) {
        this.playerPropertyManager = playerPropertyManager;
    }

    @EventHandler
    public void onDown(PlayerToggleSneakEvent event) {
        if (!event.isSneaking())
            return;
        Block relative = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (!relative.getType().equals(Material.IRON_BLOCK)) {
            return;
        }
        if (playerPropertyManager.getPropertyAsBoolean(event.getPlayer(), "DisableIronElevator")) {
            return;
        }
        World world = event.getPlayer().getWorld();
        Location location = event.getPlayer().getLocation();
        for (int i = location.getBlockY() - 2; i >= -64; i--) {
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
        if (playerPropertyManager.getPropertyAsBoolean(event.getPlayer(), "DisableIronElevator")) {
            return;
        }
        World world = event.getPlayer().getWorld();
        Location location = event.getPlayer().getLocation();
        for (int i = location.getBlockY(); i < 320; i++) {
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
