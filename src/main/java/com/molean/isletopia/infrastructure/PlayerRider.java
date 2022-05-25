package com.molean.isletopia.infrastructure;

import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class PlayerRider implements Listener {
    private final PlayerPropertyManager playerPropertyManager;
    private final Map<UUID, Long> sneakTime = new HashMap<>();

    public PlayerRider(PlayerPropertyManager playerPropertyManager) {
        this.playerPropertyManager = playerPropertyManager;
    }

    @Interval
    public void updateForce() {
        long currentTimeMillis = System.currentTimeMillis();
        for (UUID uuid : sneakTime.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline() || player.getPassengers().isEmpty()) {
                continue;
            }
            int diff = (int) (currentTimeMillis - sneakTime.get(uuid));
            if (diff < 0) diff = 0;
            if (diff > 10000) diff = 10000;

            int level = diff / 1000;
            String str =
                    "§a" + "◼".repeat(level) +
                            "§f" + "◼".repeat(10 - level);
            MessageUtils.action(player, str);
        }
    }




    @EventHandler(ignoreCancelled = true)
    public void on(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            sneakTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        } else {
            sneakTime.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        sneakTime.remove(event.getPlayer().getUniqueId());
    }


    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEntityEvent event) {
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getPlayer())) {
            return;
        }
        PlayerInventory inventory = event.getPlayer().getInventory();
        if (!inventory.getItemInMainHand().getType().equals(Material.AIR)) {
            return;
        }
        if (!inventory.getItemInOffHand().getType().equals(Material.AIR)) {
            return;
        }
        if (!event.getPlayer().isSneaking()) {
            return;
        }
        if (playerPropertyManager.getPropertyAsBoolean(event.getPlayer(), "DisablePlayerRide")) {

            return;
        }

        Entity rightClicked = event.getRightClicked();
        Entity source = event.getPlayer();
        while (!source.getPassengers().isEmpty()) {
            if (source.getPassengers().contains(rightClicked)) {
                return;
            }
            source = source.getPassengers().get(0);
        }
        source.addPassenger(rightClicked);
    }

    private void split(Entity entity, Vector velocity) {
        for (Entity passenger : entity.getPassengers()) {
            if (!(passenger instanceof Player)) {
                split(passenger, velocity);
            }
            Tasks.INSTANCE.sync(() -> {
                passenger.setVelocity(velocity);
            });
        }
        entity.eject();
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDismountEvent event) {
        Entity dismounted = event.getDismounted();
        if (dismounted instanceof Player player) {
            int diff = 5000;

            if (sneakTime.containsKey(player.getUniqueId())) {
                diff = (int) (System.currentTimeMillis() - sneakTime.get(player.getUniqueId()));
                if (diff < 0) diff = 0;
                if (diff > 10000) diff = 10000;
            }

            Vector multiply = player.getLocation().getDirection().clone().multiply(diff / 2000.0);
            split(dismounted, multiply);
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void on(PlayerIslandChangeEvent event) {
        event.getPlayer().eject();
    }


    @EventHandler(ignoreCancelled = true)
    public void on(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.getPassengers().isEmpty()) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().eject();
    }
}
