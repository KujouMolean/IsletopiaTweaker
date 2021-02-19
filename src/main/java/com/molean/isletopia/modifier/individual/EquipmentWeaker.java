package com.molean.isletopia.modifier.individual;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.molean.isletopia.IsletopiaTweakers;
import net.craftersland.data.bridge.PD;
import net.craftersland.data.bridge.api.API;
import net.craftersland.data.bridge.api.events.SyncCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
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
    public void onSyncComplete(SyncCompleteEvent event) {
        syncComplete.put(event.getPlayer(), false);
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
            syncComplete.put(event.getPlayer(), true);
        }, 20L);
    }

    @EventHandler

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
                String format = String.format("§8[§3温馨提示§8] §e你更新了装备, 受到的伤害被修正为: %.2fx", damageMultiplier);
                event.getPlayer().sendMessage(format);
            }

        }, 50L);


    }
}
