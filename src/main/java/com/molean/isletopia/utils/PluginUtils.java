package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class PluginUtils {
    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, IsletopiaTweakers.getPlugin());
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static Logger getLogger() {
        return IsletopiaTweakers.getPlugin().getLogger();
    }
}
