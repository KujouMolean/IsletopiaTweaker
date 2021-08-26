package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class PlayerRidePlayer implements Listener {
    public PlayerRidePlayer() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEntityEvent event){
        if(!PlotUtils.hasCurrentPlotPermission(event.getPlayer())){
            return;
        }
        PlayerInventory inventory = event.getPlayer().getInventory();
        if (!inventory.getItemInMainHand().getType().equals(Material.AIR)) {
            return;
        }
        if (!inventory.getItemInOffHand().getType().equals(Material.AIR)) {
            return;
        }
        Entity rightClicked = event.getRightClicked();
        if (rightClicked.getType().equals(EntityType.PLAYER)) {
            event.getPlayer().addPassenger(rightClicked);
        }
    }

    @EventHandler
    public void on(PlayerSwapHandItemsEvent event){

        Player player = event.getPlayer();
        if(player.getPassengers().isEmpty()){
            return;
        }
        Vector multiply = player.getLocation().getDirection().clone().multiply(20);

        event.setCancelled(true);

        for (Entity passenger : player.getPassengers()) {
            player.eject();
            passenger.setVelocity(multiply);
        }
    }
}
