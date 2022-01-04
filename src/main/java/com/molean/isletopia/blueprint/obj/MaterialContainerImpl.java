package com.molean.isletopia.blueprint.obj;

import org.bukkit.Material;

import java.util.HashMap;

public class MaterialContainerImpl {
    private final HashMap<Material, Integer> storedMaterial;
    private final int templateId;

    public int getTemplateId() {
        return templateId;
    }

    public HashMap<Material, Integer> getStoredMaterial() {
        return storedMaterial;
    }

    public MaterialContainerImpl(int templateId) {
        this.templateId = templateId;
        storedMaterial = new HashMap<>();
    }


    public void storeMaterial(Material material, int amount) {
        Integer orDefault = storedMaterial.getOrDefault(material, 0);
        storedMaterial.put(material, orDefault + amount);
    }


    public int getStore(Material material) {
        return storedMaterial.getOrDefault(material, 0);
    }


}
