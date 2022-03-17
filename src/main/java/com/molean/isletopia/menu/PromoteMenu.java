package com.molean.isletopia.menu;

import com.molean.isletopia.shared.model.PromoteDao;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.visit.MultiVisitMenu;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PromoteMenu extends ListMenu<PromoteDao.Promote> {
    public PromoteMenu(Player player) {
        super(player, Component.text(MessageUtils.getMessage(player, "menu.promote.title")));
        List<PromoteDao.Promote> query = PromoteDao.query();
        this.components(query);
        this.convertFunction(promote -> {
            Island island = IslandManager.INSTANCE.getIsland(promote.islandId);
            assert island != null;
            ItemStack itemStack = MultiVisitMenu.islandToItemStack(player, island);
            List<Component> lore = itemStack.lore();
            assert lore != null;
            lore.add(Component.text(MessageUtils.getMessage(player, "menu.promote.buyer", Pair.of("buyer", UUIDManager.get(promote.uuid)))));
            lore.add(Component.text(promote.localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            itemStack.lore(lore);
            return itemStack;
        });
        this.onClickSync(promote -> {
            Island island = IslandManager.INSTANCE.getIsland(promote.islandId);
            assert island != null;
            IsletopiaTweakersUtils.universalPlotVisitByMessage(player, island.getIslandId());
        });
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main")).build())
                .onCloseSync(() -> {
                })
                .onCloseAsync(() -> new MainMenu(player).open());
    }
}
