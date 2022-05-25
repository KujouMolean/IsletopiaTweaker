package com.molean.isletopia.annotations;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@Bean
public class ListenerHandler implements BeanHandler {
    private final Plugin plugin;

    public ListenerHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(Object object) {
        if (object.getClass().isInstance(Listener.class)) {
            Bukkit.getPluginManager().registerEvents((Listener) object, plugin);
        }
    }
}
