package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
import com.molean.isletopia.network.Response;
import com.molean.isletopia.network.UniversalParameter;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
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
import java.util.Set;

public class NewbieOperation implements Listener {

    public NewbieOperation() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void checkNewbie(Player player) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            Bukkit.getLogger().info("Check newbie operation for " + player.getName());
            if (!player.isOnline()) {
                Bukkit.getLogger().info("Player is not online. Then exit");
                return;
            }

            Set<Plot> plots = PlotSquared.get().getPlots(PlotPlayer.wrap(player));
            if (plots.size() == 0) {
                Bukkit.getLogger().info("Plot size is 0, try to operate...");
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 4));
                player.performCommand("plot auto");
                placeItem(player.getInventory());
            }
            if (!player.getInventory().contains(Material.CLOCK)) {
                Bukkit.getLogger().info("No clock in inventory, try to give...");
                player.getInventory().addItem(newUnbreakableItem(Material.CLOCK, "§f[§d主菜单§f]§r",
                        List.of("§f[§f西弗特左键单击§f]§r §f回到§r §f主岛屿§r", "§f[§7右键单击§f]§r §f打开§r §f主菜单§r")));
            }
        });
    }

    @EventHandler
    public void onSync(SyncCompleteEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Bukkit.getLogger().info(event.getPlayer().getName() + " sync completed, start to check server.");
            Request request = new Request("dispatcher", "getParameter");
            request.set("player", event.getPlayer().getName());
            request.set("key", "server");
            Response response = Client.send(request);
            if (response == null) {
                Bukkit.getLogger().info("Get server parameter failed, kick player.");
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    event.getPlayer().kickPlayer("[1]获取信息失败, 请重新进入服务器.");
                });

                return;
            }
            if (response.getStatus().equalsIgnoreCase("successfully")) {
                Bukkit.getLogger().info("Get server parameter successfully.");
                String server = response.get("return");
                if (server.equalsIgnoreCase(IsletopiaTweakers.getServerName())) {
                    Bukkit.getLogger().info("Server matched, then start newbiew check.");
                    checkNewbie(event.getPlayer());
                } else {
                    Bukkit.getLogger().info("Server not match, skip newbie check.");
                }
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
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            UniversalParameter.setParameter(player.getName(), "lastServer", IsletopiaTweakers.getServerName());
        });

    }

    public void placeItem(PlayerInventory inventory) {

        ItemStack menu = newUnbreakableItem(Material.CLOCK, "§f[§d主菜单§f]§r",
                List.of("§f[§f左键单击§f]§r §f回到§r §f主岛屿§r", "§f[§7右键单击§f]§r §f打开§r §f主菜单§r"));
        ItemStack helmet = newUnbreakableItem(Material.LEATHER_HELMET, "§f[§d新手帽子§f]§r", List.of());
        ItemStack chestPlate = newUnbreakableItem(Material.LEATHER_CHESTPLATE, "§f[§d新手上衣§f]§r", List.of());
        ItemStack leggings = newUnbreakableItem(Material.LEATHER_LEGGINGS, "§f[§d新手裤子§f]§r", List.of());
        ItemStack boots = newUnbreakableItem(Material.LEATHER_BOOTS, "§f[§d新手靴子§f]§r", List.of());
        ItemStack sword = newUnbreakableItem(Material.WOODEN_SWORD, "§f[§d新手木剑§f]§r", List.of());
        ItemStack shovel = newUnbreakableItem(Material.WOODEN_SHOVEL, "§f[§d新手木锹§f]§r", List.of());
        ItemStack pickAxe = newUnbreakableItem(Material.WOODEN_PICKAXE, "§f[§d新手木镐§f]§r", List.of());
        ItemStack axe = newUnbreakableItem(Material.WOODEN_AXE, "§f[§d新手木斧§f]§r", List.of());
        ItemStack hoe = newUnbreakableItem(Material.WOODEN_HOE, "§f[§d新手木锄§f]§r", List.of());
        ItemStack food = new ItemStack(Material.APPLE, 32);
        ItemStack lavaBucket = new ItemStack(Material.LAVA_BUCKET);
        ItemStack ice = new ItemStack(Material.ICE, 2);

        inventory.setHelmet(helmet);
        inventory.setChestplate(chestPlate);
        inventory.setLeggings(leggings);
        inventory.setBoots(boots);
        inventory.addItem(menu, food, sword, axe, pickAxe, hoe, shovel, lavaBucket, ice);
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
