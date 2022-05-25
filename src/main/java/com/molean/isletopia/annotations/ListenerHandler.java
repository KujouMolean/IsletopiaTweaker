package com.molean.isletopia.annotations;

import com.molean.isletopia.shared.annotations.BeanHandler;
import com.molean.isletopia.shared.annotations.BeanHandlerPriority;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@BeanHandlerPriority(0)
public class ListenerHandler implements BeanHandler {
    private final JavaPlugin plugin;

    public ListenerHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(Object object) {
        if (object instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) object, plugin);
        }
    }
}
