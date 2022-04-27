package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemRemover implements Listener {
    public ItemRemover() {
        PluginUtils.registerEvents(this);
    }

    private static boolean warning = false;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || !item.getType().equals(Material.BOOK)) {
                continue;
            }
            if (item.getEnchantments().keySet().size() == 0) {
                continue;
            }
            inventory.setItem(i, null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void beaconCheck(InventoryOpenEvent event) {
        if (warning) {
            return;
        }
        Inventory inventory = event.getInventory();
        int cnt = 0;
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) {
                continue;
            }
            if (item.getType().equals(Material.BEACON)) {
                cnt += item.getAmount();
            }
            if (item.getType().equals(Material.HEART_OF_THE_SEA)) {
                cnt += item.getAmount();
            }
            if (item.getType().equals(Material.DRAGON_HEAD)) {
                cnt += item.getAmount();
            }
            if (item.getType().equals(Material.ELYTRA)) {
                cnt += item.getAmount();
            }
        }

        if (cnt >= 16) {
            String name = event.getPlayer().getName();
            PluginUtils.getLogger().severe(name + " contains too many beacon");
            CommonResponseObject commonResponseObject = new CommonResponseObject("服务器" + ServerInfoUpdater.getServerName() + "太流畅了，大家快来玩");
            ServerMessageUtils.sendMessage("proxy", "CommonResponse", commonResponseObject);
            warning = true;
        }
    }
}
