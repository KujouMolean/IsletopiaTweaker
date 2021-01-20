package com.molean.isletopia.utils;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMSTagUtils {
    private static ItemStack append(ItemStack item, String key, NBTBase nbtBase) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        assert compound != null;
        compound.set(key, nbtBase);
        nmsItem.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static ItemStack append(ItemStack item, String key, int value) {
        return append(item, key, NBTTagInt.a(value));
    }

    public static ItemStack append(ItemStack item, String key, String value) {
        return append(item, key, NBTTagString.a(value));
    }

    public static ItemStack append(ItemStack item, String key, double value) {
        return append(item, key, NBTTagDouble.a(value));
    }

    public static NBTBase get(ItemStack item, String key) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : null;
        if (compound == null) {
            return null;
        }
        return compound.get(key);
    }

    public static Integer getInt(ItemStack item, String key) {
        NBTBase nbtBase = get(item, key);
        if (nbtBase == null) {
            return null;
        }
        return ((NBTTagInt) nbtBase).asInt();
    }

    public static String getString(ItemStack item, String key) {
        NBTBase nbtBase = get(item, key);
        if (nbtBase == null) {
            return null;
        }
        return nbtBase.asString();
    }

    public static Double getDouble(ItemStack item, String key) {
        NBTBase nbtBase = get(item, key);
        if (nbtBase == null) {
            return null;
        }
        return ((NBTTagDouble) nbtBase).asDouble();
    }
}
