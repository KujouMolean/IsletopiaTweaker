package com.molean.isletopia.cloud;

import com.molean.isletopia.shared.model.CloudInventorySlot;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CloudInventoryDetailMenu extends ChestMenu {

    public static String getTitle(Player player, CloudInventorySlot cloudInventorySlot) {
        String materialString = cloudInventorySlot.getMaterial();
        Material material = Material.getMaterial(materialString);
        assert material != null;
       return LangUtils.get(player.locale(), material.translationKey());
    }

    public CloudInventoryDetailMenu(Player player, CloudInventorySlot inventorySlot) {
        super(player, 3, Component.text(getTitle(player, inventorySlot)));
        String materialString = inventorySlot.getMaterial();
        Material material = Material.getMaterial(materialString);
        assert material != null;
        this.item(4, ItemStackSheet.fromString(material, """
                                
                %d
                """, inventorySlot.getAmount()).build());

        this.item(18, ItemStackSheet.fromString(Material.HOPPER, """
                §f自动提交
                §7将背包中的该物品自动提交到云仓库
                §7(不含装备栏/物品栏)
                /ci EnableAutoPut XXX 开启
                /ci DisableAutoPut XXX 关闭
                """).build());
            close();


        this.item(20, ItemStackSheet.fromString(Material.DISPENSER, """
                §f自动填充
                §7若物品栏中该物品未达一叠
                §7则从云仓中取出物品填满一叠
                /ci EnableAutoGet XXX 开启
                /ci DisableAutoGet XXX 关闭
                """).build());

        this.item(22, ItemStackSheet.fromString(Material.BUCKET, """
                §f示例: 手动从背包向云仓提交10个苹果
                §7/ci put APPLE 10
                """).build());

        this.item(24, ItemStackSheet.fromString(Material.WATER_BUCKET, """
                §f示例: 手动向云仓获取10个苹果到背包
                §7/ci get APPLE 10
                """).build());

        this.item(26, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, """
                §f指令帮助
                §f主指令 /cloudInventory 缩写 /ci
                                
                /ci create material
                /ci get material amount
                /ci put material amount
                /ci EnableAutoGet material
                /ci EnableAutoPut material
                /ci gui
                其他指令待补充
                """).build());
    }
}
