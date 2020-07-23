package com.molean.isletopia.prompter.prompter;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public interface Prompter {
    void open();
    void handleInventoryCloseEvent(InventoryCloseEvent event);
    void handleInventoryClickEvent(InventoryClickEvent event);
    void onComplete(Consumer<String> supplier);
    void onEscape(Runnable runnable);
}
