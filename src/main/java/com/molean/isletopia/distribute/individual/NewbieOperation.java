package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.I18n;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import net.craftersland.data.bridge.api.events.SyncCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NewbieOperation implements Listener {

    public NewbieOperation() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void checkNewbie(Player player) {
        if (!player.isOnline()) {
            return;
        }
        Set<Plot> plots = PlotSquared.get().getPlots(PlotPlayer.wrap(player));
        if (plots.size() != 0) {
            return;
        }

        List<String> servers = ServerInfoUpdater.getServers();
        for (String server : servers) {
            if (!server.startsWith("server"))
                continue;
            Integer plotID = PlotDao.getPlotID(server, player.getName());
            if (plotID != null) {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    player.kickPlayer(I18n.getMessage("error.island.mismatch", player));
                });
                return;
            }
        }
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 4));
            player.performCommand("plot auto");
            placeItem(player.getInventory());
        });


    }

    @EventHandler()
    public void onSync(SyncCompleteEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {

            if (!event.getPlayer().getInventory().contains(Material.CLOCK)) {
                event.getPlayer().getInventory().addItem(newUnbreakableItem(Material.CLOCK, I18n.getMessage("menu.item.title",event.getPlayer()),
                        List.of(I18n.getMessage("menu.item.lore.1",event.getPlayer()),
                                I18n.getMessage("menu.item.lore.2",event.getPlayer()))));
            }

            String server = UniversalParameter.getParameter(event.getPlayer().getName(), "server");
            if (server == null) {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    event.getPlayer().kickPlayer(I18n.getMessage("error.island.noIsland",event.getPlayer()));
                });
                return;
            }
            if (server.equalsIgnoreCase(ServerInfoUpdater.getServerName())) {
                checkNewbie(event.getPlayer());
            }
        });

        //check plot number
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (event.getPlayer().isOp()) {
                return;
            }
            int cnt = 0;
            for (String server : ServerInfoUpdater.getServers()) {
                if (server.equalsIgnoreCase("dispatcher"))
                    continue;
                Integer plotID = PlotDao.getPlotID(server, event.getPlayer().getName());
                if (plotID != null) {
                    cnt++;
                }
            }
            if (cnt > 1) {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    event.getPlayer().kickPlayer(I18n.getMessage("error.island.more",event.getPlayer()));
                });

            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onLeft(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        UniversalParameter.setParameter(player.getName(), "lastServer", ServerInfoUpdater.getServerName());
    }

    public void placeItem(PlayerInventory inventory) {
        ItemStack food = new ItemStack(Material.APPLE, 32);
        ItemStack lavaBucket = new ItemStack(Material.LAVA_BUCKET);
        ItemStack ice = new ItemStack(Material.ICE, 2);
        inventory.addItem(food, lavaBucket, ice);
    }

    public ItemStack newUnbreakableItem(Material material, String name, List<String> lores) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setUnbreakable(true);
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
