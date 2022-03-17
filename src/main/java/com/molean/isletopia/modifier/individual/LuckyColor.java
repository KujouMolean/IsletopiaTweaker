package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public  class  LuckyColor implements Listener, CommandExecutor {

    public LuckyColor() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("luckycolor")).setExecutor(this);
    }

    public  static Map<String, Color> colorMap = new HashMap<>();

    @EventHandler
    public void playerJoin(PlayerDataSyncCompleteEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Player player = event.getPlayer();
            int r, g, b;
            String lastBumpReward = UniversalParameter.getParameter(player.getUniqueId(), "lastLuckyColor");
            if (lastBumpReward != null && !lastBumpReward.isEmpty() &&
                    LocalDate.parse(lastBumpReward, DateTimeFormatter.ofPattern("yyyy-MM-dd")).equals(LocalDate.now())) {
                String rString = UniversalParameter.getParameter(player.getUniqueId(), "luckyColor-R");
                String gString = UniversalParameter.getParameter(player.getUniqueId(), "luckyColor-G");
                String bString = UniversalParameter.getParameter(player.getUniqueId(), "luckyColor-B");
                assert rString != null;
                r = Integer.parseInt(rString);
                assert gString != null;
                g = Integer.parseInt(gString);
                assert bString != null;
                b = Integer.parseInt(bString);
            } else {
                Random random = new Random();
                r = random.nextInt(256);
                g = random.nextInt(256);
                b = random.nextInt(256);
                UniversalParameter.setParameter(player.getUniqueId(), "luckyColor-R", r + "");
                UniversalParameter.setParameter(player.getUniqueId(), "luckyColor-G", g + "");
                UniversalParameter.setParameter(player.getUniqueId(), "luckyColor-B", b + "");
                String format = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                UniversalParameter.setParameter(player.getUniqueId(), "lastLuckyColor", format);

                ChatColor chatColor = ChatColor.of(new Color(r, g, b));
                String message = String.format(MessageUtils.getMessage(player, "luckycolor.today") + " #█§e(%d,%d,%d)#█", r, g, b);
                MessageUtils.notify(player, message.replaceAll("#", chatColor.toString()));
            }

            colorMap.put(player.getName(), new Color(r, g, b));

        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        {
            Color color = colorMap.get(player.getName());
            ChatColor chatColor = ChatColor.of(color);
            String message = String.format(MessageUtils.getMessage(player, "luckycolor.today") + " #█§e(%d,%d,%d)#█", color.getRed(), color.getGreen(), color.getBlue());
            MessageUtils.notify(player, message.replaceAll("#", chatColor.toString()));
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().equals(Material.AIR)) {
            return true;
        }
        ItemMeta itemMeta = itemInMainHand.getItemMeta();
        if (itemMeta instanceof LeatherArmorMeta) {
            org.bukkit.Color color = ((LeatherArmorMeta) itemMeta).getColor();
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();

            ChatColor chatColor = ChatColor.of(new Color(r, g, b));
            String format = String.format(MessageUtils.getMessage(player, "luckycolor.hand") + " #█§e(%d,%d,%d)#█", r, g, b);
            MessageUtils.notify(player, format.replaceAll("#", chatColor.toString()));
        }
        return true;
    }
}
