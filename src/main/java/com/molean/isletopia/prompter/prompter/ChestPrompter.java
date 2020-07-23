package com.molean.isletopia.prompter.prompter;

import com.molean.isletopia.prompter.util.Pair;
import com.molean.isletopia.prompter.util.PrompterUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChestPrompter implements Prompter {


    protected Player player;
    protected List<Pair<ItemStack, String>> itemStacks;
    protected Inventory inventory;
    protected Consumer<String> consumer;
    protected Runnable runnable;
    protected int page = 0;

    public ChestPrompter(Player player, String title) {
        this.player = player;
        this.itemStacks = new ArrayList<>();
        inventory = Bukkit.createInventory(player, 54, title);
        PrompterUtils.getChestPrompterList().add(this);
    }

    public List<Pair<ItemStack, String>> getItemStacks() {
        return itemStacks;
    }

    public void setItemStacks(List<Pair<ItemStack, String>> itemStacks) {
        this.itemStacks = itemStacks;
    }

    public void addItemStacks(Pair<ItemStack, String> itemStack) {
        itemStacks.add(itemStack);
    }

    public void freshPage() {
        inventory.clear();
        for (int i = 0; i < itemStacks.size(); i++) {
            if (i == 45) break;
            inventory.setItem(i, itemStacks.get(page * 45 + i).getKey());
        }
        //prev page button
        ItemStack prev = new ItemStack(Material.FEATHER);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.setDisplayName("§f<=");
        prev.setItemMeta(prevMeta);
        inventory.setItem(9 * 5 + 2, prev);

        //next page button
        ItemStack next = new ItemStack(Material.FEATHER);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName("§f=>");
        next.setItemMeta(nextMeta);
        inventory.setItem(9 * 5 + 6, next);
    }

    @Override
    public void open() {
        player.openInventory(inventory);
        freshPage();
    }

    @Override
    public void handleInventoryCloseEvent(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory))
            return;
        PrompterUtils.getChestPrompterList().remove(this);
        if (runnable != null) {
            runnable.run();
        }

    }

    @Override
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory))
            return;
        if (event.getClick() == ClickType.LEFT) {
            if (event.getCurrentItem() != null) {
                if (event.getCurrentItem().getType() != Material.FEATHER) {
                    int slot = event.getSlot();
                    consumer.accept(itemStacks.get(page * 45 + slot).getValue());
                    player.closeInventory();
                    PrompterUtils.getChestPrompterList().remove(this);
                    return;
                } else {
                    String name = event.getCurrentItem().getItemMeta().getDisplayName();
                    if (name.equals("=>")) {
                        nextPage();
                    } else if (name.equals("<=")) {
                        prevPage();
                    }
                    event.setCancelled(true);
                    player.updateInventory();
                    return;
                }
            }
        }
        event.setCancelled(true);
        player.updateInventory();

    }

    protected void nextPage() {
        int maxPage = itemStacks.size() / 45;
        if (itemStacks.size() % 45 != 0) maxPage++;
        if (page < maxPage) page++;
        freshPage();
    }

    protected void prevPage() {
        if (page > 0) page--;
        freshPage();
    }

    @Override
    public void onComplete(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onEscape(Runnable runnable) {
        this.runnable = runnable;
    }
}