package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.settings.SettingsMenu;
import com.molean.isletopia.other.ConfirmDialog;
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

public class MemberMenu implements Listener {

    private final Player player;
    private final Inventory inventory;

    public MemberMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 9, Component.text("<岛屿成员>"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {

        if (!"true".equalsIgnoreCase(UniversalParameter.getParameter(player.getName(), "MemberConfirm"))) {
            new ConfirmDialog(Component.text("添加岛屿成员后，你的岛员将能够随意破坏你的岛屿。" +
                    "请不要随意乱加岛员，如果因为乱给权限导致岛屿被破坏，服务器将不给予任何帮助。\n\n\n\n\n")).accept(player1 -> {
                UniversalParameter.setParameter(player1.getName(), "MemberConfirm", "true");
                MemberMenu.this.open();
            }).open(player);
            return;
        }


        for (int i = 0; i < 9; i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }

        ItemStackSheet add = new ItemStackSheet(Material.TORCH, "§f+ 添加成员 +");
        inventory.setItem(0, add.build());

        ItemStackSheet delete = new ItemStackSheet(Material.LEVER, "§f- 删除成员 -");
        inventory.setItem(2, delete.build());

        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f<<返回设置<<");
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
            case 0:
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberAddMenu(player).open());
                break;
            case 2:
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberRemoveMenu(player).open());
                break;
            case 8:
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new SettingsMenu(player).open());
                break;
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
