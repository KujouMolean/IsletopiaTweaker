package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.favorite.FavoriteMenu;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
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
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class MemberAddMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> players = new ArrayList<>();

    public MemberAddMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 54, "选择你想授权的玩家");
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        players.addAll(ServerInfoUpdater.getOnlinePlayers());
        players.remove(player.getName());
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if(!currentPlot.getOwner().equals(player.getUniqueId())){
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.kickPlayer("错误, 非岛主操作岛屿成员.");
            });
            return;
        }
        HashSet<UUID> trusted = currentPlot.getTrusted();
        players.removeIf(s -> trusted.contains(ServerInfoUpdater.getUUID(s)));

        for (int i = 0; i < players.size() && i < inventory.getSize() - 1; i++) {
            inventory.setItem(i, HeadUtils.getSkull(players.get(i)));
        }

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
        if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberMenu(player).open());
            return;
        }
        if (slot >= players.size()) {
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (currentPlot.getOwner().equals(player.getUniqueId())) {
            currentPlot.addTrusted(ServerInfoUpdater.getUUID(players.get(slot)));
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberAddMenu(player).open());
        } else {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.kickPlayer("错误, 非岛主操作岛屿成员.");
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
