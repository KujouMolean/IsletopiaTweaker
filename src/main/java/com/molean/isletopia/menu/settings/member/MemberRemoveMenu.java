package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.bungee.individual.ServerInfoUpdater;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
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

import java.util.*;

public class MemberRemoveMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> members = new ArrayList<>();
    private final int page;

    private static List<String> getPlayer(Player player) {
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        assert currentPlot != null;
        HashSet<UUID> trusted = currentPlot.getTrusted();
        List<String> members = new ArrayList<>();
        for (UUID uuid : trusted) {
            String single = PlotSquared.get().getImpromptuUUIDPipeline().getSingle(uuid, 50000);
            members.add(single);
        }
        return members;
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
            inventory.setItem(i, HeadUtils.getSkull(members.get(i + page * 52)));
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
            Plot currentPlot = PlotUtils.getCurrentPlot(player);
            assert currentPlot != null;
            if (currentPlot.getOwner().equals(player.getUniqueId())) {
                currentPlot.removeTrusted(ServerInfoUpdater.getUUID(members.get(slot + page * 52)));
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberRemoveMenu(player).open());
            } else {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                        player.kick(Component.text("错误, 非岛主操作岛屿成员.")));
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
