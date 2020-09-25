package com.molean.isletopia._menu;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FullDisplayIcon implements ComponentSheet {
    private Material material;
    private String display = null;
    private List<String> lores = null;
    private int amount = 1;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private List<ItemFlag> itemFlags = new ArrayList<>();


    private void reset() {
        material = null;
        display = null;
        lores = null;
        amount = 1;
        enchantments = new HashMap<>();
        itemFlags = new ArrayList<>();
    }

    @Override
    public Component build(Player player) {
        Component component = new Component(player, 1, 1, 1, 20);
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(display);
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
        for (ItemFlag itemFlag : itemFlags) {
            itemMeta.addItemFlags(itemFlag);
        }
        for (Enchantment enchantment : enchantments.keySet()) {
            Integer level = enchantments.get(enchantment);
            itemStack.addEnchantment(enchantment, level);
        }
        component.setItemStack(itemStack);
        return component;
    }


    @Override
    public ComponentSheet parse(ConfigurationSection section) {
        reset();
        Set<String> keys = section.getKeys(false);
        if (keys.contains("material")) {
            String material = section.getString("material");
            this.material = Material.valueOf(material);
        } else {
            return null;
        }

        if (keys.contains("display")) {
            this.display = section.getString("display");
        }

        if (keys.contains("amount")) {
            this.amount = section.getInt("amount");
        }

        if (keys.contains("lores")) {
            this.lores = new ArrayList<>(section.getStringList("lores"));
        }

        if (keys.contains("flags")) {
            List<String> flags = section.getStringList("flags");
            for (String flag : flags) {
                itemFlags.add(ItemFlag.valueOf(flag));
            }
        }

        if (keys.contains("enchantments")) {
            ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
            if (enchantmentsSection == null) {
                reset();
                return null;
            }
            Set<String> enchantmentKeys = enchantmentsSection.getKeys(false);
            for (String enchantmentKey : enchantmentKeys) {
                Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantmentKey));
                int level = enchantmentsSection.getInt(enchantmentKey);
                enchantments.put(enchantment, level);
            }
        }

        return this;
    }


}
