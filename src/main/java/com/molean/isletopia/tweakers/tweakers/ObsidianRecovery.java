package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ObsidianRecovery implements Listener {
    public ObsidianRecovery() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Action.RIGHT_CLICK_BLOCK != event.getAction())
            return;
        if (!Material.BUCKET.equals(event.getMaterial()))
            return;
        if (!Material.OBSIDIAN.equals(event.getClickedBlock().getType()))
            return;
        event.getClickedBlock().setType(Material.LAVA);
    }
}
