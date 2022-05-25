package com.molean.isletopia.infrastructure;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.task.Tasks;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class MoreChairs implements Listener {

    private final PlayerPropertyManager playerPropertyManager;
    private static final Map<UUID, ArmorStand> map = new HashMap<>();
    private static final Map<UUID, Location> originLocation = new HashMap<>();
    private static final Map<UUID, Long> sneakTime = new HashMap<>();



    public MoreChairs(PlayerPropertyManager playerPropertyManager) {
        this.playerPropertyManager = playerPropertyManager;
    }

    @Interval(1)
    public void updateCharis() {
        map.forEach((uuid, armorStand) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            armorStand.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());
        });
    }


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
        if (playerPropertyManager.getPropertyAsBoolean(event.getPlayer(), "DisableChairs")) {

            return;
        }
        if (event.isSneaking()) {
            sneakTime.put(event.getPlayer().getUniqueId(), currentTimeMillis);
            Tasks.INSTANCE.timeout(20, () -> {
                if (sneakTime.getOrDefault(event.getPlayer().getUniqueId(), 0L).equals(currentTimeMillis)) {
                    Location location = event.getPlayer().getLocation();
                    Sound sound = Sound.ITEM_ARMOR_EQUIP_TURTLE;
                    event.getPlayer().playSound(location, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            });
            return;
        }
        long duration = currentTimeMillis - sneakTime.getOrDefault(event.getPlayer().getUniqueId(), currentTimeMillis);
        sneakTime.remove(event.getPlayer().getUniqueId());
        if (duration < 1000) {
            return;
        }

        if (!event.getPlayer().isOnGround()) {
            return;
        }

        Location location = event.getPlayer().getLocation().add(0, -1.7, 0);
        Entity entity = event.getPlayer().getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        ArmorStand armorStand = (ArmorStand) entity;
        originLocation.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
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
        map.put(event.getPlayer().getUniqueId(), armorStand);
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
                Location origin = originLocation.get(player.getUniqueId());
                Location location = player.getLocation();
                location.set(origin.getX(), origin.getY(), origin.getZ());
                player.teleport(location);
                map.remove(player.getUniqueId());
                dismounted.remove();
            }
        }
    }


    @EventHandler
    public void on(PlayerLoggedEvent event) {
        Entity vehicle = event.getPlayer().getVehicle();
        if (vehicle instanceof ArmorStand) {
            vehicle.eject();
            vehicle.remove();
            map.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Entity vehicle = event.getPlayer().getVehicle();
        if (vehicle instanceof ArmorStand) {
            vehicle.eject();
            vehicle.remove();
        }
        map.remove(event.getPlayer().getUniqueId());
        originLocation.remove(event.getPlayer().getUniqueId());
        sneakTime.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();
        if (plugin instanceof IsletopiaTweakers) {
            for (UUID uuid : map.keySet()) {
                ArmorStand armorStand = map.get(uuid);
                armorStand.eject();
                armorStand.remove();
            }
        }
    }
}
