package com.molean.isletopia.infrastructure.individual;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Map;

public class MoreChairs implements Listener {

    public MoreChairs() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            map.forEach((player, armorStand) -> {
                armorStand.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());
            });
        }, 0, 1);
        IsletopiaTweakers.addDisableTask("Stop update chair position..", bukkitTask::cancel);
    }

    private static final Map<Player, ArmorStand> map = new HashMap<>();
    private static final Map<Player, Location> originLocation = new HashMap<>();
    private static final Map<Player, Long> sneakTime = new HashMap<>();


    @SuppressWarnings("deprecation")
    @EventHandler
    public void on(PlayerToggleSneakEvent event) {

        if (event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        PlayerInventory inventory = event.getPlayer().getInventory();
        if (!inventory.getItemInMainHand().getType().equals(Material.AIR)) {
            return;
        }
        if (!inventory.getItemInOffHand().getType().equals(Material.AIR)) {
            return;
        }
        if (event.getPlayer().getLocation().getPitch() < 75) {

            return;
        }
        if (event.isSneaking()) {
            sneakTime.put(event.getPlayer(), currentTimeMillis);
            Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
                if (sneakTime.getOrDefault(event.getPlayer(), 0L).equals(currentTimeMillis)) {
                    Location location = event.getPlayer().getLocation();
                    Sound sound = Sound.ITEM_ARMOR_EQUIP_TURTLE;
                    event.getPlayer().playSound(location, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }, 20L);
            return;
        }
        long duration = currentTimeMillis - sneakTime.getOrDefault(event.getPlayer(), currentTimeMillis);
        sneakTime.remove(event.getPlayer());
        if (duration < 1000) {
            return;
        }

        if (!event.getPlayer().isOnGround()) {
            return;
        }

            Location location = event.getPlayer().getLocation().add(0, -1.7, 0);
            Entity entity = event.getPlayer().getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            ArmorStand armorStand = (ArmorStand) entity;
            originLocation.put(event.getPlayer(), event.getPlayer().getLocation());
            armorStand.addPassenger(event.getPlayer());
            armorStand.setGravity(false);
            armorStand.setCanMove(false);
            armorStand.setInvulnerable(true);
            armorStand.setAI(false);
            armorStand.setArms(false);
            armorStand.setCanTick(false);
            armorStand.setVisualFire(false);
            armorStand.setDisabledSlots(EquipmentSlot.values());
            armorStand.setVisible(false);
            map.put(event.getPlayer(), armorStand);
    }

    @EventHandler
    public void on(EntityAddToWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.ARMOR_STAND)) {
            ArmorStand armorStand = (ArmorStand) entity;
            if (!armorStand.isVisible()) {
                if (armorStand.getPassengers().isEmpty()) {
                    armorStand.remove();
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity.getType().equals(EntityType.ARMOR_STAND)) {
            ArmorStand armorStand = (ArmorStand) entity;
            if (!armorStand.isVisible()) {
                if (armorStand.getPassengers().isEmpty()) {
                    armorStand.remove();
                }
            }
        }
    }

    @EventHandler
    public void on(EntityDismountEvent event) {
        Entity dismounted = event.getDismounted();
        if (dismounted.getType().equals(EntityType.ARMOR_STAND)) {
            if (event.getEntity() instanceof Player player) {
                Location origin = originLocation.get(player);
                Location location = player.getLocation();
                location.set(origin.getX(), origin.getY(), origin.getZ());
                player.teleport(location);
                map.remove(player);
                dismounted.remove();
            }
        }
    }


    @EventHandler
    public void on(PlayerDataSyncCompleteEvent event) {
        Entity vehicle = event.getPlayer().getVehicle();
        if (vehicle instanceof ArmorStand) {
            vehicle.eject();
            vehicle.remove();
            map.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Entity vehicle = event.getPlayer().getVehicle();
        if (vehicle instanceof ArmorStand) {
            vehicle.eject();
            vehicle.remove();
            map.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void on(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        if (plugin instanceof IsletopiaTweakers) {
            for (Player player : map.keySet()) {
                ArmorStand armorStand = map.get(player);
                armorStand.eject();
                armorStand.remove();
            }
        }
    }
}
