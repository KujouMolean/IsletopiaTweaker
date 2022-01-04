package com.molean.isletopia.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackSheet {
    private final Material material;
    private String display = null;
    private List<Component> lores = null;
    private int amount = 1;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private final List<ItemFlag> itemFlags = new ArrayList<>();


    public static ItemStackSheet fromString(Material material, String titleAndContent) {
        ItemStackSheet itemStackSheet = new ItemStackSheet(material);
        String[] split = titleAndContent.split("\n");
        if (split.length >= 1) {
            itemStackSheet.setDisplay(split[0]);
        }
        for (int i = 1; i < split.length; i++) {
            itemStackSheet.addLore(split[i]);
        }
        return itemStackSheet;
    }

    public static ItemStackSheet fromString(Material material, String titleAndContent, Object... args) {
        return fromString(material, titleAndContent.formatted(args));
    }

    public ItemStackSheet(Material material) {
        this.material = material;
    }

    public ItemStackSheet(Material material, String display) {
        this.material = material;
        this.display = display;
    }

    public ItemStackSheet(Material material, String display, int amount) {
        this.material = material;
        this.display = display;
        this.amount = amount;
    }

    public void addLore(String lore) {
        if(lores == null){
            lores = new ArrayList<>();
        }
        lores.add(Component.text(lore));
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
    }

    public void addItemFlag(ItemFlag itemFlag) {
        itemFlags.add(itemFlag);
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        if (display != null) {

            itemMeta.displayName(Component.text(display));
        }
        itemMeta.lore(lores);
        for (ItemFlag itemFlag : itemFlags) {
            itemMeta.addItemFlags(itemFlag);
        }
        itemStack.setItemMeta(itemMeta);

        for (Enchantment enchantment : enchantments.keySet()) {
            Integer level = enchantments.get(enchantment);
            itemStack.addUnsafeEnchantment(enchantment, level);
        }
        return itemStack;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
