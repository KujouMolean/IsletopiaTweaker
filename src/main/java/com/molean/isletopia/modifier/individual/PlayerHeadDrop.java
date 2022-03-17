package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.utils.BlockHeadUtils;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.shared.utils.LangUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.bukkit.Material.AIR;

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
        ItemStack type = new ItemStack(Material.CREEPER_HEAD);
        ItemMeta typeMeta = type.getItemMeta();
        typeMeta.displayName(Component.text("entity.creeper.lightning.name"));
        type.setItemMeta(typeMeta);

        ItemStack[] source = new ItemStack[9];
        for (int i = 0; i < source.length; i++) {
            source[i] = new ItemStack(Material.AIR);
        }
        source[4] = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta source4Meta = source[4].getItemMeta();
        source4Meta.displayName(Component.text("modification.playerHeadDrop.result"));
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
                        ItemStack itemStack = HeadUtils.getSkullFromValue(block.getType().name(), s);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.displayName(null);
                        itemStack.setItemMeta(itemMeta);
                        event.getEntity().getWorld().dropItem(block.getLocation(), itemStack);
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
            ItemStack itemStack = HeadUtils.getSkull(livingEntity.getName());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(null);
            itemStack.setItemMeta(itemMeta);
            livingEntity.getWorld().dropItem(livingEntity.getLocation(), itemStack);
        } else if (drops.containsKey(livingEntity.getType())) {
            String name = LangUtils.get(Locale.SIMPLIFIED_CHINESE, livingEntity.getType().translationKey());
            ItemStack itemStack = HeadUtils.getSkullFromValue(name, drops.get(livingEntity.getType()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(null);
            itemStack.setItemMeta(itemMeta);
            livingEntity.getWorld().dropItem(livingEntity.getLocation(), itemStack);

        } else {
            return;
        }
        livingEntity.setHealth(0);
        creeper.setMetadata("player-head", new FixedMetadataValue(IsletopiaTweakers.getPlugin(), "true"));
    }


}
