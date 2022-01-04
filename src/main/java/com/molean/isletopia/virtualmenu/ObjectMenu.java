package com.molean.isletopia.virtualmenu;

import com.molean.isletopia.shared.utils.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ObjectMenu extends ChestMenu {

    private final List<Pair<ItemStack, Runnable>> syncTasks = new ArrayList<>();
    private final int maxSize;

    public ObjectMenu(Player player, int rows, Component title) {
        super(player, rows, title);
        this.maxSize = rows * 9;
    }

    public void addItem(ItemStack itemStack, Runnable syncTask) {
        syncTasks.add(new Pair<>(itemStack, syncTask));
    }

    @Override
    public void onLeftClick(int slot) {
        super.onLeftClick(slot);
        if (slot < 0) {
            return;
        }
        if (slot >= syncTasks.size()) {
            return;
        }
        syncTasks.get(slot).getValue().run();
    }

    @Override
    public void beforeOpen() {
        super.beforeOpen();
        for (int i = 0; i < syncTasks.size() && i < maxSize; i++) {
            item(i, syncTasks.get(i).getKey());
        }
    }
}
