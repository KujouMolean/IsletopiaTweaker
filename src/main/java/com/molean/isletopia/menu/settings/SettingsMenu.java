package com.molean.isletopia.menu.settings;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.menu.settings.member.MemberMenu;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SettingsMenu extends ChestMenu {

    public SettingsMenu(Player player) {
        super(player, 2, Component.text("岛屿设置"));

        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        ItemStackSheet visit = new ItemStackSheet(Material.GRASS_BLOCK, "§f更改生物群系");
        itemWithAsyncClickEvent(0, visit.build(), () -> new BiomeMenu(player).open());

        ItemStackSheet member = new ItemStackSheet(Material.PLAYER_HEAD, "§f添加删除成员");
        itemWithAsyncClickEvent(1, member.build(), () -> new MemberMenu(player).open());

        ItemStackSheet bed = new ItemStackSheet(Material.RED_BED, "§f更改复活位置");
        item(2, bed.build(), () -> {
            player.performCommand("is setHome");
            close();
        });

        if (currentPlot.containsFlag("Lock")) {
            ItemStackSheet cancel = new ItemStackSheet(Material.IRON_DOOR, "§f点击开放岛屿");
            cancel.addLore("§7当前岛屿模式: §c锁定");
            cancel.addLore("§7仅岛屿成员可访问此岛屿.");
            item(3, cancel.build(), () -> {
                if (currentPlot.containsFlag("Lock")) {
                    player.performCommand("is unlock");
                } else {
                    player.performCommand("is lock");
                }
                close();
            });
        } else {
            ItemStackSheet denyAll = new ItemStackSheet(Material.OAK_DOOR, "§f点击闭关锁岛");
            denyAll.addLore("§7当前岛屿模式: §2开放");
            denyAll.addLore("§7任何人都可以访问此岛屿.");
            item(3, denyAll.build(), () -> {
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) return;
                if (currentIsland.containsFlag("Lock")) {
                    player.performCommand("is unlock");
                } else {
                    player.performCommand("is lock");
                }
            });
        }

        ItemStackSheet icon = new ItemStackSheet(Material.GRASS_BLOCK, "§f设置岛屿图标");
        icon.addLore("§7副手手持需要的图标");
        icon.addLore("§7设置成功后会消耗掉副手物品");
        item(4, icon.build(), () -> {
            player.performCommand("is setIcon");
        });

        ItemStackSheet name = new ItemStackSheet(Material.NAME_TAG, "§f设置岛屿名称");
        name.addLore("§7自行使用指令：/is name XXX");
        item(5, name.build(), () -> {
            MessageUtils.info(player, "使用: is name XXX");
            close();
        });

        ItemStackSheet preferred = new ItemStackSheet(Material.APPLE, "§f首选岛屿");
        preferred.addLore("§7如果你有多个岛屿，你会优先传送到首选岛屿上");
        preferred.addLore("§7当前: " + (currentPlot.containsFlag("Preferred") ? "首选" : "非首选"));
        item(6, preferred.build(), () -> {
            player.performCommand("is preferred");
            close();
        });


        ItemStackSheet allowDrop = new ItemStackSheet(Material.DROPPER, "§f游客丢弃物品权限");
        allowDrop.addLore("§7当前: " + (currentPlot.containsFlag("AllowItemDrop") ? "允许" : "不允许"));
        item(7, allowDrop.build(), () -> {
            player.performCommand("is allowItemDrop");
            close();
        });

        ItemStackSheet allowPickup = new ItemStackSheet(Material.HOPPER, "§f游客拾起物品权限");
        allowPickup.addLore("§7当前: " + (currentPlot.containsFlag("AllowItemPickup") ? "允许" : "不允许"));
        item(8, allowPickup.build(), () -> {
            player.performCommand("is allowItemPickup");
            close();
        });

        ItemStackSheet spectator = new ItemStackSheet(Material.FEATHER, "§f游客旁观模式");
        spectator.addLore("§7当前: " + (currentPlot.containsFlag("SpectatorVisitor") ? "开启" : "关闭"));
        item(9, spectator.build(), () -> {
            player.performCommand("is spectatorVisitor");
            close();
        });

        ItemStackSheet antiFire = new ItemStackSheet(Material.CAMPFIRE, "§b[Hex]§f岛屿防火");
        antiFire.addLore("§7当前: " + (currentPlot.containsFlag("AntiFire") ? "开启" : "关闭"));
        if (currentPlot.containsFlag("AntiFire")) {
            antiFire.addLore("§c关闭此选项不会返还海洋之心");
        } else {
            antiFire.addLore("§c开启此选项需要消耗宝石海洋之心");
        }
        item(10, antiFire.build(), () -> {
            player.performCommand("is antiFire");
            close();
        });


        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f返回主菜单");
        itemWithAsyncClickEvent(17, father.build(), () -> new PlayerMenu(player).open());
    }


    private BukkitTask bukkitTask = null;

    @Override
    public void beforeOpen() {
        super.beforeOpen();
        Random random = new Random();
        List<Material> spawnEggs = getSpawnEggs();
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            ItemStack item = inventory.getItem(0);
            if (item != null) {
                item.setType(spawnEggs.get(random.nextInt(spawnEggs.size())));
            }
        }, 0, 20);
    }

    @Override
    public void afterClose() {
        super.afterClose();
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    public static List<Material> getSpawnEggs() {
        List<Material> spawnEggs = new ArrayList<>();
        for (Material value : Material.values()) {
            if (value.name().contains("SPAWN_EGG")) {
                spawnEggs.add(value);
            }
        }
        return spawnEggs;
    }


}
