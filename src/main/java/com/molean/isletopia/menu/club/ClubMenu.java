package com.molean.isletopia.menu.club;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.menu.ItemStackSheet;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class ClubMenu implements Listener {
    private final Player player;
    private final Inventory inventory;

    public ClubMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 36, Component.text("社团列表"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        List<String> clubs = UniversalParameter.getParameterAsList("Molean", "clubs");
        for (int i = 0; i < clubs.size(); i++) {
            Material clubIcon = null;
            try {
                clubIcon = Material.valueOf(UniversalParameter.getParameter(clubs.get(i), "clubIcon"));
            } catch (Exception e) {
                clubIcon = Material.GRASS_BLOCK;
            }

            String rawClubGoal = UniversalParameter.getParameter(clubs.get(i), "clubGoal");
            if (rawClubGoal == null || rawClubGoal.isEmpty()) {

            }else{
                String[] split = rawClubGoal.split("#");
            }


            ItemStackSheet itemStackSheet = new ItemStackSheet(clubIcon, clubs.get(i));



        }


        //here place icon

        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
        if (!event.getClick().equals(ClickType.LEFT)) {
            return;
        }
        int slot = event.getSlot();
        switch (slot) {
            case 1:
                break;
            case 2:
                break;
        }


    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.getHandlers().unregister(this);
    }
}
