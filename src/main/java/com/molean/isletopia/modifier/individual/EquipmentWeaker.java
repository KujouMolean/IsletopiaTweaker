package com.molean.isletopia.modifier.individual;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ArmoredHorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class EquipmentWeaker implements Listener {
    public EquipmentWeaker() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    private double getDamageMultiplier(Player player) {
        double multiplier = 1;
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (ItemStack armorContent : armorContents) {
            if (armorContent == null) {
                continue;
            }
            short maxDurability = armorContent.getType().getMaxDurability();
            if (maxDurability < 10) {
                continue;
            }
            multiplier *= Math.log(maxDurability) / Math.log(125);

            ItemMeta itemMeta = armorContent.getItemMeta();
            if (itemMeta instanceof LeatherArmorMeta && LuckyColor.colorMap.get(player.getName())!=null) {
                Color color = ((LeatherArmorMeta) itemMeta).getColor();
                int diff = 0;
                int luckyRed = LuckyColor.colorMap.get(player.getName()).getRed();
                int luckyGreen = LuckyColor.colorMap.get(player.getName()).getGreen();
                int luckyBlue = LuckyColor.colorMap.get(player.getName()).getBlue();
                diff += Math.abs(luckyRed - color.getRed());
                diff += Math.abs(luckyGreen - color.getGreen());
                diff += Math.abs(luckyBlue - color.getBlue());

                Map<Enchantment, Integer> enchantments = armorContent.getEnchantments();

                if (diff < 256) {
                    if (enchantments.isEmpty()) {

                        multiplier = (1 - (0.50) * (256.0 - diff) / 256.0) * multiplier;
                    }else{
                        multiplier = (1 - ((0.50) * (256.0 - diff) / 256.0) * 0.5) * multiplier;
                    }
                }
            }
        }
        return multiplier;
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.PLAYER)) {
            return;
        }
        Player player = (Player) entity;
        double damage = event.getDamage();
        event.setDamage(damage * getDamageMultiplier(player));
    }

    private static final Map<OfflinePlayer, UUID> map = new HashMap<>();
    private static final Map<OfflinePlayer, Boolean> syncComplete = new HashMap<>();

    @EventHandler
    public void onSyncComplete(PlayerDataSyncCompleteEvent event) {
        syncComplete.put(event.getPlayer(), false);
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
            syncComplete.put(event.getPlayer(), true);
        }, 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerArmorChangeEvent event) {
        if (!syncComplete.getOrDefault(event.getPlayer(), false)) {
            return;
        }

        if (event.getOldItem() != null && event.getNewItem() != null) {
            if (event.getOldItem().getType().equals(event.getNewItem().getType())) {
                return;
            }
        }
        UUID uuid = UUID.randomUUID();
        map.put(event.getPlayer(), uuid);
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), (task) -> {
            if (uuid == map.get(event.getPlayer())) {
                double damageMultiplier = getDamageMultiplier(event.getPlayer());
                String format = String.format("你更新了装备, 受到的伤害被修正为: %.2fx", damageMultiplier);
                MessageUtils.notify(event.getPlayer(), format);
            }

        }, 50L);


    }


}
