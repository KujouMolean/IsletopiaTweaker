package com.molean.isletopia.prompter.listener;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.prompter.IsletopiaPrompters;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class PromterInventoryHandler implements Listener {
    public PromterInventoryHandler() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        for (int i = 0; i < IsletopiaPrompters.getChestPrompterList().size(); i++) {
            IsletopiaPrompters.getChestPrompterList().get(i).handleInventoryCloseEvent(event);

        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        for (int i = 0; i < IsletopiaPrompters.getChestPrompterList().size(); i++) {
            IsletopiaPrompters.getChestPrompterList().get(i).handleInventoryClickEvent(event);
        }
    }
}
