package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.utils.MapUtils;
import com.molean.isletopia.utils.NMSTagUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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

public class StaticMap implements CommandExecutor, TabCompleter, Listener {
    public StaticMap() {
        Objects.requireNonNull(Bukkit.getPluginCommand("staticmap")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("staticmap")).setTabCompleter(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        ItemStack icon = new ItemStack(Material.FILLED_MAP);
        ItemMeta itemMeta = icon.getItemMeta();
        itemMeta.displayName(Component.text("§f跨区地图"));
        itemMeta.lore(List.of(Component.text("§dStatic Map")));
        icon.setItemMeta(itemMeta);
        ItemStack type = new ItemStack(Material.ANVIL);
        ItemStack[] source = new ItemStack[9];
        for (int i = 0; i < source.length; i++) {
            source[i] = new ItemStack(Material.AIR);
        }
        source[4] = new ItemStack(Material.FILLED_MAP);
        ItemMeta itemMeta1 = source[4].getItemMeta();
        itemMeta1.displayName(Component.text("§f任意普通地图"));
        source[4].setItemMeta(itemMeta1);
        ItemStack result = icon.clone();

        LocalRecipe.addRecipe(icon, type, source, result);
    }


    @EventHandler
    public void onJoin(PlayerDataSyncCompleteEvent event) {
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
            if (!itemStack.getType().equals(Material.FILLED_MAP)) {
                return;
            }
            String colors = NMSTagUtils.get(itemStack, "static-map");
            ItemMeta itemMeta = itemStack.getItemMeta();
            MapUtils.updateStaticMap(colors, (MapMeta) itemMeta);
            itemStack.setItemMeta(itemMeta);
        }, 1L);
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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!itemStack.getType().equals(Material.FILLED_MAP)) {
            player.sendMessage("你手上的不是静态地图");
            return true;
        }
        String colors = NMSTagUtils.get(itemStack, "static-map");

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 4) {
                player.sendMessage("Args not enough!");
                player.sendMessage("/staticmap set [x] [y] [c]");
                return true;
            }
            colors = MapUtils.setPixel(colors, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            itemStack = NMSTagUtils.set(itemStack, "static-map", colors);
            ItemMeta itemMeta = itemStack.getItemMeta();
            MapUtils.updateStaticMap(colors, (MapMeta) itemMeta);
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItemInMainHand(itemStack);
        }
        if (args[0].equalsIgnoreCase("fill")) {
            if (args.length < 6) {
                player.sendMessage("Args not enough!");
                player.sendMessage("/staticmap fill [x1] [y1] [x2] [y2] [c]");
                return true;
            }
            int x1 = Integer.parseInt(args[1]);
            int y1 = Integer.parseInt(args[2]);
            int x2 = Integer.parseInt(args[3]);
            int y2 = Integer.parseInt(args[4]);
            int c = Integer.parseInt(args[5]);
            colors = MapUtils.fillRectangle(colors, x1, y1, x2, y2, c);
            itemStack = NMSTagUtils.set(itemStack, "static-map", colors);
            ItemMeta itemMeta = itemStack.getItemMeta();
            MapUtils.updateStaticMap(colors, (MapMeta) itemMeta);
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItemInMainHand(itemStack);
        }
        if (args[0].equalsIgnoreCase("image")) {
            if (args.length < 4) {
                player.sendMessage("Args not enough!");
                player.sendMessage("/staticmap image [x] [y] [url]");
                return true;
            }
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            String url = args[3];
            BufferedImage read;
            try {
                read = ImageIO.read(new URL(url));
            } catch (IOException e) {
                player.sendMessage("URL is invalid!");
                return true;
            }
            BufferedImage finalRead = read;
            String newColor = MapUtils.drawImage(colors, x, y, finalRead);
            itemStack = NMSTagUtils.set(itemStack, "static-map", newColor);
            ItemMeta itemMeta = itemStack.getItemMeta();
            MapUtils.updateStaticMap(newColor, (MapMeta) itemMeta);
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItemInMainHand(itemStack);
        }
        if (args[0].equalsIgnoreCase("text")) {
            if (args.length < 4) {
                player.sendMessage("Args not enough!");
                player.sendMessage("/staticmap text [x] [y] [text]");
                return true;
            }
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            String text = args[3];
            colors = MapUtils.drawText(colors, x, y, MinecraftFont.Font, text);
            itemStack = NMSTagUtils.set(itemStack, "static-map", colors);
            ItemMeta itemMeta = itemStack.getItemMeta();
            MapUtils.updateStaticMap(colors, (MapMeta) itemMeta);
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItemInMainHand(itemStack);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("set");
            list.add("fill");
            list.add("image");
            list.add("text");
        }
        return list;
    }
}
