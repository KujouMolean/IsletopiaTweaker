package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.infrastructure.individual.MessageUtils;
import com.molean.isletopia.menu.ItemStackSheet;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MemberAddMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> players = new ArrayList<>();
    private final int page;

    private static List<String> getPlayers(Player player) {
        List<String> players = ServerInfoUpdater.getOnlinePlayers();
        players.remove(player.getName());
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        HashSet<UUID> trusted = currentPlot.getTrusted();
        players.removeIf(s -> trusted.contains(ServerInfoUpdater.getUUID(s)));
        return players;
    }

    public MemberAddMenu(Player player) {
        this(player, getPlayers(player), 0);
    }

    public MemberAddMenu(Player player, List<String> players, int page) {
        inventory = Bukkit.createInventory(player, 54, MessageUtils.getMessage("menu.settings.member.add.title"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        this.player = player;
        this.players.addAll(players);
        this.players.sort(Comparator.comparing(String::toLowerCase));
        if (page > players.size() / 52) {
            page = 0;
        }
        if (players.size() % 52 == 0 && page == players.size() / 52) {
            page = 0;
        }
        this.page = page;

    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }

        for (int i = 0; i + page * 52 < players.size() && i < inventory.getSize() - 2; i++) {
            inventory.setItem(i, HeadUtils.getSkull(players.get(i + page * 52)));
        }
        ItemStackSheet next = new ItemStackSheet(Material.LADDER, MessageUtils.getMessage("menu.visit.nextPage"));
        inventory.setItem(inventory.getSize() - 2, next.build());
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage("menu.settings.member.add.return"));
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
            ItemStack item = inventory.getItem(slot);
            assert item != null;
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(MessageUtils.getMessage("menu.wait"));
            item.setItemMeta(itemMeta);
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberAddMenu(player, players, page + 1).open());
            return;
        }
        if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberMenu(player).open());
            return;
        }
        if (slot < players.size() && slot < 52) {
            Plot currentPlot = PlotUtils.getCurrentPlot(player);
            if (currentPlot.getOwner().equals(player.getUniqueId())) {
                currentPlot.addTrusted(ServerInfoUpdater.getUUID(players.get(slot + page * 52)));
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberAddMenu(player).open());
            } else {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    player.kickPlayer(MessageUtils.getMessage("error.menu.settings.member.non-owner"));
                });
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
        event.getHandlers().unregister(this);
    }
}
