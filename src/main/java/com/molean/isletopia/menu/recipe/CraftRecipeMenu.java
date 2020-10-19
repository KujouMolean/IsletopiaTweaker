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

public class CraftRecipeMenu implements Listener {
    private final Player player;
    private final Inventory inventory;
    private final LocalRecipe localRecipe;
    private boolean stop = false;
    private final String fatherCommand;

    public CraftRecipeMenu(Player player, LocalRecipe localRecipe, String fatherCommand) {
        this.fatherCommand = fatherCommand;
        this.player = player;
        this.localRecipe = localRecipe;
        inventory = Bukkit.createInventory(player, 36, I18n.getMessage("menu.recipe.title",player));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, I18n.getMessage("menu.recipe.return",player));
        for (int i = 27; i < 36; i++) {
            inventory.setItem(i, father.build());
        }

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            int cnt = 0;
            while (!stop) {

                inventory.setItem(10, localRecipe.types.get(cnt % localRecipe.types.size()));

                inventory.setItem(3, localRecipe.sources.get(cnt % localRecipe.sources.size())[0]);
                inventory.setItem(4, localRecipe.sources.get(cnt % localRecipe.sources.size())[1]);
                inventory.setItem(5, localRecipe.sources.get(cnt % localRecipe.sources.size())[2]);

                inventory.setItem(12, localRecipe.sources.get(cnt % localRecipe.sources.size())[3]);
                inventory.setItem(13, localRecipe.sources.get(cnt % localRecipe.sources.size())[4]);
                inventory.setItem(14, localRecipe.sources.get(cnt % localRecipe.sources.size())[5]);

                inventory.setItem(21, localRecipe.sources.get(cnt % localRecipe.sources.size())[6]);
                inventory.setItem(22, localRecipe.sources.get(cnt % localRecipe.sources.size())[7]);
                inventory.setItem(23, localRecipe.sources.get(cnt % localRecipe.sources.size())[8]);

                inventory.setItem(16, localRecipe.results.get(cnt % localRecipe.results.size()));


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
        if (slot >= 27) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                new RecipeListMenu(player,fatherCommand).open();
            });
        }


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
