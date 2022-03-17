package com.molean.isletopia.menu.charge;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.charge.ChargeDetailCommitter;
import com.molean.isletopia.charge.ChargeDetailUtils;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.shared.model.ChargeDetail;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class PlayerChargeMenu extends ChestMenu {
    private BukkitTask bukkitTask;
    private final LocalIsland island;

    public PlayerChargeMenu(Player player) {
        super(player, 3, Component.text(MessageUtils.getMessage(player, "menu.charge.title")));
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        island = currentPlot;

        ItemStackSheet introduction = ItemStackSheet.fromString(Material.BOOK, MessageUtils.getMessage(player, "menu.charge.info"));
        ItemStackSheet father = ItemStackSheet.fromString(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main"));
        this.item(10, introduction.build())
                .item(26, father.build(), () -> new MainMenu(player).open());
    }


    public void updateBill() {
        ChargeDetail chargeDetail = ChargeDetailCommitter.get(island.getIslandId());
        ItemStackSheet billThisWeek = fromPlayerChargeDetail(chargeDetail, Material.PAPER, MessageUtils.getMessage(player, "menu.charge.bill.title"));
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


    public ItemStackSheet fromPlayerChargeDetail(ChargeDetail chargeDetail, Material material, String display) {
        long total = ChargeDetailUtils.getTotalPowerUsage(chargeDetail);
        long dispenser = ChargeDetailUtils.getDispenserPowerUsage(chargeDetail);
        long initial = ChargeDetailUtils.POWER_INITIAL;
        long piston = ChargeDetailUtils.getPistonPowerUsage(chargeDetail);
        long hopper = ChargeDetailUtils.getHopperPowerUsage(chargeDetail);
        long vehicle = ChargeDetailUtils.getVehiclePowerUsage(chargeDetail);
        long furnace = ChargeDetailUtils.getFurnacePowerUsage(chargeDetail);
        long tnt = ChargeDetailUtils.getTntPowerUsage(chargeDetail);
        long redstone = ChargeDetailUtils.getRedstonePowerUsage(chargeDetail);
        long water = ChargeDetailUtils.getTotalWaterUsage(chargeDetail);
        long powerPerMinutes = chargeDetail.getOnlineMinutes() * ChargeDetailUtils.POWER_PER_ONLINE;
        long powerLeft = ChargeDetailUtils.getLeftPower(chargeDetail);

        String message = MessageUtils.getMessage(player, "menu.charge.detail",
                Pair.of("display", display),
                Pair.of("total", (powerPerMinutes + initial) + ""),
                Pair.of("consume", total + ""),
                Pair.of("left", (powerLeft) + ""),
                Pair.of("piston", (piston) + ""),
                Pair.of("dispenser", (dispenser) + ""),
                Pair.of("furnace", (furnace) + ""),
                Pair.of("water", (water) + ""),
                Pair.of("hopper", (hopper) + ""),
                Pair.of("tnt", (tnt) + ""),
                Pair.of("vehicle", (vehicle) + ""),
                Pair.of("redstone", (redstone) + ""),
                Pair.of("piston_price", (1.0 / ChargeDetailUtils.PISTON_TIMES) + ""),
                Pair.of("dispenser_price", (1.0 / ChargeDetailUtils.DISPENSER_TIMES) + ""),
                Pair.of("furnace_price", (1.0 / ChargeDetailUtils.FURNACE_TIMES) + ""),
                Pair.of("water_price", (1.0 / ChargeDetailUtils.WATER_TIMES) + ""),
                Pair.of("hopper_price", (200.0 / ChargeDetailUtils.HOPPER_TIMES) + ""),
                Pair.of("tnt_price", (1.0 / ChargeDetailUtils.TNT_TIMES) + ""),
                Pair.of("vehicle_price", (1.0 / ChargeDetailUtils.VEHICLE_TIMES) + ""),
                Pair.of("redstone_price", (1.0 / ChargeDetailUtils.REDSTONE_TIMES) + "")
        );
        return ItemStackSheet.fromString(material, message);
    }

}
