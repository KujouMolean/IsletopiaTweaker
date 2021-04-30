package com.molean.isletopia.utils;


import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSTagUtils {
    private static Method asNMSCopyMethod ;
    private static Method asBukkitCopyMethod ;
    private static Method getTagMethod;
    private static Method setTagMethod ;
    private static Method setStringMethod;
    private static Method getStringMethod ;
    private static Method hasKey;

    static {
        try {
            Class<?> craftItemClass = NMSUtils.getBukkitClass("inventory.CraftItemStack");
            Class<?> nbtTagCompoundClass = NMSUtils.getNMSClass("NBTTagCompound");
            Class<?> nmsItemClass = NMSUtils.getNMSClass("ItemStack");
            asNMSCopyMethod = craftItemClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
            asBukkitCopyMethod = nmsItemClass.getDeclaredMethod("asBukkitCopy");
            getTagMethod = nmsItemClass.getDeclaredMethod("getOrCreateTag");
            setTagMethod = nmsItemClass.getDeclaredMethod("setTag", nbtTagCompoundClass);
            setStringMethod = nbtTagCompoundClass.getDeclaredMethod("setString", String.class, String.class);
            getStringMethod = nbtTagCompoundClass.getDeclaredMethod("getString", String.class);
            hasKey = nbtTagCompoundClass.getDeclaredMethod("hasKey", String.class);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static ItemStack set(ItemStack item, String key, String value) {
        try {
            Object nmsItem = asNMSCopyMethod.invoke(null, item);
            Object compound = getTagMethod.invoke(nmsItem);
            setStringMethod.invoke(compound, key, value);
            setTagMethod.invoke(nmsItem, compound);
            return (ItemStack) asBukkitCopyMethod.invoke(nmsItem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static String get(ItemStack item, String key) {
        try {
            Object nmsItem = asNMSCopyMethod.invoke(null, item);
            Object compound = getTagMethod.invoke(nmsItem);
            if ((Boolean) hasKey.invoke(compound, key)) {
                return (String) getStringMethod.invoke(compound, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
