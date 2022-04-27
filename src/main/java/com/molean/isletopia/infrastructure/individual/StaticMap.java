package com.molean.isletopia.infrastructure.individual;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MapUtils;
import com.molean.isletopia.utils.NMSTagUtils;
import com.molean.isletopia.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StaticMap implements  Listener {
    public StaticMap() {
        PluginUtils.registerEvents(this);
        ItemStack icon = new ItemStack(Material.FILLED_MAP);
        ItemMeta itemMeta = icon.getItemMeta();
        itemMeta.displayName(Component.text("recipe.map.title"));
        itemMeta.lore(List.of(Component.text("§dStatic Map")));
        icon.setItemMeta(itemMeta);
        ItemStack type = new ItemStack(Material.ANVIL);
        ItemStack[] source = new ItemStack[9];
        for (int i = 0; i < source.length; i++) {
            source[i] = new ItemStack(Material.AIR);
        }
        source[4] = new ItemStack(Material.FILLED_MAP);
        ItemMeta itemMeta1 = source[4].getItemMeta();
        itemMeta1.displayName(Component.text("recipe.map.source.title"));
        source[4].setItemMeta(itemMeta1);
        ItemStack result = icon.clone();

        LocalRecipe.addRecipe(icon, type, source, result);
    }


    @EventHandler
    public void on(EntityAddToWorldEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.ITEM_FRAME)) {
            return;
        }
        ItemFrame itemFrame = (ItemFrame) entity;
        ItemStack item = itemFrame.getItem();
        if (!item.getType().equals(Material.FILLED_MAP)) {
            return;
        }
        String colors = NMSTagUtils.get(item, "static-map");
        if (colors != null && !colors.isEmpty()) {
            ItemMeta itemMeta = item.getItemMeta();
            MapUtils.updateStaticMap(colors, (MapMeta) itemMeta);
            item.setItemMeta(itemMeta);
            itemFrame.setItem(item);
        }
    }

    @EventHandler
    public void onJoin(PlayerDataSyncCompleteEvent event) {
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        Tasks.INSTANCE.timeout(1, () -> {
            if (!itemStack.getType().equals(Material.FILLED_MAP)) {
                return;
            }
            String colors = NMSTagUtils.get(itemStack, "static-map");
            ItemMeta itemMeta = itemStack.getItemMeta();
            MapUtils.updateStaticMap(colors, (MapMeta) itemMeta);
            itemStack.setItemMeta(itemMeta);
        });
    }

    @EventHandler
    public void onPlayer(PlayerItemHeldEvent event) {
        int newSlot = event.getNewSlot();
        ItemStack itemStack = event.getPlayer().getInventory().getItem(newSlot);
        if (itemStack == null || !itemStack.getType().equals(Material.FILLED_MAP)) {
            return;
        }
        String colors = NMSTagUtils.get(itemStack, "static-map");
        ItemMeta itemMeta = itemStack.getItemMeta();
        MapUtils.updateStaticMap(colors, (MapMeta) itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    @EventHandler
    public void on(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getFirstItem();
        ItemStack secondItem = event.getInventory().getSecondItem();
        if (secondItem != null) {
            return;
        }
        if (firstItem == null || !firstItem.getType().equals(Material.FILLED_MAP)) {
            return;
        }

        if (NMSTagUtils.get(firstItem, "static-map") != null) {
            return;
        }
        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) firstItem.getItemMeta();
        String colors = MapUtils.getColors(mapMeta);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text(Objects.requireNonNull(event.getInventory().getRenameText())));
        itemMeta.lore(List.of(Component.text("Static Map")));
        itemStack.setItemMeta(itemMeta);
        assert colors != null;
        itemStack = NMSTagUtils.set(itemStack, "static-map", colors);
        event.getInventory().setRepairCost(10);
        event.setResult(itemStack);
    }
}
