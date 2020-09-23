package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TeleportSign implements Listener {


    public TeleportSign() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null)
            return;
        if (event.getClickedBlock().getType() == Material.AIR)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (!(event.getClickedBlock().getState() instanceof Sign))
            return;
        String line = ((Sign) event.getClickedBlock().getState()).getLine(0);
        line = line.replaceAll("§.", "");
        if (!line.matches("\\[[a-zA-Z0-9].*]"))
            return;
        String target = line.substring(1, line.length() - 1);
        String cmd = "visit " + target;
        event.getPlayer().performCommand(cmd);
        Bukkit.getLogger().info(event.getPlayer().getName() + " issued command by sign: " + cmd);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String line = event.getLine(0);
        if (line != null && line.matches("\\[[a-zA-Z0-9].*]")) {
            String target = line.substring(1, line.length() - 1);
            event.setLine(0, "[§b" + target + "§r]");
        }
    }
}
