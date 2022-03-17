package com.molean.isletopia.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackSheet {
    private final ItemStack itemStack;
    private String display = null;
    private List<Component> lores = null;
    private int amount = 1;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private final List<ItemFlag> itemFlags = new ArrayList<>();


    public static ItemStackSheet fromString(ItemStack itemStack, String titleAndContent) {
        ItemStackSheet itemStackSheet = new ItemStackSheet(itemStack);
        String[] split = titleAndContent.split("\n");
        if (split.length >= 1) {
            itemStackSheet.display(split[0]);
        }
        for (int i = 1; i < split.length; i++) {
            itemStackSheet.addLore(split[i]);
        }
        return itemStackSheet;
    }
    public static ItemStackSheet fromString(Material material, String titleAndContent) {
        return ItemStackSheet.fromString(new ItemStack(material), titleAndContent);
    }

    public static ItemStackSheet fromString(Material material, String titleAndContent, Object... args) {
        return fromString(material, titleAndContent.formatted(args));
    }

    public static ItemStackSheet fromString(ItemStack itemStack, String titleAndContent, Object... args) {
        return fromString(itemStack, titleAndContent.formatted(args));
    }

    public ItemStackSheet(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    public ItemStackSheet(Material material) {
        this(material, null);
    }
    public ItemStackSheet(Material material, String display) {
        this(material, display, 1);
    }

    public ItemStackSheet(Material material, String display, int amount) {
        this.itemStack = new ItemStack(material);
        this.display = display;
        this.amount = amount;
    }

    public ItemStackSheet addLore(String lore) {
        if(lores == null){
            lores = new ArrayList<>();
        }
        lores.add(Component.text(lore));
        return this;
    }

    public ItemStackSheet addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemStackSheet addItemFlag(ItemFlag itemFlag) {
        itemFlags.add(itemFlag);
        return this;
    }

    public ItemStack build() {

        itemStack.setAmount(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (display != null) {

                itemMeta.displayName(Component.text(display));
            }
            itemMeta.lore(lores);
            for (ItemFlag itemFlag : itemFlags) {
                itemMeta.addItemFlags(itemFlag);
            }
            itemStack.setItemMeta(itemMeta);

        }

        for (Enchantment enchantment : enchantments.keySet()) {
            Integer level = enchantments.get(enchantment);
            itemStack.addUnsafeEnchantment(enchantment, level);
        }
        return itemStack;
    }

    public String display() {
        return display;
    }

    public ItemStackSheet display(String display) {
        this.display = display;
        return this;
    }
}
