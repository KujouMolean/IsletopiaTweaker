package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.utils.NMSTagUtils;
import net.craftersland.data.bridge.api.events.SyncCompleteEvent;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
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
        itemMeta.setDisplayName("§f跨区地图");
        itemMeta.setLore(List.of("§dStatic Map"));
        icon.setItemMeta(itemMeta);
        ItemStack type = new ItemStack(Material.ANVIL);
        ItemStack[] source = new ItemStack[9];
        for (int i = 0; i < source.length; i++) {
            source[i] = new ItemStack(Material.AIR);
        }
        source[4] = new ItemStack(Material.FILLED_MAP);
        ItemMeta itemMeta1 = source[4].getItemMeta();
        itemMeta1.setDisplayName("§f任意普通地图");
        source[4].setItemMeta(itemMeta1);
        ItemStack result = icon.clone();

        LocalRecipe.addRecipe(icon, type, source, result);
    }


    private static void updateStaticMap(ItemStack itemStack, Player player) {
        if (itemStack == null || !itemStack.getType().equals(Material.FILLED_MAP)) {
            return;
        }
        String colors = NMSTagUtils.get(itemStack, "static-map");
        if (colors == null || colors.isEmpty()) {
            return;
        }
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        MapView mapView = Bukkit.createMap(player.getWorld());
        while (!mapView.getRenderers().isEmpty()) {
            mapView.removeRenderer(mapView.getRenderers().get(0));
        }
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
                try {
                    for (int i = 0; i < 128 * 128; i++) {
                        int x = i % 128;
                        int y = i / 128;
                        byte color = (byte) Integer.parseInt(colors.substring(i * 2, i * 2 + 2), 16);
                        mapCanvas.setPixel(x, y, color);
                    }
                } catch (Exception e) {
                    player.sendMessage("ERROR");
                    e.printStackTrace();
                }
            }
        });
        mapMeta.setMapView(mapView);
        itemStack.setItemMeta(mapMeta);
    }

    private static void setPixel(ItemStack itemStack, int x, int y, int color) {
        String colors = NMSTagUtils.get(itemStack, "static-map");
        if (colors == null || colors.isEmpty()) {
            return;
        }
        if (x > 128 || x < 0 || y > 128 || y < 0 || color > 0xFF || color < 0) {
            return;
        }
        int p = (y * 128 + x) * 2;
        colors = colors.substring(0, p) + String.format("%02x", color) + colors.substring(p + 2);
        ItemMeta newMeta = NMSTagUtils.set(itemStack, "static-map", colors).getItemMeta();
        itemStack.setItemMeta(newMeta);
    }

    private static void fillRectangle(ItemStack itemStack, int x1, int y1, int x2, int y2, int color) {
        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                setPixel(itemStack, i, j, color);
            }
        }
    }

    public void drawText(ItemStack itemStack, int x, int y, MapFont font, String text) {
        int xStart = x;
        byte color = 44;
        if (!font.isValid(text)) {
            return;
        }
        int i = 0;
        while (i < text.length()) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                x = xStart;
                y += font.getHeight() + 1;
            } else if (ch == 167) {
                int j = text.indexOf(59, i);
                if (j < 0) {
                    break;
                }

                try {
                    color = Byte.parseByte(text.substring(i + 1, j));
                    i = j;
                } catch (NumberFormatException var12) {
                    return;
                }
            } else {
                MapFont.CharacterSprite sprite = font.getChar(text.charAt(i));
                assert sprite != null;
                for (int r = 0; r < font.getHeight(); ++r) {
                    for (int c = 0; c < sprite.getWidth(); ++c) {
                        if (sprite.get(r, c)) {
                            setPixel(itemStack, x + c, y + r, unsignedByteToInt(color));
                        }
                    }
                }
                x += sprite.getWidth() + 1;
            }
            ++i;
        }

    }

    private static int unsignedByteToInt(byte b) {
        return Integer.parseInt(String.format("%x", b), 16);
    }

    private static void drawImage(ItemStack itemStack, int x, int y, BufferedImage image) {
        if (image == null) {
            return;
        }
        @SuppressWarnings("deprecation")
        byte[] bytes = MapPalette.imageToBytes(image);
        for (int x2 = 0; x2 < image.getWidth(null); ++x2) {
            for (int y2 = 0; y2 < image.getHeight(null); ++y2) {
                Color color = new Color(image.getRGB(x2, y2));
                @SuppressWarnings("deprecation")
                byte b = MapPalette.matchColor(color);
                setPixel(itemStack, x + x2, y + y2, unsignedByteToInt(b));
            }
        }
    }


    @EventHandler
    public void onSyncCom(SyncCompleteEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
            updateStaticMap(itemStack, player);
        }, 1L);
    }

    @EventHandler
    public void onPlayer(PlayerItemHeldEvent event) {
        int newSlot = event.getNewSlot();
        Player player = event.getPlayer();
        ItemStack itemStack = event.getPlayer().getInventory().getItem(newSlot);
        updateStaticMap(itemStack, player);
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
        MapView mapView = mapMeta.getMapView();

        StringBuilder colors = new StringBuilder();
        if (mapView == null) {
            return;
        }
        for (MapRenderer renderer : mapView.getRenderers()) {
            try {
                Field worldMapField = renderer.getClass().getDeclaredField("worldMap");
                worldMapField.setAccessible(true);
                Object worldMap = worldMapField.get(renderer);
                Field worldMapColorsField = worldMap.getClass().getDeclaredField("colors");
                worldMapColorsField.setAccessible(true);
                byte[] bytes = (byte[]) worldMapColorsField.get(worldMap);
                for (int y = 0; y < 128; y++) {
                    for (int x = 0; x < 128; x++) {
                        Byte color = bytes[y * 128 + x];
                        colors.append(String.format("%02x", color));
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(event.getInventory().getRenameText());
        itemMeta.setLore(List.of("Static Map"));
        itemStack.setItemMeta(itemMeta);
        itemStack = NMSTagUtils.set(itemStack, "static-map", colors.toString());
        event.getInventory().setRepairCost(10);
        event.setResult(itemStack);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!itemStack.getType().equals(Material.FILLED_MAP)) {
            player.sendMessage(MessageUtils.getMessage("staticmap.notMap"));
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 4) {
                player.sendMessage("Args not enough!");
                player.sendMessage("/staticmap set [x] [y] [c]");
                return true;
            }
            setPixel(itemStack, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            updateStaticMap(itemStack, player);
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
            fillRectangle(itemStack, x1, y1, x2, y2, c);
            updateStaticMap(itemStack, player);
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
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                BufferedImage read = null;
                try {
                    read = ImageIO.read(new URL(url));
                } catch (IOException e) {
                    player.sendMessage("URL is invalid!");
                    return;
                }
                BufferedImage finalRead = read;
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    drawImage(itemStack, x, y, finalRead);
                    updateStaticMap(itemStack, player);
                });

            });
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
            drawText(itemStack, x, y, MinecraftFont.Font, text);
            updateStaticMap(itemStack, player);
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
