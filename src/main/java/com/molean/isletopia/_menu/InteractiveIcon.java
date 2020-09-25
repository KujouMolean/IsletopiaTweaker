package com.molean.isletopia._menu;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class InteractiveIcon extends FullDisplayIcon {

    public InteractiveIcon() {
    }

    @Override
    public Component build(Player player) {
        return super.build(player);
    }

    @Override
    public ComponentSheet parse(ConfigurationSection section) {
        return super.parse(section);
    }
}
