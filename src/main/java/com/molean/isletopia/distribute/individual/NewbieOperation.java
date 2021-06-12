package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.infrastructure.individual.MessageUtils;
import com.molean.isletopia.utils.HeadUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import net.craftersland.data.bridge.api.events.SyncCompleteEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
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
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                        player.kick(Component.text("严重错误, 在其他服务器拥有岛屿, 但位置与数据库不匹配.")));
                return;
            }
        }

        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 4));
            player.performCommand("plot auto");
            placeItem(player.getInventory());
            ItemStack clock = newUnbreakableItem(Material.CLOCK, "§f[§d主菜单§f]§r",
                    List.of(Component.text("§f[§f西弗特左键单击§f]§r §f回到§r §f主岛屿§r"),
                            Component.text("§f[§7右键单击§f]§r §f打开§r §f主菜单§r")));
            player.getInventory().addItem(clock);
        });


    }

    @EventHandler()
    public void onSync(SyncCompleteEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {

            // if player has not clock
            String server = UniversalParameter.getParameter(event.getPlayer().getName(), "server");
            if (server == null) {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                        event.getPlayer().kick(Component.text("严重错误, 已加入服务器, 但未被分配岛屿.")));
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
            List<String> servers = new ArrayList<>(ServerInfoUpdater.getServers());
            for (String server : servers) {
                if (!server.startsWith("server"))
                    continue;
                Integer plotID = PlotDao.getPlotID(server, event.getPlayer().getName());
                if (plotID != null) {
                    cnt++;
                }
            }
            if (cnt > 1) {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                        event.getPlayer().kick(Component.text("发生错误, 非管理员但拥有多个岛屿.")));

            }
            HeadUtils.getSkull(event.getPlayer().getName());
        });
    }


    public void placeItem(PlayerInventory inventory) {
        ItemStack food = new ItemStack(Material.APPLE, 32);
        ItemStack lavaBucket = new ItemStack(Material.LAVA_BUCKET);
        ItemStack waterBucket1 = new ItemStack(Material.WATER_BUCKET, 1);
        ItemStack waterBucket2 = new ItemStack(Material.WATER_BUCKET, 1);
        inventory.addItem(food, lavaBucket, waterBucket1, waterBucket2);
    }

    public ItemStack newUnbreakableItem(Material material, String name, List<Component> lores) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setUnbreakable(true);
        itemMeta.setLocalizedName(name);
        itemStack.lore(lores);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
