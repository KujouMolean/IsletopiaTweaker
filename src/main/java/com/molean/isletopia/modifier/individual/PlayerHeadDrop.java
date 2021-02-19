package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.LangUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

        ItemStack icon = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta itemMeta = icon.getItemMeta();
        itemMeta.setDisplayName("§f玩家头颅");
        icon.setItemMeta(itemMeta);

        ItemStack type = new ItemStack(Material.CREEPER_HEAD);
        ItemMeta typeMeta = type.getItemMeta();
        typeMeta.setDisplayName("§f闪电苦力怕");
        type.setItemMeta(typeMeta);

        ItemStack[] source = new ItemStack[9];
        for (int i = 0; i < source.length; i++) {
            source[i] = new ItemStack(Material.AIR);
        }
        source[4] = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta source4Meta = source[4].getItemMeta();
        source4Meta.setDisplayName("任意种类的生物被炸死");
        source[4].setItemMeta(source4Meta);

        ItemStack result = icon.clone();
        LocalRecipe.addRecipe(icon, type, source, result);


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
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) entity;
        if (livingEntity.getHealth() > event.getFinalDamage()) {
            return;
        }
        if (creeper.hasMetadata("player-head")) {
            event.setCancelled(true);
        } else if (livingEntity.getType().equals(EntityType.PLAYER)) {
            ItemStack skull = HeadUtils.getSkull(livingEntity.getName());
            livingEntity.getWorld().dropItem(livingEntity.getLocation(), skull);
        } else if (drops.containsKey(livingEntity.getType())) {
            String name = LangUtils.get("entity.minecraft." + livingEntity.getType().name().toLowerCase());
            ItemStack skullFromValue = HeadUtils.getSkullFromValue(name, drops.get(livingEntity.getType()));
            livingEntity.getWorld().dropItem(livingEntity.getLocation(), skullFromValue);
        } else {
            return;
        }
        livingEntity.setHealth(0);
        creeper.setMetadata("player-head", new FixedMetadataValue(IsletopiaTweakers.getPlugin(), "true"));
    }


}
