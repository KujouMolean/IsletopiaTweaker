package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.utils.UUIDUtils;
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
        @SuppressWarnings("all")
        String firstLine = ((Sign) event.getClickedBlock().getState()).getLine(0);
        @SuppressWarnings("all")
        String secondLine = ((Sign) event.getClickedBlock().getState()).getLine(1);
        firstLine = firstLine.replaceAll("§.", "");
        secondLine = secondLine.replaceAll("§.", "");
        if (firstLine.matches("\\[[#a-zA-Z0-9_]*]")) {
            String target = firstLine.substring(1, firstLine.length() - 1);

            if (!target.startsWith("#")) {
                if (UUIDUtils.get(target) == null) {
                    target = "#" + target;
                }
            }

            String cmd = "visit " + target;
            event.getPlayer().performCommand(cmd);
            Bukkit.getLogger().info(event.getPlayer().getName() + " issued command by sign: " + cmd);
        }
        if (firstLine.matches("\\[[#a-zA-Z0-9_]*") && secondLine.matches("[#a-zA-Z0-9_]*]")) {
            String target = (firstLine + secondLine).substring(1, firstLine.length()+secondLine.length() - 1);
            if (!target.startsWith("#")) {
                if (UUIDUtils.get(target) == null) {
                    target = "#" + target;
                }
            }
            String cmd = "visit " + target;
            event.getPlayer().performCommand(cmd);
            Bukkit.getLogger().info(event.getPlayer().getName() + " issued command by sign: " + cmd);
        }

    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        @SuppressWarnings("all")
        String line = event.getLine(0);
        if (line != null && line.matches("\\[[#a-zA-Z0-9_]*]")) {
            String target = line.substring(1, line.length() - 1);
            event.line(0, Component.text("[§b" + target + "§r]"));
        }else if(line != null && line.matches("\\[[#a-zA-Z0-9_]*")){
            @SuppressWarnings("all")
            String secondLine = event.getLine(1);
            if (secondLine != null && secondLine.matches("[#a-zA-Z0-9_]*]")) {
                event.line(0, Component.text("[§b" + line.substring(1)));
                event.line(1, Component.text("§b" + secondLine.substring(0, secondLine.length() - 1) + "§r]"));
            }
        }
    }
}
