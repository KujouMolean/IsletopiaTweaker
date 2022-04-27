package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.protect.individual.IslandMobCap;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.utils.NMSUtils;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.lang.reflect.Field;

public class NetherAdapter implements Listener {


    public NetherAdapter() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        PluginUtils.registerEvents(this);
        Class<?> dimensionManagerClass = NMSUtils.getNMSClass("world.level.dimension.DimensionManager");
        Field q = dimensionManagerClass.getDeclaredField("q");
        q.setAccessible(true);
        Object o = q.get(null);
        Field b = dimensionManagerClass.getDeclaredField("B");
        b.setAccessible(true);
        b.set(o, 1.0);
    }

    @EventHandler
    public void on(PortalCreateEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) {
            event.setCancelled(true);
            return;
        }
        IslandId islandId = IslandId.fromLocation(ServerInfoUpdater.getServerName(), entity.getLocation().getBlockX(), entity.getLocation().getBlockZ());

        for (BlockState block : event.getBlocks()) {
            if (!islandId.equals(IslandId.fromLocation(ServerInfoUpdater.getServerName(), block.getX(), block.getZ()))) {
                event.setCancelled(true);
                return;
            }
        }

    }
}
