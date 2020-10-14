package com.molean.isletopia.menu.settings.biome;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.settings.SettingsMenu;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.world.biome.BiomeType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BiomeMenu1 implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<LocalBiome> biomes = new ArrayList<>(Arrays.asList(LocalBiome.values()));

    public BiomeMenu1(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 54, "选择想要切换的生物群系:");
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }


        Biome biome = player.getLocation().getBlock().getBiome();
        for (int i = 0; i < biomes.size() && i < inventory.getSize() - 2; i++) {
            LocalBiome localBiome = biomes.get(i);
            ItemStackSheet itemStackSheet = new ItemStackSheet(localBiome.getIcon(), "§f" + localBiome.getName());
            if (!localBiome.getCreatures().isEmpty()) {
                itemStackSheet.addLore("§f生物: " + String.join(", ", localBiome.getCreatures()));
            }
            if (!localBiome.getEnvironment().isEmpty()) {
                itemStackSheet.addLore("§f环境: " + String.join(", ", localBiome.getEnvironment()));
            }
            for (String s : localBiome.getExtraInfo()) {
                itemStackSheet.addLore(s);
            }
            if (biome.name().equalsIgnoreCase(localBiome.name())) {
                itemStackSheet.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                itemStackSheet.addItemFlag(ItemFlag.HIDE_ENCHANTS);
                String display = itemStackSheet.getDisplay();
                itemStackSheet.setDisplay("§f当前所在生物群系: " + display);
            }
            inventory.setItem(i, itemStackSheet.build());
        }

        ItemStackSheet next = new ItemStackSheet(Material.LADDER, "§f下一页");
        inventory.setItem(inventory.getSize() - 2, next.build());

        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f返回主菜单");
        inventory.setItem(inventory.getSize() - 1, father.build());

        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory), 1);

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
        if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new SettingsMenu(player).open());
            return;
        }
        if (slot == inventory.getSize() - 2) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new BiomeMenu2(player).open());
            return;
        }

        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        LocalBiome localBiome = biomes.get(slot);
        if (currentPlot.getOwner().equals(player.getUniqueId())) {
            player.sendMessage("§8[§3岛屿助手§8] §7尝试修改岛屿生物群系...");
            currentPlot.setBiome(new BiomeType(localBiome.name()), () -> {
                player.sendMessage("§8[§3岛屿助手§8] §7成功修改生物群系为:" + localBiome.getName() + ".");
            });
        } else {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory));
            });
        }

        player.closeInventory();

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
        event.getHandlers().unregister(this);

    }
}
