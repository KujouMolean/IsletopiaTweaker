package com.molean.isletopia.menu.charge;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.charge.PlayerChargeDetail;
import com.molean.isletopia.charge.PlayerChargeDetailCommitter;
import com.molean.isletopia.charge.PlayerChargeDetailUtils;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.PlayerMenu;
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

public class PlayerChargeMenu implements Listener {
    private final Player player;
    private final Inventory inventory;
    private final String ownerName;
    private boolean hasLastWeek;
    private BukkitTask bukkitTask;

    public PlayerChargeMenu(Player player) {
        this.player = player;
        Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        ownerName = currentPlot.getOwner();
        inventory = Bukkit.createInventory(player, 27, Component.text("§f水电系统"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public static ItemStackSheet fromPlayerChargeDetail(PlayerChargeDetail playerChargeDetail, Material material, String display) {
        ItemStackSheet bills = new ItemStackSheet(material, display);
        bills.addLore("§c总用电量: " + PlayerChargeDetailUtils.getTotalPowerUsage(playerChargeDetail) + "度");
        bills.addLore("§7  发射器: " + PlayerChargeDetailUtils.getDispenserPowerUsage(playerChargeDetail) + "度  " +
                "§7  活塞: " + PlayerChargeDetailUtils.getPistonPowerUsage(playerChargeDetail) + "度  " +
                "§7  漏斗: " + PlayerChargeDetailUtils.getHopperPowerUsage(playerChargeDetail) + "度  ");
        bills.addLore("§7  矿车: " + PlayerChargeDetailUtils.getVehiclePowerUsage(playerChargeDetail) + "度  " +
                "§7  熔炉: " + PlayerChargeDetailUtils.getFurnacePowerUsage(playerChargeDetail) + "度  " +
                "§7  tnt: " + PlayerChargeDetailUtils.getTntPowerUsage(playerChargeDetail) + "度  ");
        bills.addLore("§7  红石: " + PlayerChargeDetailUtils.getRedstonePowerUsage(playerChargeDetail) + "度  ");
        bills.addLore("§c总用水量: " + PlayerChargeDetailUtils.getTotalWaterUsage(playerChargeDetail) + "吨");
        bills.addLore("");
        bills.addLore("§d激活信标发电量: " + playerChargeDetail.getPowerProduceTimes() * PlayerChargeDetailUtils.POWER_PER_PRODUCE + "度");
        bills.addLore("§b潮涌核心净水量: " + playerChargeDetail.getWaterProduceTimes() * PlayerChargeDetailUtils.WATER_PER_PRODUCE + "吨");
        bills.addLore("§d在线赠送电量: " + playerChargeDetail.getOnlineMinutes() * PlayerChargeDetailUtils.POWER_PER_ONLINE + "度");
        bills.addLore("§b在线赠送水量: " + playerChargeDetail.getOnlineMinutes() * PlayerChargeDetailUtils.WATER_PER_ONLINE + "吨");
        bills.addLore("§d购买电量(左键): " + playerChargeDetail.getPowerChargeTimes() * PlayerChargeDetailUtils.POWER_PER_BUY + "度(下次花费:" + (playerChargeDetail.getPowerChargeTimes() + 1) + "钻石)");
        bills.addLore("§b购买水量(右键): " + playerChargeDetail.getWaterChargeTimes() * PlayerChargeDetailUtils.WATER_PER_BUY + "吨(下次花费:" + (playerChargeDetail.getWaterChargeTimes() + 1) + "钻石)");
        bills.addLore("");
        bills.addLore("§d合计剩余电量: " + PlayerChargeDetailUtils.getLeftPower(playerChargeDetail) + "度");
        bills.addLore("§b合计剩余水量: " + PlayerChargeDetailUtils.getLeftWater(playerChargeDetail) + "吨");
        bills.addLore("");
        bills.addLore("§d需要交纳电费: " + PlayerChargeDetailUtils.getPowerCost(playerChargeDetail) + "钻石");
        bills.addLore("§b需要交纳水费: " + PlayerChargeDetailUtils.getWaterCost(playerChargeDetail) + "钻石");
        bills.addLore("");
        return bills;
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        ItemStackSheet introduction = new ItemStackSheet(Material.BOOK);
        introduction.setDisplay("§f水电系统说明");
        introduction.addLore("§c前排提示: 目前系统还在测试中,无需缴费,不会停电停水");
        introduction.addLore("");
        introduction.addLore("§f1.水电费每周周日结算,未缴纳水电费的岛屿次周将停水停电");
        introduction.addLore("§f  如无特殊情况,请在周日前交纳,以防造成机器损坏");
        introduction.addLore("§f2.有玩家在岛屿上时每分钟赠送50度电和50吨水");
        introduction.addLore("§f  多个玩家在同一岛屿上只送一次");
        introduction.addLore("§f3.在岛上放置信标/潮涌核心可额外赠送水电");
        introduction.addLore("§f  信标25度电/个,潮涌核心25吨水/个");
        introduction.addLore("§f4.采用梯度用电用水,每次购买后单价加1钻石");
        introduction.addLore("§f  每次购买可以获得10000度电");
        introduction.addLore("§f5.历史账单不追究,如果超过一周未缴纳");
        introduction.addLore("§f  则账单作废,可以不用再交纳欠下的水电费");
        introduction.addLore("§f6.闲时用电免费, 每日0~8点不计水电费");
        introduction.addLore("§f  如有大功耗机器,请夜间使用");
        inventory.setItem(10, introduction.build());

        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(ownerName);
            ItemStackSheet billThisWeek = fromPlayerChargeDetail(playerChargeDetail, Material.PAPER, "§f本周费用(" + ownerName + ")");
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
                    PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(ownerName);
                    if (takeItem(player, Material.DIAMOND, playerChargeDetail.getPowerChargeTimes() + 1)) {
                        playerChargeDetail.setPowerChargeTimes(playerChargeDetail.getPowerChargeTimes() + 1);
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
                    PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(ownerName);
                    if (takeItem(player, Material.DIAMOND, playerChargeDetail.getWaterChargeTimes() + 1)) {

                        playerChargeDetail.setWaterChargeTimes(playerChargeDetail.getWaterChargeTimes() + 1);
                    } else {
                        player.closeInventory();
                        player.sendMessage("§c你的背包中没有足够的钻石!");
                    }
                }
            }
        }

        PlayerChargeDetail playerChargeDetail = PlayerChargeDetailCommitter.get(ownerName);
        ItemStackSheet billThisWeek = fromPlayerChargeDetail(playerChargeDetail, Material.PAPER, "§f本周费用(" + ownerName + ")");
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
