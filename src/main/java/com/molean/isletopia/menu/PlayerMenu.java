package com.molean.isletopia.menu;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.club.ClubListMenu;
import com.molean.isletopia.menu.visit.MultiVisitMenu;
import com.molean.isletopia.shared.database.AchievementDao;
import com.molean.isletopia.shared.database.ClubDao;
import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.shared.model.*;
import com.molean.isletopia.shared.utils.PlayerUtils;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ChestMenu;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

public class PlayerMenu extends ChestMenu {

    public PlayerMenu(Player player, UUID target) {
        super(player, 6, Component.text(Objects.requireNonNull(UUIDManager.get(target))));


        for (int i = 0; i < 6 * 9; i++) {
            this.item(i, ItemStackSheet.fromString(Material.GRAY_STAINED_GLASS_PANE, " ").build());
        }

        ItemStack skull = HeadUtils.getSkull(UUIDManager.get(target));
        ItemStackSheet itemStackSheet = ItemStackSheet.fromString(skull, PlayerUtils.getDisplay(target));

        this.item(4, itemStackSheet.build());

        try {
            List<PlayerAchievement> playerAchievements = AchievementDao.getPlayerAchievements(target);
            for (int i = 0; i < playerAchievements.size() && i < 8; i++) {
                String achievementName = playerAchievements.get(i).getAchievement();
                Achievement achievement = AchievementDao.getAchievement(achievementName);
                assert achievement != null;
                String icon = achievement.getIcon();
                Material material = Material.STONE;
                try {
                    material = Material.valueOf(icon.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ignored) {
                }
                ItemStackSheet sheet = ItemStackSheet.fromString(material, achievement.toString());
                this.item(27 + i, sheet.build());
            }

            ItemStackSheet achievementSheet = ItemStackSheet.fromString(Material.REDSTONE_TORCH, """
                    §f查看此玩家获得的更多成就
                    """).addEnchantment(Enchantment.ARROW_DAMAGE, 0).addItemFlag(ItemFlag.HIDE_ENCHANTS);
            if (playerAchievements.size() > 8) {
                this.itemWithAsyncClickEvent(27 + 8, achievementSheet.build(), () -> {
                    new ListMenu<PlayerAchievement>(player, Component.text("成就")).components(playerAchievements)
                            .convertFunction(playerAchievement -> {
                                String achievementName = playerAchievement.getAchievement();
                                Achievement achievement = null;
                                try {
                                    achievement = AchievementDao.getAchievement(achievementName);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                assert achievement != null;
                                String icon = achievement.getIcon();
                                Material material = Material.STONE;
                                try {
                                    material = Material.valueOf(icon.toUpperCase(Locale.ROOT));
                                } catch (IllegalArgumentException ignored) {
                                }
                                ItemStackSheet sheet = ItemStackSheet.fromString(material, achievement.toString());
                                return sheet.build();
                            }).closeItemStack(ItemStackSheet.fromString(Material.BARRIER, "返回").build())
                            .onCloseAsync(() -> new PlayerMenu(player, target).open())
                            .onCloseSync(null);

                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(target);
        MultiVisitMenu.sortIsland(playerIslands);
        for (int i = 0; i < playerIslands.size() && i < 8; i++) {
            IslandId islandId = playerIslands.get(i).getIslandId();
            this.itemWithAsyncClickEvent(18 + i, MultiVisitMenu.islandToItemStack(player, playerIslands.get(i)), () -> {
                IsletopiaTweakersUtils.universalPlotVisitByMessage(player, islandId);
            });
        }
        ItemStackSheet visit = ItemStackSheet.fromString(Material.FEATHER, """
                §f访问该玩家的更多岛屿
                """).addEnchantment(Enchantment.ARROW_DAMAGE, 0).addItemFlag(ItemFlag.HIDE_ENCHANTS);
        if (playerIslands.size() > 8) {
            this.item(18 + 8, visit.build(), () -> {
                player.performCommand("visit " + UUIDManager.get(target));
            });

        }


        if (CollectionDao.getPlayerCollections(player.getUniqueId()).contains(target)) {
            ItemStackSheet star = ItemStackSheet.fromString(Material.SPYGLASS, """
                    §f取消关注此玩家
                    """).addEnchantment(Enchantment.ARROW_DAMAGE, 0).addItemFlag(ItemFlag.HIDE_ENCHANTS);
            this.itemWithAsyncClickEvent(47, star.build(), () -> {
                String name = UUIDManager.get(target);
                Tasks.INSTANCE.sync(() -> {
                    player.performCommand("is unstar " + name);
                    close();
                });
            });

        } else {
            ItemStackSheet star = ItemStackSheet.fromString(Material.SPYGLASS, """
                    §f关注此玩家
                    §7将该玩家添加到你的关注列表
                    §7该玩家上线和下线都会通知您
                    """);
            this.itemWithAsyncClickEvent(47, star.build(), () -> {
                String name = UUIDManager.get(target);
                Tasks.INSTANCE.sync(() -> {
                    player.performCommand("is star " + name);
                    close();
                });
            });
        }

        this.item(49, ItemStackSheet.fromString(Material.BARRIER, "§f关闭").build(), this::close);

        ItemStackSheet club = ItemStackSheet.fromString(Material.WRITABLE_BOOK, """
                §f查看此玩家的所在社团
                """);

        this.itemWithAsyncClickEvent(51, club.build(), () -> {
            Tasks.INSTANCE.async(() -> {
                Set<Club> joinedClubs = null;
                try {
                    joinedClubs = ClubDao.getJoinedClubs(target);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                assert joinedClubs != null;
                if (joinedClubs.isEmpty()) {
                    return;
                }
                new ClubListMenu(player, new ArrayList<>(joinedClubs)).open();

            });
        });


    }


}
