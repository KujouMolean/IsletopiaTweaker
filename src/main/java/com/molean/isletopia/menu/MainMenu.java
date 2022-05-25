package com.molean.isletopia.menu;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.dialog.ConfirmDialog;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.menu.assist.AssistMenu;
import com.molean.isletopia.menu.charge.PlayerChargeMenu;
import com.molean.isletopia.menu.club.ClubRealmMenu;
import com.molean.isletopia.menu.favorite.SubscribeMenu;
import com.molean.isletopia.menu.settings.SettingsMenu;
import com.molean.isletopia.menu.visit.MultiVisitMenu;
import com.molean.isletopia.menu.visit.PromoteMenu;
import com.molean.isletopia.menu.visit.VisitMenu;
import com.molean.isletopia.menu.visit.VisitorMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.model.PromoteDao;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.utils.*;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainMenu extends ChestMenu {
    static {
        PromoteDao.checkTable();
    }

    public MainMenu(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter, Player player) {

        super(player, 6, Component.text(MessageUtils.getMessage(player, "menu.title")));

        for (int i = 0; i < 6 * 9; i++) {
            this.item(i, ItemStackSheet.fromString(Material.GRAY_STAINED_GLASS_PANE, " ").build());
        }
        List<PromoteDao.Promote> query = PromoteDao.query();
        int index = 0;
        for (PromoteDao.Promote promote : query) {
            Island island = IslandManager.INSTANCE.getIsland(promote.islandId);
            if (island == null) {
                continue;
            }
            ItemStack itemStack = MultiVisitMenu.islandToItemStack(player, island);
            List<Component> lore = itemStack.lore();
            assert lore != null;
            lore.add(Component.text(MessageUtils.getMessage(player, "menu.promote.buyer", Pair.of("buyer", UUIDManager.get(promote.uuid)))));
            lore.add(Component.text(promote.localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            itemStack.lore(lore);
            this.item(index, itemStack, () -> IsletopiaTweakersUtils.universalPlotVisitByMessage(player, island.getIslandId()));
            index++;
            if (index >= 8) {
                break;
            }
        }

        ItemStackSheet heart = ItemStackSheet.fromString(Material.HEART_OF_THE_SEA, MessageUtils.getMessage(player, "menu.promote"))
                .addEnchantment(Enchantment.ARROW_DAMAGE, 0).addItemFlag(ItemFlag.HIDE_ENCHANTS);
        this.item(8, heart.build());
        this.clickEventAsync(8, clickType -> {
            if (clickType.equals(ClickType.LEFT)) {
                new PromoteMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open();
            }
        });
        this.clickEventSync(8, clickType -> {
            if (clickType.equals(ClickType.RIGHT)) {
                ConfirmDialog confirmDialog = new ConfirmDialog(player, Component.text(MessageUtils.getMessage(player, "menu.promote.confirm")));
                confirmDialog.onConfirm(player1 -> {
                    LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                    if (currentIsland == null) {
                        MessageUtils.fail(player, "menu.promote.failed.empty");
                        return;
                    }
                    if (InventoryUtils.takeItem(player, Material.HEART_OF_THE_SEA, 1)) {
                        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                            PromoteDao.add(currentIsland.getId(), player.getUniqueId());
                            new MainMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open();
                            MessageUtils.success(player, "menu.promote.success");
                        });
                    } else {
                        MessageUtils.fail(player, "menu.promote.failed.heart");
                    }
                });
                confirmDialog.open();
            }
        });


        ItemStackSheet bookShelf = ItemStackSheet.fromString(Material.ENCHANTED_GOLDEN_APPLE, MessageUtils.getMessage(player, "menu.clubrealm"));

        ItemStackSheet favorite = ItemStackSheet.fromString(Material.NETHER_STAR, MessageUtils.getMessage(player, "menu.subscribe"));

        ItemStackSheet visits = ItemStackSheet.fromString(Material.FEATHER, MessageUtils.getMessage(player, "menu.visit"));

        ItemStackSheet settings = ItemStackSheet.fromString(Material.LEVER, MessageUtils.getMessage(player, "menu.settings"));

        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);

        assert currentPlot != null;
        if (!player.getUniqueId().equals(currentPlot.getUuid())) {
            settings = ItemStackSheet.fromString(Material.LEVER, MessageUtils.getMessage(player, "menu.settings.notOwned"));
        }

        ItemStackSheet bills = ItemStackSheet.fromString(Material.PAPER, MessageUtils.getMessage(player, "menu.power"));

        ItemStackSheet assist = ItemStackSheet.fromString(Material.BEACON, MessageUtils.getMessage(player, "menu.assist"));

        ItemStackSheet projects = ItemStackSheet.fromString(Material.END_PORTAL_FRAME, MessageUtils.getMessage(player, "menu.visitor"));

        ItemStackSheet glass = ItemStackSheet.fromString(Material.GRAY_STAINED_GLASS_PANE, MessageUtils.getMessage(player, "menu.spliter"));
        for (int i = 9; i < 3 * 9; i++) {
            this.item(i, glass.build());
        }

        ItemStack skullWithIslandInfo = HeadUtils.getSkullWithIslandInfo(player.getName());
        SkullMeta itemMeta = (SkullMeta) skullWithIslandInfo.getItemMeta();
        assert itemMeta != null;
        itemMeta.displayName(Component.text(MessageUtils.getMessage(player, "menu.is")));
        itemMeta.lore(List.of(Component.text(MessageUtils.getMessage(player, "menu.is.left"))));
        itemMeta.lore(List.of(Component.text(MessageUtils.getMessage(player, "menu.is.right"))));
        skullWithIslandInfo.setItemMeta(itemMeta);

        this
                .itemWithAsyncClickEvent(35, bookShelf.build(), () -> new ClubRealmMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(29, favorite.build(), () -> new SubscribeMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(31, visits.build(), () -> new VisitMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(33, settings.build(), () -> new SettingsMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(27, projects.build(), () -> new VisitorMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(47, bills.build(), () -> new PlayerChargeMenu(chargeCommitter, playerPropertyManager, sidebarManager, player).open())
                .item(49, skullWithIslandInfo)
                .clickEventSync(49, clickType -> {
                    if (clickType.equals(ClickType.LEFT)) {
                        player.performCommand("is");
                    } else if (clickType.equals(ClickType.RIGHT)) {
                        player.performCommand("visit " + player.getName());
                    }
                })
                .itemWithAsyncClickEvent(51, assist.build(), () -> new AssistMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open());

    }
}
