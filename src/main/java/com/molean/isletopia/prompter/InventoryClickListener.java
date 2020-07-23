package com.molean.isletopia.prompter;

import com.molean.isletopia.prompter.util.PrompterUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        for (int i = 0; i < PrompterUtils.getChestPrompterList().size(); i++) {
            PrompterUtils.getChestPrompterList().get(i).handleInventoryClickEvent(event);
        }
    }
}
