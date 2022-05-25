package com.molean.isletopia.modifier;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Singleton
@CommandAlias("LuckyColor")
public class LuckyColor extends BaseCommand implements Listener {

    public static Map<String, Color> colorMap = new HashMap<>();


    @AutoInject
    private UniversalParameter universalParameter;

    @EventHandler
    public void playerJoin(PlayerLoggedEvent event) {
        Tasks.INSTANCE.async(() -> {
            Player player = event.getPlayer();
            int r, g, b;
            String lastBumpReward = universalParameter.getParameter(player.getUniqueId(), "lastLuckyColor");
            if (lastBumpReward != null && !lastBumpReward.isEmpty() &&
                    LocalDate.parse(lastBumpReward, DateTimeFormatter.ofPattern("yyyy-MM-dd")).equals(LocalDate.now())) {
                String rString = universalParameter.getParameter(player.getUniqueId(), "luckyColor-R");
                String gString = universalParameter.getParameter(player.getUniqueId(), "luckyColor-G");
                String bString = universalParameter.getParameter(player.getUniqueId(), "luckyColor-B");
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
                universalParameter.setParameter(player.getUniqueId(), "luckyColor-R", r + "");
                universalParameter.setParameter(player.getUniqueId(), "luckyColor-G", g + "");
                universalParameter.setParameter(player.getUniqueId(), "luckyColor-B", b + "");
                String format = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                universalParameter.setParameter(player.getUniqueId(), "lastLuckyColor", format);

                ChatColor chatColor = ChatColor.of(new Color(r, g, b));
                String message = String.format(MessageUtils.getMessage(player, "luckycolor.today") + " #█§e(%d,%d,%d)#█", r, g, b);
                MessageUtils.notify(player, message.replaceAll("#", chatColor.toString()));
            }

            colorMap.put(player.getName(), new Color(r, g, b));

        });
    }


    @Default
    public void onDefault(Player player) {
        {
            Color color = colorMap.get(player.getName());
            ChatColor chatColor = ChatColor.of(color);
            String message = String.format(MessageUtils.getMessage(player, "luckycolor.today") + " #█§e(%d,%d,%d)#█", color.getRed(), color.getGreen(), color.getBlue());
            MessageUtils.notify(player, message.replaceAll("#", chatColor.toString()));
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().equals(Material.AIR)) {
            return;
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
    }
}
