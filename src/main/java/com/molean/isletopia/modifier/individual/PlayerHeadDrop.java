package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.LangUtils;
import com.molean.isletopia.utils.NMSTagUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PlayerHeadDrop implements Listener {
    private static final Map<EntityType, String> drops = new HashMap<>();

    public PlayerHeadDrop() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Properties properties = null;
        try {
            InputStream inputStream = PlayerHeadDrop.class.getClassLoader().getResourceAsStream("MobSkull.properties");
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            drops.put(EntityType.valueOf(key), value);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {

        if (!event.getDamager().getType().equals(EntityType.CREEPER)) {
            return;
        }
        Creeper creeper = (Creeper) event.getDamager();
        if (!creeper.isPowered()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Creature)) {
            return;
        }
        Creature creature = (Creature) entity;
        if (creature.getHealth() > event.getFinalDamage()) {
            return;
        }
        if (creeper.hasMetadata("player-head")) {
            event.setCancelled(true);
        } else if (creature instanceof Player) {
            ItemStack skull = HeadUtils.getSkull(creature.getName());
            creature.getWorld().dropItem(creature.getLocation(), skull);
        } else if (drops.containsKey(creature.getType())) {
            String name = LangUtils.get("entity.minecraft." + creature.getType().name().toLowerCase());
            ItemStack skullFromValue = HeadUtils.getSkullFromValue(name, drops.get(creature.getType()));
            creature.getWorld().dropItem(creature.getLocation(), skullFromValue);
        } else {
            return;
        }
        creature.setHealth(0);
        creeper.setMetadata("player-head", new FixedMetadataValue(IsletopiaTweakers.getPlugin(), "true"));
    }


}
