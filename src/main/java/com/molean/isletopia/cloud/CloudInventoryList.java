package com.molean.isletopia.cloud;

import com.molean.isletopia.shared.database.CloudInventoryDao;
import com.molean.isletopia.shared.model.CloudInventorySlot;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class CloudInventoryList extends ListMenu<CloudInventorySlot> {

    public CloudInventoryList(Player player) {
        super(player, Component.text("云仓(测试中|不稳定)"));
        List<CloudInventorySlot> inventorySlotsSnapshot = null;
        try {
            inventorySlotsSnapshot = CloudInventoryDao.getInventorySlotsSnapshot(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException("db err");
        }
        inventorySlotsSnapshot.add(new CloudInventorySlot());
        this.components(inventorySlotsSnapshot);
        this.convertFunction(slot -> {
            if (slot.getMaterial() == null) {
                return ItemStackSheet.fromString(Material.GREEN_STAINED_GLASS_PANE, """
                        扩展仓库位
                        (暂时仅可扩展9个仓库位)
                        """).build();
            }
            String materialString = slot.getMaterial();
            int amount = slot.getAmount();
            Material material = Material.valueOf(materialString);
            return ItemStackSheet.fromString(material, """
                                        
                    x%d
                    """, amount).build();
        });

        this.onClickAsync(cloudInventorySlot -> {

            String material = cloudInventorySlot.getMaterial();

            if (material == null) {
                MessageUtils.notify(player, "/ci create 材料名称");
                close();
            }else{
                new CloudInventoryDetail(player, cloudInventorySlot).open();
            }

        });



    }

}
