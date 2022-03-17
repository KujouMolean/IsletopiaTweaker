package com.molean.isletopia.menu.settings;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.menu.settings.member.MemberMenu;
import com.molean.isletopia.shared.utils.Pair;
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
        super(player, 2, Component.text(MessageUtils.getMessage(player, "menu.settings.title")));

        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        ItemStackSheet visit = new ItemStackSheet(Material.GRASS_BLOCK, MessageUtils.getMessage(player, "menu.settings.biome"));
        itemWithAsyncClickEvent(0, visit.build(), () -> new BiomeMenu(player).open());

        ItemStackSheet member = new ItemStackSheet(Material.PLAYER_HEAD, MessageUtils.getMessage(player, "menu.settings.member"));
        itemWithAsyncClickEvent(1, member.build(), () -> new MemberMenu(player).open());

        ItemStackSheet bed = new ItemStackSheet(Material.RED_BED, MessageUtils.getMessage(player, "menu.settings.home"));
        item(2, bed.build(), () -> {
            player.performCommand("is setHome");
            close();
        });

        if (currentPlot.containsFlag("Lock")) {


            ItemStackSheet unlock = ItemStackSheet.fromString(Material.IRON_DOOR, MessageUtils.getMessage(player, "menu.settings.unlock"));

            item(3, unlock.build(), () -> {
                player.performCommand("is unlock");

                close();
            });
        } else {
            ItemStackSheet lock = ItemStackSheet.fromString(Material.OAK_DOOR, MessageUtils.getMessage(player, "menu.settings.lock"));
            item(3, lock.build(), () -> {
                player.performCommand("is lock");
            });
        }

        ItemStackSheet icon = ItemStackSheet.fromString(Material.GRASS_BLOCK, MessageUtils.getMessage(player, "menu.settings.icon"));
        item(4, icon.build(), () -> {
            player.performCommand("is setIcon");
        });

        ItemStackSheet name = ItemStackSheet.fromString(Material.NAME_TAG, MessageUtils.getMessage(player, "menu.settings.name"));
        item(5, name.build(), () -> {
            MessageUtils.info(player, "/is name XXX");
            close();
        });

        ItemStackSheet preferred = ItemStackSheet.fromString(Material.APPLE,
                MessageUtils.getMessage(player, "menu.settings.prefer",
                        Pair.of("status", currentPlot.containsFlag("Preferred") ?
                                MessageUtils.getMessage(player, "menu.settings.prefer.true") :
                                MessageUtils.getMessage(player, "menu.settings.prefer.false")
                        )));

        item(6, preferred.build(), () -> {
            player.performCommand("is preferred");
            close();
        });


        ItemStackSheet allowDrop = new ItemStackSheet(Material.DROPPER, MessageUtils.getMessage(player, "menu.settings.drop",
                Pair.of("status", currentPlot.containsFlag("AllowItemDrop") ?
                        MessageUtils.getMessage(player, "menu.settings.drop.true") :
                        MessageUtils.getMessage(player, "menu.settings.drop.false")
                )));
        item(7, allowDrop.build(), () -> {
            player.performCommand("is allowItemDrop");
            close();
        });

        ItemStackSheet allowPickup = ItemStackSheet.fromString(Material.HOPPER, "menu.settings.pick",
                Pair.of("status", currentPlot.containsFlag("AllowItemPickup") ?
                        MessageUtils.getMessage(player, "menu.settings.pick.true") :
                        MessageUtils.getMessage(player, "menu.settings.pick.false")
                ));

        item(8, allowPickup.build(), () -> {
            player.performCommand("is allowItemPickup");
            close();
        });

        ItemStackSheet spectator = ItemStackSheet.fromString(Material.FEATHER, "menu.settings.spectator",
                Pair.of("status", currentPlot.containsFlag("SpectatorVisitor") ?
                        MessageUtils.getMessage(player, "menu.settings.spectator.true") :
                        MessageUtils.getMessage(player, "menu.settings.spectator.false")
                ));
        item(9, spectator.build(), () -> {
            player.performCommand("is spectatorVisitor");
            close();
        });

        ItemStackSheet antiFire = ItemStackSheet.fromString(Material.CAMPFIRE, "menu.settings.antifire",
                Pair.of("status", currentPlot.containsFlag("AntiFire") ?
                        MessageUtils.getMessage(player, "menu.settings.antifire.true") :
                        MessageUtils.getMessage(player, "menu.settings.antifire.false")
                ));
        if (!currentPlot.containsFlag("AntiFire") && !currentPlot.containsFlag("DisableAntiFire")) {
            antiFire.addLore(MessageUtils.getMessage(player, "menu.settings.antifire.first"));
        }
        item(10, antiFire.build(), () -> {
            player.performCommand("is antiFire");
            close();
        });

        ItemStackSheet beacon = ItemStackSheet.fromString(Material.BEACON, MessageUtils.getMessage(player, "menu.settings.hexbeacon"));
        itemWithAsyncClickEvent(11, beacon.build(), () -> new HexBeacon(player).open());

        ItemStackSheet priority = ItemStackSheet.fromString(Material.ARROW, MessageUtils.getMessage(player, "menu.settings.priority"));
        item(12, priority.build(), () -> {
            MessageUtils.info(player, "/is priority XX");
            close();
        });


        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main"));
        itemWithAsyncClickEvent(17, father.build(), () -> new MainMenu(player).open());
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
