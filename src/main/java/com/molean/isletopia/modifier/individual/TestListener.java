package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import io.papermc.paper.event.player.PlayerBedFailEnterEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class TestListener implements Listener {
    public TestListener() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }


    @EventHandler
    public void on(PlayerToggleSneakEvent event) {

    }


    @EventHandler
    public void on(PlayerBedEnterEvent event) {

    }

    @EventHandler
    public void on(PlayerBedLeaveEvent event){

    }
}
