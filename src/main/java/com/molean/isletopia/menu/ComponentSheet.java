package com.molean.isletopia.menu;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface ComponentSheet {
    Component build(Player player);

    ComponentSheet parse(ConfigurationSection text);
}
