package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HopperFilter implements Listener {
    //todo
    public HopperFilter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent event) {
        if (!event.getInitiator().equals(event.getDestination())) {
            return;
        }
        if (!event.getInitiator().getType().equals(InventoryType.HOPPER)) {
            return;
        }
        Inventory hopper = event.getSource();
        Location location = event.getSource().getLocation();
        assert location != null;
        World world = location.getWorld();
        double x1 = location.getX() - 0.2D;
        double y1 = location.getY() - 1.2D;
        double z1 = location.getZ() - 0.2D;
        double x2 = location.getX() + 1.2D;
        double y2 = location.getY() + 0.2D;
        double z2 = location.getZ() + 1.2D;
        BoundingBox boundingBox = new BoundingBox(x1, y1, z1, x2, y2, z2);
        Collection<Entity> entities = world.getNearbyEntities(boundingBox);
        List<Material> filterInItems = new ArrayList<>();
        List<Material> filterOutItems = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof ItemFrame) {
                ItemFrame itemFrame = (ItemFrame) entity;
                if (!entity.getLocation().getBlock().getRelative(itemFrame.getAttachedFace()).equals(hopper.getLocation().getBlock())) {
                    return;
                }
                ItemStack item = itemFrame.getItem();
                switch (itemFrame.getRotation()) {
                    case CLOCKWISE_45:
                    case CLOCKWISE_135:
                    case FLIPPED_45:
                    case COUNTER_CLOCKWISE_45:
                        filterOutItems.add(item.getType());
                        break;
                    default:
                        filterInItems.add(item.getType());
                        break;
                }
            }
        }
        if (filterInItems.size() == 0 && filterOutItems.size() == 0) {
            return;
        }
        event.setCancelled(true);
        for (ItemStack itemStack : event.getSource()) {
            if (filterInItems.contains(itemStack.getType())) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                ItemStack clone = itemStack.clone();
                clone.setAmount(1);
                event.getDestination().addItem(clone);
                break;
            }
        }
    }

}
