package com.molean.isletopia.dialog;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class InformationDialog extends PlayerDialog {
    private final Component component;

    public InformationDialog(Component component) {
        this.component = component;
    }

    public InformationDialog(String text) {
        this.component = Component.text(text);
    }

    @Override
    public void open(Player player, String title, String author) {
        super.componentList.clear();
        super.componentList.add(component);
        super.open(player, title, author);
    }
}
