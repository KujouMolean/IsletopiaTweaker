package com.molean.isletopia.menu;

import com.molean.isletopia.shared.database.IslandDao;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VisitorMenu extends ListMenu<Pair<String, Timestamp>> {


    private List<Pair<String, Timestamp>> pairs;

    public VisitorMenu(Player player) {
        super(player, Component.text("访客列表"));
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.hasPermission(player) || player.isOp())) {
            MessageUtils.fail(player, "你只能查看自己岛屿的访客记录!");
            throw new RuntimeException("not owner");
        }

        try {
            this.components(IslandDao.queryVisit(currentIsland.getId(), 3));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("sql error");
        }
        this.onClickSync(stringTimestampPair -> {
            String key = stringTimestampPair.getKey();
            player.performCommand("visit " + key);
        });
        this.convertFunction(stringTimestampPair -> {
            ArrayList<Component> components = new ArrayList<>();
            ItemStack itemStack = HeadUtils.getSkullWithIslandInfo(stringTimestampPair.getKey());
            if (itemStack.lore() != null) {
                components.addAll(itemStack.lore());
            }
            Timestamp value = stringTimestampPair.getValue();
            LocalDateTime localDateTime = value.toLocalDateTime();
            String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            components.add(Component.text("访问时间: " + format));
            itemStack.lore(components);
            return itemStack;
        });

        this.closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回主菜单").build());
        this.onCloseSync(() -> {
            new PlayerMenu(player).open();
        });
    }
}
