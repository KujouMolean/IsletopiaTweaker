package com.molean.isletopia.dialog;

import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ChestDialog extends ChestMenu implements IPlayerDialog {

    public ChestDialog(Player player, Component title, int row) {
        super(player, row, title);
    }


    public void confirmComponent(ItemStack itemStack) {

    }

    public void cancelComponent(ItemStack itemStack) {

    }

    @Override
    public void open() {
        super.open();
    }
}
