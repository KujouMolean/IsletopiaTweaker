package com.molean.isletopia.modifier.individual;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public class TestListener implements Listener {
    public TestListener() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }



}
