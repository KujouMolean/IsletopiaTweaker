package com.molean.isletopia.menu.visit;

import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.utils.IslandUtils;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MultiVisitMenu extends ListMenu<Island> {

    public static ItemStack islandToItemStack(Player player,Island island) {
        String icon = island.getIcon();
        Material material;
        try {
            material = Material.valueOf(icon);
        } catch (IllegalArgumentException ignored) {
            material = Material.GRASS_BLOCK;
        }
        ItemStackSheet itemStackSheet = ItemStackSheet.fromString(material, IslandUtils.getDisplayInfo(player.locale(), island));
        return itemStackSheet.build();
    }

    public static void sortIsland(List<Island> islandList) {
        islandList.sort((o1, o2) -> {
            String priority1 = o1.getFlagData("Priority");
            String priority2 = o2.getFlagData("Priority");
            int p1 = 0, p2 = 0;
            try {
                p1 = Integer.parseInt(priority1);
            } catch (NumberFormatException ignored) {
            }
            try {
                p2 = Integer.parseInt(priority2);
            } catch (NumberFormatException ignored) {
            }
            if (o1.containsFlag("Preferred")) {
                p1 = Integer.MAX_VALUE;
            }
            if (o2.containsFlag("Preferred")) {
                p2 = Integer.MAX_VALUE;
            }
            return p2 - p1;
        });
    }

    public MultiVisitMenu(Player player, List<Island> islandList) {
        super(player, Component.text(MessageUtils.getMessage(player, "menu.multivisit.title")));
        sortIsland(islandList);
        this.components(islandList);
        this.convertFunction(island -> MultiVisitMenu.islandToItemStack(player, island));
        this.onClickSync(island -> IsletopiaTweakersUtils.universalPlotVisitByMessage(player, island.getIslandId()));
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main")).build())
                .onCloseSync(null)
                .onCloseAsync(() -> new MainMenu(player).open());
    }

    @Override
    public void afterOpen() {
        super.afterOpen();
        if (components().size() != 1) {
            return;
        }
        if (!PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableSingleIslandMenu")) {
            return;
        }
        IsletopiaTweakersUtils.universalPlotVisitByMessage(player, components().get(0).getIslandId());
        close();
    }
}
