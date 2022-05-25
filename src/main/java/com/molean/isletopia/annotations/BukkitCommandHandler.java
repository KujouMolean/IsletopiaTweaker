package com.molean.isletopia.annotations;

import com.molean.isletopia.shared.annotations.BeanHandler;
import com.molean.isletopia.shared.annotations.BeanHandlerPriority;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

@BeanHandlerPriority(0)
public class BukkitCommandHandler implements BeanHandler {
    @Override
    public void handle(Object object) {
        if (object.getClass().isAnnotationPresent(BukkitCommand.class)) {
            BukkitCommand annotation = object.getClass().getAnnotation(BukkitCommand.class);
            String value = annotation.value();
            PluginCommand pluginCommand = Bukkit.getPluginCommand(value);
            if (pluginCommand != null) {
                if (object instanceof CommandExecutor commandExecutor) {
                    pluginCommand.setExecutor(commandExecutor);
                }
                if (object instanceof TabCompleter tabCompleter) {
                    pluginCommand.setTabCompleter(tabCompleter);
                }
            }
        }
    }
}
