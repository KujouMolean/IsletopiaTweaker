package com.molean.isletopia.virtualmenu.internal;

import com.molean.isletopia.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class AbstractChestMenu extends InventoryMenu {

    public AbstractChestMenu(Player player, int rows, Component title) {
        super(player, rows * 9, title);
        if (Bukkit.isPrimaryThread()) {
            PluginUtils.getLogger().warning("Create menu " + ((TextComponent) title).content() + " in main thread.");
        }
    }

}
