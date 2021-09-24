package com.molean.isletopia.menu;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.IslandDao;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.MessageUtils;
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
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VisitorMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private List<Pair<String, Timestamp>> pairs;
    private int page;

    public VisitorMenu(Player player, int page) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 54, Component.text("最近三天的访客记录(第" + page + "页)"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        this.page = page;
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }


        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.hasPermission(player) || player.isOp())) {
            MessageUtils.fail(player, "你只能查看自己岛屿的访客记录!");
            return;
        }

        try {
            pairs = IslandDao.queryVisit(currentIsland.getId(), 3);
        } catch (SQLException e) {
            e.printStackTrace();
            MessageUtils.fail(player, "发生错误，请联系管理员!");
            return;
        }

        if (page > pairs.size() / 52) {
            page = 0;
        }

        for (int i = 0; page * 52 + i < pairs.size() && i < inventory.getSize() - 2; i++) {
            ItemStack skullWithIslandInfo = HeadUtils.getSkullWithIslandInfo(pairs.get(page * 52 + i).getKey());
            ArrayList<Component> components = new ArrayList<>();
            if (skullWithIslandInfo.lore() != null) {
                components.addAll(skullWithIslandInfo.lore());
            }
            Timestamp value = pairs.get(page * 52 + i).getValue();
            String format = value.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            components.add(Component.text("访问时间: " + format));
            skullWithIslandInfo.lore(components);
            inventory.setItem(i, skullWithIslandInfo);
        }
        ItemStackSheet next = new ItemStackSheet(Material.LADDER, "§f下一页");
        inventory.setItem(inventory.getSize() - 2, next.build());
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f关闭菜单");
        inventory.setItem(inventory.getSize() - 1, father.build());

        //here place icon
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
        if (slot == inventory.getSize() - 2) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new VisitorMenu(player, page + 1).open());
            return;
        }
        if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), (@NotNull Runnable) player::closeInventory);
            return;
        }
        if (slot < 52 && page * 52 + slot < pairs.size()) {
            player.performCommand("visit " + pairs.get(page * 52 + slot).getKey());
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
        event.getHandlers().unregister(this);
    }
}
