package com.molean.isletopia.prompter;

import com.molean.isletopia.prompter.util.PrompterUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        for (int i = 0; i < PrompterUtils.getChestPrompterList().size(); i++) {
            PrompterUtils.getChestPrompterList().get(i).handleInventoryCloseEvent(event);

        }
    }
}
