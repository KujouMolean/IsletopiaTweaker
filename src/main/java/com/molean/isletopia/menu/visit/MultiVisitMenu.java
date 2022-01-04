package com.molean.isletopia.menu.visit;

import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MultiVisitMenu extends ListMenu<Island> {
    public MultiVisitMenu(Player player, List<Island> islandList) {
        super(player, Component.text("选择你要访问的岛屿"));
        this.components(islandList);
        this.convertFunction(island -> {
            String icon = island.getIcon();
            Material material;
            try {
                material = Material.valueOf(icon);
            } catch (IllegalArgumentException ignored) {
                material = Material.GRASS_BLOCK;
            }
            ItemStackSheet itemStackSheet = new ItemStackSheet(material);
            String name = island.getName();
            if (name == null) {
                name = "未命名";
            }
            itemStackSheet.setDisplay("§7" + name + "§7(#" + island.getId() + ")");
            itemStackSheet.addLore("§7岛屿主人: " + UUIDUtils.get(island.getUuid()));

            IslandId islandId = island.getIslandId();

            if (island.getMembers().size() > 0) {
                itemStackSheet.addLore("§7岛屿成员: ");
                int cnt = 0;
                ArrayList<UUID> uuids = new ArrayList<>(island.getMembers());
                StringBuilder line = new StringBuilder();
                line.append(" §7- ");
                for (int i = 0; i < uuids.size() && i < 16; i++) {
                    line.append(UUIDUtils.get(uuids.get(i)));
                    line.append("  ");
                    cnt++;
                    if (cnt % 4 == 0 || cnt == uuids.size()) {
                        itemStackSheet.addLore(line.toString());
                        line = new StringBuilder();
                        line.append(" §7- ");
                    }
                }
                if (island.getMembers().size() > 16) {
                    itemStackSheet.addLore(" §7- ...");
                }
            }
            Timestamp creation = island.getCreation();
            String format = creation.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            itemStackSheet.addLore("§7创建日期: " + format);
            itemStackSheet.addLore("位于%s,%d,%d".formatted(islandId.getServer(), islandId.getX(), islandId.getZ()));
            if (island.containsFlag("Lock")) {
                itemStackSheet.addLore("§c该岛屿锁定，非成员无法访问");
            }
            return itemStackSheet.build();
        });

        this.onClickSync(island -> IsletopiaTweakersUtils.universalPlotVisitByMessage(player, island.getIslandId()));
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回主菜单").build())
                .onCloseSync(() -> {})
                .onCloseAsync(() -> new PlayerMenu(player).open());
    }
}
