package com.molean.isletopia.menu.settings.biome;

import org.bukkit.Material;

import java.util.List;

public class LocalBiome {
    private String id;
    private String name;
    private Material icon;
    private List<String> creatures;
    private List<String> environment;


    public LocalBiome(String id, String name, Material icon, List<String> creatures, List<String> environment) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.creatures = creatures;
        this.environment = environment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public List<String> getCreatures() {
        return creatures;
    }

    public void setCreatures(List<String> creatures) {
        this.creatures = creatures;
    }

    public List<String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(List<String> environment) {
        this.environment = environment;
    }
}
