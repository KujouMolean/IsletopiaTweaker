package com.molean.isletopia.utils;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MaterialListUtil {
    public static List<Material> toMaterialList(List<String> strings) {
        ArrayList<Material> materials = new ArrayList<>();
        for (String string : strings) {
            Material material = Material.getMaterial(string.toUpperCase(Locale.ROOT));
            if (material != null) {
                materials.add(material);
            }
        }
        return materials;
    }

    public static List<String> toStringList(List<Material> materials) {
        ArrayList<String> strings = new ArrayList<>();
        for (Material material : materials) {
            strings.add(material.name());
        }
        return strings;
    }


}
