package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.infrastructure.individual.I18n;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.PlotSquared;
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

public class MemberRemoveMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> members = new ArrayList<>();

    public MemberRemoveMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 54, I18n.getMessage("menu.settings.member.remove.title",player));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (!currentPlot.getOwner().equals(player.getUniqueId())) {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.kickPlayer(I18n.getMessage("error.menu.settings.member.non-owner",player));
            });
            return;
        }
        HashSet<UUID> trusted = currentPlot.getTrusted();
        for (UUID uuid : trusted) {
            String single = PlotSquared.get().getImpromptuUUIDPipeline().getSingle(uuid, 50000);
            members.add(single);
        }

        for (int i = 0; i < members.size() && i < inventory.getSize() - 1; i++) {
            inventory.setItem(i, HeadUtils.getSkull(members.get(i)));
        }
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, I18n.getMessage("menu.settings.member.remove.return",player));
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
        if (slot >= members.size()) {
            return;
        }
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (currentPlot.getOwner().equals(player.getUniqueId())) {
            currentPlot.removeTrusted(ServerInfoUpdater.getUUID(members.get(slot)));
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberRemoveMenu(player).open());
        } else {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.kickPlayer(I18n.getMessage("error.menu.settings.member.non-owner",player));
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
