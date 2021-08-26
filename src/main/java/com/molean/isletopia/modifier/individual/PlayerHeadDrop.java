package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.utils.BlockHeadUtils;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.LangUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.bukkit.Material.*;

public class PlayerHeadDrop implements Listener {
    public static final Map<EntityType, String> drops = new HashMap<>();
    public static Map<Material, Set<String>> blocks;


    public PlayerHeadDrop() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        blocks = BlockHeadUtils.getBlockHeadMap();

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
        itemMeta.displayName(Component.text("§f玩家头颅"));
        icon.setItemMeta(itemMeta);

        ItemStack type = new ItemStack(Material.CREEPER_HEAD);
        ItemMeta typeMeta = type.getItemMeta();
        typeMeta.displayName(Component.text("§f闪电苦力怕"));
        type.setItemMeta(typeMeta);

        ItemStack[] source = new ItemStack[9];
        for (int i = 0; i < source.length; i++) {
            source[i] = new ItemStack(Material.AIR);
        }
        source[4] = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta source4Meta = source[4].getItemMeta();
        source4Meta.displayName(Component.text("任意种类的生物被炸死"));
        source[4].setItemMeta(source4Meta);

        ItemStack result = icon.clone();
        LocalRecipe.addRecipe(icon, type, source, result);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Creeper creeper)) {
            return;
        }
        if (!creeper.isPowered()) {
            return;
        }
        if (creeper.hasMetadata("player-head")) {
            event.setCancelled(true);
        }

        if ("Creeper".equalsIgnoreCase(event.getEntity().getCustomName())) {
            for (Block block : event.blockList()) {
                if (block.getType().isBlock()) {
                    ArrayList<String> strings = new ArrayList<>(blocks.get(block.getType()));
                    String s = strings.get(new Random().nextInt(strings.size()));
                    if (s != null && !s.isEmpty()) {
                        ItemStack skull = HeadUtils.getSkullFromValue(block.getType().name(), s);
                        ItemMeta itemMeta = skull.getItemMeta();
                        itemMeta.displayName(null);
                        skull.setItemMeta(itemMeta);
                        event.getEntity().getWorld().dropItem(block.getLocation(), skull);
                        block.setType(AIR);
                        break;
                    }
                }
            }
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
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        if (livingEntity.getHealth() > event.getFinalDamage()) {
            return;
        }
        if (creeper.hasMetadata("player-head")) {
            event.setCancelled(true);
        } else if (livingEntity.getType().equals(EntityType.PLAYER)) {
            ItemStack skull = HeadUtils.getSkull(livingEntity.getName());
            livingEntity.getWorld().dropItem(livingEntity.getLocation(), skull);
        } else if (drops.containsKey(livingEntity.getType())) {
            String name = LangUtils.get(livingEntity.getType().name().toLowerCase());
            ItemStack skullFromValue = HeadUtils.getSkullFromValue(name, drops.get(livingEntity.getType()));
            ItemMeta itemMeta = skullFromValue.getItemMeta();
            itemMeta.displayName(null);
            skullFromValue.setItemMeta(itemMeta);
            livingEntity.getWorld().dropItem(livingEntity.getLocation(), skullFromValue);
        } else {
            return;
        }
        livingEntity.setHealth(0);
        creeper.setMetadata("player-head", new FixedMetadataValue(IsletopiaTweakers.getPlugin(), "true"));
    }


}
