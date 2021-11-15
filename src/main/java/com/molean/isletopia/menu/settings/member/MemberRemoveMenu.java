package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.shared.utils.UUIDUtils;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class MemberRemoveMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> members = new ArrayList<>();
    private final int page;

    private static List<String> getPlayer(Player player) {
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        ArrayList<String> strings = new ArrayList<>();
        for (UUID member : currentPlot.getMembers()) {
            strings.add(UUIDUtils.get(member));
        }
        return strings;
    }

    public MemberRemoveMenu(Player player) {
        this(player, getPlayer(player), 0);
    }

    public MemberRemoveMenu(Player player, List<String> members, int page) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 54, Component.text("撤销某个玩家的权限:"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        this.members.addAll(members);
        this.members.sort(Comparator.comparing(String::toLowerCase));

        if (page > members.size() / 52) {
            page = 0;
        }
        if (members.size() % 52 == 0 && page == members.size() / 52) {
            page = 0;
        }
        this.page = page;

    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }

        for (int i = 0; i + page * 52 < members.size() && i < inventory.getSize() - 2; i++) {
                inventory.setItem(i, HeadUtils.getSkullWithIslandInfo(members.get(i + page * 52)));
        }
        ItemStackSheet next = new ItemStackSheet(Material.LADDER, "§f下一页");
        inventory.setItem(inventory.getSize() - 2, next.build());
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f返回成员");
        inventory.setItem(inventory.getSize() - 1, father.build());

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
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberRemoveMenu(player, members, page + 1).open());
            return;
        }
        if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberMenu(player).open());
            return;
        }

        if (slot < members.size() && slot < 52) {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.performCommand("is distrust " + UUIDUtils.get(members.get(slot + page * 52)));
                player.closeInventory();
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
        event.getHandlers().unregister(this);
    }
}
