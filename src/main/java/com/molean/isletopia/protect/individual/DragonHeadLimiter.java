package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.utils.NMSTagUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class DragonHeadLimiter implements Listener {

    public DragonHeadLimiter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        switch (blockPlaced.getType()) {
            case DRAGON_HEAD, DRAGON_WALL_HEAD:
                break;
            default:
                return;
        }
        ItemStack itemInHand = event.getItemInHand();
        String bind = NMSTagUtils.get(itemInHand, "bind");
        if (bind == null || bind.isEmpty()) {
            return;
        }
        Location location = event.getBlockPlaced().getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            event.setBuild(false);
            return;
        }
        if (!Objects.equals(UUIDUtils.get(currentIsland.getUuid()), bind)) {
            event.setBuild(false);
            event.getPlayer().sendMessage("§c这个龙首只能放在 " + bind + " 岛屿");
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void on(ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();

        switch (itemStack.getType()) {
            case DRAGON_HEAD, DRAGON_WALL_HEAD:
                break;
            default:
                return;
        }
        String bind = NMSTagUtils.get(itemStack, "bind");
        if (bind != null && !bind.isEmpty()) {
            return;
        }
        Location location = event.getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            return;
        }
        String owner = UUIDUtils.get(currentIsland.getUuid());
        itemStack.lore(List.of(Component.text("§c绑定=>" + owner)));
        item.setItemStack(NMSTagUtils.set(itemStack, "bind", owner));
    }
}
