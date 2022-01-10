package com.molean.isletopia.menu.charge;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.charge.ChargeDetail;
import com.molean.isletopia.charge.ChargeDetailCommitter;
import com.molean.isletopia.charge.ChargeDetailUtils;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.utils.InventoryUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PlayerChargeMenu extends ChestMenu {
    private BukkitTask bukkitTask;
    private final LocalIsland island;

    public PlayerChargeMenu(Player player) {
        super(player, 3, Component.text("§f水电系统"));
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        island = currentPlot;

        ItemStackSheet introduction = ItemStackSheet.fromString(Material.BOOK, """
                §d§l水电系统说明
                有关此收费系统的详细标准详见梦幻之屿Wiki
                                
                §3§l简要说明:
                §b - §7每周水电数据完全重置，赠送100,000水和电
                §b - §7岛屿上有玩家在线时，每分钟赠送50度电和50吨水
                §b - §7在岛上放置信标/潮涌核心可额外赠送25水或电
                §b - §7采用梯度用电用水,每次购买后单价加1钻石
                §b - §7闲时水电全免, 每日0~8点不计水电费
                               
                §3§l注意事项:
                §b - §7该系统不会影响正常红石玩家发展，放心食用
                §b - §7用于购买水电的等价物可能会更换，不会通知
                §b - §7超大规模红石岛屿请确保水电足够，以防装置损坏
                """);
        ItemStackSheet father = ItemStackSheet.fromString(Material.BARRIER, "§f返回主菜单");

        this
                .item(10, introduction.build())
                .item(26, father.build(), () -> new PlayerMenu(player).open());
    }


    public void updateBill() {
        ChargeDetail chargeDetail = ChargeDetailCommitter.get(island.getIslandId());
        ItemStackSheet billThisWeek = fromPlayerChargeDetail(chargeDetail, Material.PAPER, "§f本周费用(" + UUIDUtils.get(island.getUuid()) + ")");
        item(13, billThisWeek.build());
    }

    @Override
    public void beforeOpen() {
        super.beforeOpen();
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(),
                this::updateBill, 0, 20);
    }

    @Override
    public void afterClose() {
        super.afterClose();
        bukkitTask.cancel();

    }


    public static ItemStackSheet fromPlayerChargeDetail(ChargeDetail chargeDetail, Material material, String display) {
        long total = ChargeDetailUtils.getTotalPowerUsage(chargeDetail);
        long dispenser = ChargeDetailUtils.getDispenserPowerUsage(chargeDetail);
        long piston = ChargeDetailUtils.getPistonPowerUsage(chargeDetail);
        long hopper = ChargeDetailUtils.getHopperPowerUsage(chargeDetail);
        long vehicle = ChargeDetailUtils.getVehiclePowerUsage(chargeDetail);
        long furnace = ChargeDetailUtils.getFurnacePowerUsage(chargeDetail);
        long tnt = ChargeDetailUtils.getTntPowerUsage(chargeDetail);
        long redstone = ChargeDetailUtils.getRedstonePowerUsage(chargeDetail);
        long water = ChargeDetailUtils.getTotalWaterUsage(chargeDetail);
        long powerProduce = chargeDetail.getPowerProduceTimes() * ChargeDetailUtils.POWER_PER_PRODUCE;
        long waterProduce = chargeDetail.getWaterProduceTimes() * ChargeDetailUtils.WATER_PER_PRODUCE;
        long powerPerMinutes = chargeDetail.getOnlineMinutes() * ChargeDetailUtils.POWER_PER_ONLINE;
        long waterPerMinutes = chargeDetail.getOnlineMinutes() * ChargeDetailUtils.WATER_PER_ONLINE;
        long powerBuy = chargeDetail.getPowerChargeTimes() * ChargeDetailUtils.POWER_PER_BUY;
        long powerCostNextBuy = (chargeDetail.getPowerChargeTimes() + 1);
        long waterCostNextBuy = (chargeDetail.getWaterChargeTimes() + 1);
        long waterBuy = chargeDetail.getWaterChargeTimes() * ChargeDetailUtils.WATER_PER_BUY;
        long powerLeft = ChargeDetailUtils.getLeftPower(chargeDetail);
        long waterLeft = ChargeDetailUtils.getLeftWater(chargeDetail);
        long powerCost = ChargeDetailUtils.getPowerCost(chargeDetail);
        long waterCost = ChargeDetailUtils.getWaterCost(chargeDetail);
        ItemStackSheet bills = ItemStackSheet.fromString(material, """
                %s
                §c总用电量: %d度
                §7  发射器: %d度  §7  活塞: %d度  §7  漏斗: %d度
                §7  矿车: %d度  §7  熔炉: %d度  §7  tnt: %d度
                §7  红石: %d度
                §c总用水量: %d度
                
                §d激活信标发电量: %d度
                §b潮涌核心净水量: %d吨
                §d在线赠送电量: %d度
                §b在线赠送水量: %d吨
                §d购买电量(左键): %d度(下次花费:%d)
                §b购买水量(右键): %d吨(下次花费:%d)
                §d合计剩余电量: %d度
                §b合计剩余水量: %d吨
                §d需要交纳电费: %d度
                §b需要交纳水费: %d吨
                """.formatted(display,total,dispenser,piston,hopper,vehicle,furnace,tnt,redstone,water,
                powerProduce,waterProduce,powerPerMinutes,waterPerMinutes,powerBuy,powerCostNextBuy,waterBuy,waterCostNextBuy,
                powerLeft,waterLeft,powerCost,waterCost));

        return bills;
    }



    @Override
    public void onLeftClick(int slot) {
        super.onLeftClick(slot);
        if (slot == 13) {
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(island.getIslandId());
            if (InventoryUtils.takeItem(player, Material.DIAMOND, chargeDetail.getPowerChargeTimes() + 1)) {
                chargeDetail.setPowerChargeTimes(chargeDetail.getPowerChargeTimes() + 1);
            } else {
                player.closeInventory();
                player.sendMessage("§c你的背包中没有足够的钻石!");
            }
            updateBill();
        } else if (slot == 26) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerMenu(player).open());
            updateBill();
        }
    }

    @Override
    public void onRightClick(int slot) {
        super.onRightClick(slot);
        if (slot == 13) {
            ChargeDetail chargeDetail = ChargeDetailCommitter.get(island.getIslandId());
            if (InventoryUtils.takeItem(player, Material.DIAMOND, chargeDetail.getWaterChargeTimes() + 1)) {

                chargeDetail.setWaterChargeTimes(chargeDetail.getWaterChargeTimes() + 1);
            } else {
                player.closeInventory();
                player.sendMessage("§c你的背包中没有足够的钻石!");
            }
        }
        updateBill();

    }


}
