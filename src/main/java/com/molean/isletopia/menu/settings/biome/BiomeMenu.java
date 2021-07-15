package com.molean.isletopia.menu.settings.biome;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.settings.SettingsMenu;
import com.molean.isletopia.infrastructure.individual.MessageUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import net.kyori.adventure.text.Component;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BiomeMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<Biome> biomes = List.of(Biome.values());
    private final int page;

    public BiomeMenu(Player player) {
        this(player, 0);
    }

    public BiomeMenu(Player player, int page) {
        this.player = player;
        this.page = page;
        inventory = Bukkit.createInventory(player, 54, Component.text("选择想要切换的生物群系:"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }

        Biome biome = player.getLocation().getBlock().getBiome();

        for (int i = page * 52; i < biomes.size() && i - page * 52 < inventory.getSize() - 2; i++) {
            String name = "未知";
            Material icon = Material.PLAYER_HEAD;
            List<String> creatures = new ArrayList<>();
            List<String> environments = new ArrayList<>();
            String id = biomes.get(i).name();
            try {
                LocalBiome localBiome = LocalBiome.valueOf(biomes.get(i).name().toUpperCase());
                name = localBiome.getName();
                icon = localBiome.getIcon();
                creatures.addAll(localBiome.getCreatures());
                creatures.removeIf(String::isEmpty);
                environments.addAll(localBiome.getEnvironment());
                environments.removeIf(String::isEmpty);
            } catch (IllegalArgumentException ignore) {
            }
            ItemStackSheet itemStackSheet = new ItemStackSheet(icon, "§f" + name);
            if (!creatures.isEmpty()) {
                itemStackSheet.addLore("§f生物: " + String.join(", ", creatures));
            }
            if (!environments.isEmpty()) {
                itemStackSheet.addLore("§f环境: " + String.join(", ", environments));
            }
            if (biome.name().equalsIgnoreCase(id)) {
                itemStackSheet.addItemFlag(ItemFlag.HIDE_ENCHANTS);
                itemStackSheet.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                String display = itemStackSheet.getDisplay();
                itemStackSheet.setDisplay("§f当前所在生物群系: " + display);
            }
            inventory.setItem(i - page * 52, itemStackSheet.build());
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
            if (page == 0) {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new BiomeMenu(player, 1).open());
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new BiomeMenu(player, 0).open());
            }
            return;
        }

        Plot currentPlot = PlotUtils.getCurrentPlot(player);

        String biomeName = biomes.get(slot + page * 52).name();
        String name = "未知";
        try {
            name = LocalBiome.valueOf(biomeName.toUpperCase()).getName();
        } catch (IllegalArgumentException ignore) {
        }
        assert currentPlot != null;
        if (Objects.equals(currentPlot.getOwner(), player.getUniqueId())) {
            player.sendMessage("§8[§3岛屿助手§8] §7尝试修改岛屿生物群系...");
            String finalName = name;
            currentPlot.getPlotModificationManager().setBiome(BiomeTypes.get(biomeName.toLowerCase(Locale.ROOT)), () ->
                    player.sendMessage("§8[§3岛屿助手§8] §7成功修改生物群系为:" + finalName + "."));
        } else {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () ->
                    player.kick(Component.text("错误, 非岛主操作岛屿成员.")));
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
