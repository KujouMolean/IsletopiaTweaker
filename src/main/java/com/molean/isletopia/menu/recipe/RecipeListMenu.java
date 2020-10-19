package com.molean.isletopia.menu.recipe;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.infrastructure.individual.I18n;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RecipeListMenu implements Listener {
    private final Player player;
    private final Inventory inventory;
    private boolean stop = false;
    private final String fatherCommand;

    public RecipeListMenu(Player player, String fatherCommand) {
        this.player = player;
        this.fatherCommand = fatherCommand;
        inventory = Bukkit.createInventory(player, 54, I18n.getMessage("menu.recipeList.title",player));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, I18n.getMessage("menu.recipeList.return",player));
        inventory.setItem(inventory.getSize() - 1, father.build());
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            int cnt = 0;
            while (!stop) {
                for (int i = 0; i < LocalRecipe.localRecipeList.size(); i++) {
                    List<ItemStack> icons = LocalRecipe.localRecipeList.get(i).icons;
                    inventory.setItem(i, icons.get(cnt % icons.size()));
                }
                cnt++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
        if (!event.getClick().equals(ClickType.LEFT)) {
            return;
        }
        int slot = event.getSlot();
        if (slot == inventory.getSize() - 1) {
            player.performCommand(fatherCommand);
            return;
        }
        if (slot >= LocalRecipe.localRecipeList.size() || slot < 0) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new CraftRecipeMenu(player, LocalRecipe.localRecipeList.get(slot), fatherCommand).open());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        stop = true;
        event.getHandlers().unregister(this);
    }
}
