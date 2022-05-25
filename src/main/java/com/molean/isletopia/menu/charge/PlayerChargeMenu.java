package com.molean.isletopia.menu.charge;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.charge.ChargeUtils;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
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

    private final ChargeCommitter chargeCommitter;

    public PlayerChargeMenu(ChargeCommitter chargeCommitter, PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, Player player) {

        super(player, 3, Component.text(MessageUtils.getMessage(player, "menu.charge.title")));
        this.chargeCommitter = chargeCommitter;
        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        island = currentPlot;

        ItemStackSheet introduction = ItemStackSheet.fromString(Material.BOOK, MessageUtils.getMessage(player, "menu.charge.info"));
        ItemStackSheet father = ItemStackSheet.fromString(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main"));
        this.item(10, introduction.build())
                .itemWithAsyncClickEvent(26, father.build(), () -> new MainMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open());

    }


    public void updateBill() {
        ChargeDetail chargeDetail = chargeCommitter.get(island.getIslandId());
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
        long total = ChargeUtils.getTotalPowerUsage(chargeDetail);
        long dispenser = ChargeUtils.getDispenserPowerUsage(chargeDetail);
        long initial = ChargeUtils.POWER_INITIAL;
        long piston = ChargeUtils.getPistonPowerUsage(chargeDetail);
        long hopper = ChargeUtils.getHopperPowerUsage(chargeDetail);
        long vehicle = ChargeUtils.getVehiclePowerUsage(chargeDetail);
        long furnace = ChargeUtils.getFurnacePowerUsage(chargeDetail);
        long tnt = ChargeUtils.getTntPowerUsage(chargeDetail);
        long redstone = ChargeUtils.getRedstonePowerUsage(chargeDetail);
        long water = ChargeUtils.getTotalWaterUsage(chargeDetail);
        long powerPerMinutes = chargeDetail.getOnlineMinutes() * ChargeUtils.POWER_PER_ONLINE;
        long powerLeft = ChargeUtils.getLeftPower(chargeDetail);

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
                Pair.of("piston_price", (1.0 / ChargeUtils.PISTON_TIMES) + ""),
                Pair.of("dispenser_price", (1.0 / ChargeUtils.DISPENSER_TIMES) + ""),
                Pair.of("furnace_price", (1.0 / ChargeUtils.FURNACE_TIMES) + ""),
                Pair.of("water_price", (1.0 / ChargeUtils.WATER_TIMES) + ""),
                Pair.of("hopper_price", (200.0 / ChargeUtils.HOPPER_TIMES) + ""),
                Pair.of("tnt_price", (1.0 / ChargeUtils.TNT_TIMES) + ""),
                Pair.of("vehicle_price", (1.0 / ChargeUtils.VEHICLE_TIMES) + ""),
                Pair.of("redstone_price", (1.0 / ChargeUtils.REDSTONE_TIMES) + "")
        );
        return ItemStackSheet.fromString(material, message);
    }

}
