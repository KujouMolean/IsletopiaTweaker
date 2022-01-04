package com.molean.isletopia.virtualmenu.internal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class AbstractChestMenu extends InventoryMenu {

    public AbstractChestMenu(Player player, int rows, Component title) {
        super(player, rows * 9, title);
    }
}
