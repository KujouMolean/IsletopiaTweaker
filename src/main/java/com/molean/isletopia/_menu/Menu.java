package com.molean.isletopia._menu;

import org.bukkit.entity.Player;

import java.util.List;

public class Menu {
    private String namespace;
    private String name;

    private final List<Component> components;

    public Menu(List<Component> components) {
        this.components = components;
    }

    public void open(Player player) {

    }
}
