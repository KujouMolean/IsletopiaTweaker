package com.molean.isletopia.menu;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.blueprint.obj.BluePrintData;
import com.molean.isletopia.shared.utils.ObjectUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.NMSTagUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class BluePrintMenu implements Listener {
    private final Player player;
    private final Inventory inventory;
    private final Location bot;


    public BluePrintMenu(Player player, Location bot) {
        this.player = player;
        this.bot = bot;
        inventory = Bukkit.createInventory(player, 9, Component.text("蓝图菜单"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < 9; i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        inventory.setItem(0, new ItemStack(player.getInventory().getItemInMainHand()));

        ItemStackSheet preview = new ItemStackSheet(Material.SPYGLASS, "§f构建预览");
        preview.addLore("§7根据蓝图中现有的材料进行预览");
        preview.addLore("§7预览结果与构建结果一致");
        inventory.setItem(2, preview.build());

        ItemStackSheet fullPreview = new ItemStackSheet(Material.ENDER_EYE, "§f完整预览");
        fullPreview.addLore("§7预览蓝图填充满材料后的结果");
        inventory.setItem(4, fullPreview.build());

        ItemStackSheet build = new ItemStackSheet(Material.DIAMOND_PICKAXE, "§f构建蓝图");
        build.addLore("§7在此处放置此蓝图");
        build.addLore("§7缺失材料的方块会被跳过");
        build.addLore("§7(推荐先构建预览一次)");
        inventory.setItem(6, build.build());

        ItemStackSheet materials = new ItemStackSheet(Material.PAPER, "§f填充材料说明");
        materials.addLore("§7将蓝图放在主手，材料放副手，按F即可填充材料");
        materials.addLore("§7可将材料放在潜影盒一次性填充");
        inventory.setItem(8, materials.build());

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
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        byte[] bytes = NMSTagUtils.getAsBytes(itemInMainHand, "BluePrint");
        BluePrintData bluePrintData = (BluePrintData) ObjectUtils.deserialize(bytes);
        if (bluePrintData == null) {
            MessageUtils.fail(player, "蓝图解析失败，无法提供材料。");
            player.closeInventory();
            return;
        }
        switch (slot) {
//            case 2 -> {
//                boolean b = bluePrintTemplate.checkPlace(bot);
//                bluePrintTemplate.preview(player, bot, 600,false);
//                if (b) {
//                    MessageUtils.success(player, "正在预览，该位置可以构建此蓝图。");
//                } else {
//                    MessageUtils.fail(player, "该位置不可构建此蓝图，正在强制预览。");
//                }
//            }
//            case 4 -> {
//                boolean b = bluePrintTemplate.checkPlace(bot);
//                bluePrintTemplate.preview(player, bot, 600,true);
//                if (b) {
//                    MessageUtils.success(player, "正在预览，该位置可以构建此蓝图。");
//                } else {
//                    MessageUtils.fail(player, "该位置不可构建此蓝图，正在强制预览。");
//                }
//            }
//            case 6 -> {
//                if (bluePrintTemplate.checkPlace(bot)) {
//                    bluePrintTemplate.forcePlace(bot);
//                    byte[] serialize = ObjectUtils.serialize(bluePrintTemplate);
//                    ItemStack result = NMSTagUtils.set(itemInMainHand, "BluePrint", serialize);
//                    Selection.updateItemStack(result, bluePrintTemplate);
//                    player.getInventory().setItemInMainHand(result);
//                    MessageUtils.success(player, "构建成功!");
//                } else {
//                    MessageUtils.success(player, "该位置不可构建此蓝图。");
//                }
//            }
        }
        player.closeInventory();
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
