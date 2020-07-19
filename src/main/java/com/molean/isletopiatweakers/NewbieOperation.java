package com.molean.isletopiatweakers;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.RegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Set;

public class NewbieOperation implements Listener {

    public NewbieOperation() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }
    public void checkNewbie(Player player) {
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
            if (!player.isOnline())
                return;
            Set<Plot> plots = PlotSquared.get().getPlots(PlotPlayer.wrap(player));
            if (plots.size() == 0) {
                player.performCommand("plot auto");
                placeItem(player.getInventory());
            }
            if (!player.getInventory().contains(Material.CLOCK)) {
                player.getInventory().addItem(newUnbreakableItem(Material.CLOCK, "§f[§d主菜单§f]§r",
                        List.of("§f[§f左键单击§f]§r §f回到§r §f主岛屿§r", "§f[§7右键单击§f]§r §f打开§r §f主菜单§r")));
            }
        }, 100);
    }

    @EventHandler
    public void onRegister(RegisterEvent event) {
        checkNewbie(event.getPlayer());
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        checkNewbie(event.getPlayer());
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
        ItemStack food = new ItemStack(Material.APPLE, 16);

        inventory.setHelmet(helmet);
        inventory.setChestplate(chestPlate);
        inventory.setLeggings(leggings);
        inventory.setBoots(boots);
        inventory.addItem(menu, food, sword, axe, pickAxe, hoe, shovel);
    }

    public ItemStack newUnbreakableItem(Material material, String name, List<String> lores) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
