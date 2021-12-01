package com.molean.isletopia.menu.charge;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.charge.ChargeDetail;
import com.molean.isletopia.charge.ChargeDetailCommitter;
import com.molean.isletopia.charge.ChargeDetailUtils;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.shared.utils.UUIDUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PlayerChargeMenu implements Listener {
    private final Player player;
    private final Inventory inventory;
    private final UUID owner;
    private BukkitTask bukkitTask;

    public PlayerChargeMenu(Player player) {
        this.player = player;
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        owner = currentPlot.getUuid();
        inventory = Bukkit.createInventory(player, 27, Component.text("§f水电系统"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public static ItemStackSheet fromPlayerChargeDetail(ChargeDetail chargeDetail, Material material, String display) {
        ItemStackSheet bills = new ItemStackSheet(material, display);
        bills.addLore("§c总用电量: " + ChargeDetailUtils.getTotalPowerUsage(chargeDetail) + "度");
        bills.addLore("§7  发射器: " + ChargeDetailUtils.getDispenserPowerUsage(chargeDetail) + "度  " +
                "§7  活塞: " + ChargeDetailUtils.getPistonPowerUsage(chargeDetail) + "度  " +
                "§7  漏斗: " + ChargeDetailUtils.getHopperPowerUsage(chargeDetail) + "度  ");
        bills.addLore("§7  矿车: " + ChargeDetailUtils.getVehiclePowerUsage(chargeDetail) + "度  " +
                "§7  熔炉: " + ChargeDetailUtils.getFurnacePowerUsage(chargeDetail) + "度  " +
                "§7  tnt: " + ChargeDetailUtils.getTntPowerUsage(chargeDetail) + "度  ");
        bills.addLore("§7  红石: " + ChargeDetailUtils.getRedstonePowerUsage(chargeDetail) + "度  ");
        bills.addLore("§c总用水量: " + ChargeDetailUtils.getTotalWaterUsage(chargeDetail) + "吨");
        bills.addLore("");
        bills.addLore("§d激活信标发电量: " + chargeDetail.getPowerProduceTimes() * ChargeDetailUtils.POWER_PER_PRODUCE + "度");
        bills.addLore("§b潮涌核心净水量: " + chargeDetail.getWaterProduceTimes() * ChargeDetailUtils.WATER_PER_PRODUCE + "吨");
        bills.addLore("§d在线赠送电量: " + chargeDetail.getOnlineMinutes() * ChargeDetailUtils.POWER_PER_ONLINE + "度");
        bills.addLore("§b在线赠送水量: " + chargeDetail.getOnlineMinutes() * ChargeDetailUtils.WATER_PER_ONLINE + "吨");
        bills.addLore("§d购买电量(左键): " + chargeDetail.getPowerChargeTimes() * ChargeDetailUtils.POWER_PER_BUY + "度(下次花费:" + (chargeDetail.getPowerChargeTimes() + 1) + "钻石)");
        bills.addLore("§b购买水量(右键): " + chargeDetail.getWaterChargeTimes() * ChargeDetailUtils.WATER_PER_BUY + "吨(下次花费:" + (chargeDetail.getWaterChargeTimes() + 1) + "钻石)");
        bills.addLore("");
        bills.addLore("§d合计剩余电量: " + ChargeDetailUtils.getLeftPower(chargeDetail) + "度");
        bills.addLore("§b合计剩余水量: " + ChargeDetailUtils.getLeftWater(chargeDetail) + "吨");
        bills.addLore("");
        bills.addLore("§d需要交纳电费: " + ChargeDetailUtils.getPowerCost(chargeDetail) + "钻石");
        bills.addLore("§b需要交纳水费: " + ChargeDetailUtils.getWaterCost(chargeDetail) + "钻石");
        bills.addLore("");
        return bills;
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        ItemStackSheet introduction = new ItemStackSheet(Material.BOOK);
        introduction.setDisplay("§d§l水电系统说明");
        introduction.addLore("有关此收费系统的详细标准详见梦幻之屿Wiki");
        introduction.addLore("");
        introduction.addLore("§3§l简要说明:");
        introduction.addLore("§b - §7每周水电数据完全重置，赠送100,000水和电");
        introduction.addLore("§b - §7岛屿上有玩家在线时，每分钟赠送50度电和50吨水");
        introduction.addLore("§b - §7在岛上放置信标/潮涌核心可额外赠送25水或电");
        introduction.addLore("§b - §7采用梯度用电用水,每次购买后单价加1钻石");
        introduction.addLore("§b - §7闲时水电全免, 每日0~8点不计水电费");
        introduction.addLore("");
        introduction.addLore("§3§l注意事项:");
        introduction.addLore("§b - §7该系统不会影响正常红石玩家发展，放心食用");
        introduction.addLore("§b - §7用于购买水电的等价物可能会更换，不会通知");
        introduction.addLore("§b - §7超大规模红石岛屿请确保水电足够，以防装置损坏");
        inventory.setItem(10, introduction.build());

        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(owner);
            ItemStackSheet billThisWeek = fromPlayerChargeDetail(chargeDetail, Material.PAPER, "§f本周费用(" + UUIDUtils.get(owner) + ")");
            inventory.setItem(13, billThisWeek.build());

        }, 0, 20);

        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f返回主菜单");
        inventory.setItem(26, father.build());
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory));
    }


    public static boolean takeItem(Player player, Material material, int amount) {
        int total = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType().equals(material)) {
                total += content.getAmount();
            }
        }
        if (total < amount) {
            return false;
        }

        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType().equals(material)) {
                if (content.getAmount() > amount) {
                    content.setAmount(content.getAmount() - amount);
                    return true;
                } else {
                    amount -= content.getAmount();
                    content.setAmount(0);
                }
            }
        }
        return true;

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
        int slot = event.getSlot();
        switch (event.getClick()) {
            case LEFT -> {
                if (slot == 13) {
                    ChargeDetail chargeDetail = ChargeDetailCommitter.get(owner);
                    if (takeItem(player, Material.DIAMOND, chargeDetail.getPowerChargeTimes() + 1)) {
                        chargeDetail.setPowerChargeTimes(chargeDetail.getPowerChargeTimes() + 1);
                    } else {
                        player.closeInventory();
                        player.sendMessage("§c你的背包中没有足够的钻石!");
                    }
                } else if (slot == 26) {
                    Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerMenu(player).open());
                }
            }
            case RIGHT -> {
                if (slot == 13) {
                    ChargeDetail chargeDetail = ChargeDetailCommitter.get(owner);
                    if (takeItem(player, Material.DIAMOND, chargeDetail.getWaterChargeTimes() + 1)) {

                        chargeDetail.setWaterChargeTimes(chargeDetail.getWaterChargeTimes() + 1);
                    } else {
                        player.closeInventory();
                        player.sendMessage("§c你的背包中没有足够的钻石!");
                    }
                }
            }
        }

        ChargeDetail chargeDetail = ChargeDetailCommitter.get(owner);
        ItemStackSheet billThisWeek = fromPlayerChargeDetail(chargeDetail, Material.PAPER, "§f本周费用(" + UUIDUtils.get(owner) + ")");

        inventory.setItem(13, billThisWeek.build());
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
        bukkitTask.cancel();
        event.getHandlers().unregister(this);
    }
}
