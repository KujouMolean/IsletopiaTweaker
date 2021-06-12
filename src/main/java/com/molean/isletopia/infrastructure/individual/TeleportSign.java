package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import net.kyori.adventure.text.Component;
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
        String line = ((Sign) event.getClickedBlock().getState()).line(0).toString();
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
        Component line = event.line(0);
        if (line != null && line.toString().matches("\\[[a-zA-Z0-9].*]")) {
            String target = line.toString().substring(1, line.toString().length() - 1);
            event.line(0, Component.text("[§b" + target + "§r]"));
        }
    }
}
