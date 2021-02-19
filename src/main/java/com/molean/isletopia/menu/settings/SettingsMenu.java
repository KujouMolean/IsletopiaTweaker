package com.molean.isletopia.menu.settings;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.infrastructure.individual.MessageUtils;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.menu.settings.member.MemberMenu;
import com.molean.isletopia.utils.PlotUtils;
import com.molean.isletopia.utils.Sftp;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.backup.BackupProfile;
import com.plotsquared.core.location.BlockLoc;
import com.plotsquared.core.plot.Plot;
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

import java.nio.file.Path;
import java.util.*;

public class SettingsMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private boolean stop = false;
    private boolean open;

    public SettingsMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 18, MessageUtils.getMessage("menu.settings.title"));
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
        for (int i = 0; i < 18; i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }

        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (!currentPlot.getOwner().equals(player.getUniqueId())) {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.kickPlayer(MessageUtils.getMessage("error.menu.settings.non-owner"));
            });
            return;
        }
        HashSet<UUID> denied = currentPlot.getDenied();
        ItemStackSheet visit = new ItemStackSheet(Material.GRASS_BLOCK, MessageUtils.getMessage("menu.settings.biome"));
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

        ItemStackSheet member = new ItemStackSheet(Material.PLAYER_HEAD, MessageUtils.getMessage("menu.settings.member"));
        inventory.setItem(2, member.build());

        ItemStackSheet bed = new ItemStackSheet(Material.RED_BED, MessageUtils.getMessage("menu.settings.home"));
        inventory.setItem(4, bed.build());


        if (denied.contains(PlotDao.getAllUUID())) {
            ItemStackSheet cancel = new ItemStackSheet(Material.IRON_DOOR, MessageUtils.getMessage("menu.settings.unlock"));
            cancel.addLore(MessageUtils.getMessage("menu.settings.unlock.1"));
            cancel.addLore(MessageUtils.getMessage("menu.settings.unlock.2"));
            inventory.setItem(6, cancel.build());
            open = false;

        } else {
            ItemStackSheet denyAll = new ItemStackSheet(Material.OAK_DOOR, MessageUtils.getMessage("menu.settings.lock"));
            denyAll.addLore(MessageUtils.getMessage("menu.settings.lock.1"));
            denyAll.addLore(MessageUtils.getMessage("menu.settings.lock.2"));
            inventory.setItem(6, denyAll.build());
            open = true;
        }

        ItemStackSheet shield = new ItemStackSheet(Material.SHIELD, MessageUtils.getMessage("menu.settings.backup"));
        shield.addLore(MessageUtils.getMessage("menu.settings.backup.1"));
        shield.addLore(MessageUtils.getMessage("menu.settings.backup.2"));
        shield.addLore(MessageUtils.getMessage("menu.settings.backup.3"));
        shield.addLore(MessageUtils.getMessage("menu.settings.backup.4"));
        inventory.setItem(8, shield.build());

        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage("menu.settings.return"));
        inventory.setItem(17, father.build());
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
                if (currentPlot.getOwner().equals(player.getUniqueId())) {
                    Location location = player.getLocation();
                    com.plotsquared.core.location.Location bottomAbs = currentPlot.getBottomAbs();

                    currentPlot.setHome(new BlockLoc(
                            location.getBlockX() - bottomAbs.getX(),
                            location.getBlockY() - bottomAbs.getY(),
                            location.getBlockZ() - bottomAbs.getZ(),
                            location.getYaw(),
                            location.getPitch()));
                    player.sendMessage(MessageUtils.getMessage("menu.settings.home.ok")
                            .replace("%1%", location.getBlockX() + "")
                            .replace("%2%", location.getBlockX() + "")
                            .replace("%3%", location.getBlockZ() + "")
                    );
                    player.closeInventory();
                } else {
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        player.kickPlayer(MessageUtils.getMessage("error.menu.settings.non-owner"));
                    });
                }
                break;
            }

            case 6: {
                Plot currentPlot = PlotUtils.getCurrentPlot(player);
                if (currentPlot.getOwner().equals(player.getUniqueId())) {
                    if (open) {
                        currentPlot.addDenied(PlotDao.getAllUUID());
                    } else {
                        currentPlot.removeDenied(PlotDao.getAllUUID());
                    }
                    Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new SettingsMenu(player).open());
                } else {
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        player.kickPlayer(MessageUtils.getMessage("error.menu.settings.non-owner"));
                    });
                }
                break;
            }
            case 8: {
                player.closeInventory();
                Plot currentPlot = PlotUtils.getCurrentPlot(player);
                if (!currentPlot.getOwner().equals(player.getUniqueId())) {
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        player.kickPlayer(MessageUtils.getMessage("error.menu.settings.non-owner"));
                    });
                    return;
                }
                if (!player.getInventory().contains(Material.DIAMOND_BLOCK)) {
                    player.sendMessage(MessageUtils.getMessage("backup.noCost"));
                    return;
                }
                for (ItemStack itemStack : player.getInventory()) {
                    if (itemStack != null && itemStack.getType().equals(Material.DIAMOND_BLOCK)) {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        break;
                    }
                }
                player.sendMessage(MessageUtils.getMessage("backup.start.1"));
                player.sendMessage(MessageUtils.getMessage("backup.start.2"));
                BackupProfile profile = PlotSquared.imp().getBackupManager().getProfile(currentPlot);
                profile.createBackup().whenComplete((backup, throwable) -> {
                    if (throwable != null) {
                        player.sendMessage(MessageUtils.getMessage("backup.failed"));
                        return;
                    }
                    profile.listBackups().whenComplete((backups, throwable1) -> {
                        if (backups.size() == 0 || throwable1 != null) {
                            player.sendMessage(MessageUtils.getMessage("backup.failed"));
                            return;
                        }
                        Path path = backups.get(0).getFile();
                        assert path != null;
                        UUID uuid = UUID.randomUUID();
                        boolean b = Sftp.uploadFile(path.toString(), "/var/www/skin/schem/" + uuid + ".schem");
                        if (!b) {
                            player.sendMessage(MessageUtils.getMessage("backup.failed"));
                            return;
                        }
                        String url = "http://skin.molean.com/schem/" + uuid + ".schem";
                        player.sendMessage(MessageUtils.getMessage("backup.success").replace("%url%", url));
                    });
                });
                break;
            }

            case 17: {
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
