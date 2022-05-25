package com.molean.isletopia.menu.visit;

import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
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

public class VisitorMenu extends ListMenu<Pair<String, Timestamp>> {
    public VisitorMenu(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter, Player player) {

        super(player, Component.text(MessageUtils.getMessage(player, "menu.visitor.title")));
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.hasPermission(player) || player.isOp())) {
            MessageUtils.fail(player, "menu.visitor.noPermission");
            return;
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

            components.add(Component.text(MessageUtils.getMessage(player, "menu.visitor.time", Pair.of("time", format))));
            itemStack.lore(components);
            return itemStack;
        });

        this.closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main")).build());
        this.onCloseAsync(() -> new MainMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .onCloseSync(null);

    }
}
