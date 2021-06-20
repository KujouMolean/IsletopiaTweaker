package com.molean.isletopia.menu.settings;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.menu.settings.member.MemberMenu;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.location.BlockLoc;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SettingsMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private boolean stop = false;
    private boolean open;

    public SettingsMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 9, Component.text("岛屿设置"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
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

    public void open() {
        for (int i = 0; i < 9; i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }

        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        assert currentPlot != null;
        if (!currentPlot.getOwner().equals(player.getUniqueId())) {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                    player.kick(Component.text("错误, 非岛主更改岛屿设置.")));
            return;
        }
        HashSet<UUID> denied = currentPlot.getDenied();
        ItemStackSheet visit = new ItemStackSheet(Material.GRASS_BLOCK, "§f= 更改生物群系 =");
        inventory.setItem(0, visit.build());

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Random random = new Random();
            List<Material> spawnEggs = getSpawnEggs();

            while (!stop) {
                ItemStack item = inventory.getItem(0);
                if (item != null) {
                    item.setType(spawnEggs.get(random.nextInt(spawnEggs.size())));
                }

                try {

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        ItemStackSheet member = new ItemStackSheet(Material.PLAYER_HEAD, "§f< 添加删除成员 >");
        inventory.setItem(2, member.build());

        ItemStackSheet bed = new ItemStackSheet(Material.RED_BED,"§f| 更改复活位置 |");
        inventory.setItem(4, bed.build());

        if (denied.contains(PlotDao.getAllUUID())) {
            ItemStackSheet cancel = new ItemStackSheet(Material.IRON_DOOR, "§f! 点击开放岛屿 !");
            cancel.addLore("§7当前岛屿模式: §c锁定");
            cancel.addLore("§7仅岛屿成员可访问你的岛屿.");
            inventory.setItem(6, cancel.build());
            open = false;
        } else {
            ItemStackSheet denyAll = new ItemStackSheet(Material.OAK_DOOR, "§f! 点击闭关锁岛 !");
            denyAll.addLore("§7当前岛屿模式: §2开放");
            denyAll.addLore("§7任何人都可以访问你的岛屿.");
            inventory.setItem(6, denyAll.build());
            open = true;
        }

        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f<<返回主菜单<<");
        inventory.setItem(8, father.build());
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
        if (!event.getClick().equals(ClickType.LEFT)) {
            return;
        }
        int slot = event.getSlot();
        if (slot < 0) {
            return;
        }
        switch (slot) {
            case 0: {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new BiomeMenu(player).open());
                break;
            }
            case 2: {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new MemberMenu(player).open());
                break;
            }
            case 4: {
                Plot currentPlot = PlotUtils.getCurrentPlot(player);
                assert currentPlot != null;
                if (currentPlot.getOwner().equals(player.getUniqueId())) {
                    Location location = player.getLocation();
                    com.plotsquared.core.location.Location bottomAbs = currentPlot.getBottomAbs();

                    currentPlot.setHome(new BlockLoc(
                            location.getBlockX() - bottomAbs.getX(),
                            location.getBlockY() - bottomAbs.getY(),
                            location.getBlockZ() - bottomAbs.getZ(),
                            location.getYaw(),
                            location.getPitch()));
                    player.sendMessage("§8[§3岛屿助手§8] §7成功修改重生点为: %1%,%2%,%3%"
                            .replace("%1%", location.getBlockX() + "")
                            .replace("%2%", location.getBlockX() + "")
                            .replace("%3%", location.getBlockZ() + "")
                    );
                    player.closeInventory();
                } else {
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                            player.kick(Component.text("错误, 非岛主更改岛屿设置.")));
                }
                break;
            }

            case 6: {
                Plot currentPlot = PlotUtils.getCurrentPlot(player);
                assert currentPlot != null;
                if (currentPlot.getOwner().equals(player.getUniqueId())) {
                    if (open) {
                        currentPlot.addDenied(PlotDao.getAllUUID());
                        List<PlotPlayer<?>> playersInPlot = currentPlot.getPlayersInPlot();
                        for (PlotPlayer<?> plotPlayer : playersInPlot) {
                            Player player = Bukkit.getPlayer(plotPlayer.getUUID());
                            if (player == null) {
                                continue;
                            }
                            if (!PlotUtils.hasCurrentPlotPermission(player)) {
                                player.performCommand("is");
                            }
                        }
                    } else {
                        currentPlot.removeDenied(PlotDao.getAllUUID());
                    }
                    Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new SettingsMenu(player).open());
                } else {
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                            player.kick(Component.text("错误, 非岛主更改岛屿设置.")));
                }
                break;
            }
            case 8: {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerMenu(player).open());
                break;
            }
        }

    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        stop = true;
        event.getHandlers().unregister(this);

    }
}
