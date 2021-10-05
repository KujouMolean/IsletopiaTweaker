package com.molean.isletopia.menu.settings;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.menu.settings.member.MemberMenu;
import net.kyori.adventure.text.Component;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class SettingsMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private boolean stop = false;
    private boolean open;

    public SettingsMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 9, Component.text("岛屿设置"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public static List<Material> getSpawnEggs() {
        List<Material> spawnEggs = new ArrayList<>();
        for (Material value : Material.values()) {
            if (value.name().contains("SPAWN_EGG")) {
                spawnEggs.add(value);
            }
        }

        return spawnEggs;
    }

    public void open() {
        for (int i = 0; i < 9; i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }

        Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        if (!currentPlot.getOwner().equals(player.getName())) {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                    player.kick(Component.text("错误, 非岛主更改岛屿设置.")));
            return;
        }

        ItemStackSheet visit = new ItemStackSheet(Material.GRASS_BLOCK, "§f= 更改生物群系 =");
        inventory.setItem(0, visit.build());

        Random random = new Random();
        List<Material> spawnEggs = getSpawnEggs();
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), new Consumer<BukkitTask>() {
            @Override
            public void accept(BukkitTask task) {
                if (stop) {
                    task.cancel();
                }
                ItemStack item = inventory.getItem(0);
                if (item != null) {
                    item.setType(spawnEggs.get(random.nextInt(spawnEggs.size())));
                }
            }
        }, 0, 20);

        ItemStackSheet member = new ItemStackSheet(Material.PLAYER_HEAD, "§f< 添加删除成员 >");
        inventory.setItem(2, member.build());

        ItemStackSheet bed = new ItemStackSheet(Material.RED_BED, "§f| 更改复活位置 |");
        inventory.setItem(4, bed.build());

        if (currentPlot.containsFlag("Lock")) {
            ItemStackSheet cancel = new ItemStackSheet(Material.IRON_DOOR, "§f! 点击开放岛屿 !");
            cancel.addLore("§7当前岛屿模式: §c锁定");
            cancel.addLore("§7仅岛屿成员可访问你的岛屿.");
            inventory.setItem(6, cancel.build());
            open = false;
        } else {
            ItemStackSheet denyAll = new ItemStackSheet(Material.OAK_DOOR, "§f! 点击闭关锁岛 !");
            denyAll.addLore("§7当前岛屿模式: §2开放");
            denyAll.addLore("§7任何人都可以访问你的岛屿.");
            inventory.setItem(6, denyAll.build());
            open = true;
        }

        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f<<返回主菜单<<");
        inventory.setItem(8, father.build());
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
        if (slot < 0) {
            return;
        }
        switch (slot) {
            case 0 -> {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new BiomeMenu(player).open());
            }
            case 2 -> {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberMenu(player).open());
            }
            case 4 -> {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    player.performCommand("is setHome");

                });

            }
            case 6 -> {
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    break;
                }
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    if (currentIsland.containsFlag("Lock")) {
                        player.performCommand("is unlock");
                    } else {
                        player.performCommand("is lock");
                    }
                });


            }
            case 8 -> {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerMenu(player).open());
            }
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
