package com.molean.isletopia.utils;

import com.molean.isletopia.task.Tasks;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ThreadSafeUtils {
    public static void kick(Player player, String why) {
        Tasks.INSTANCE.sync(() -> player.kick(Component.text(why)));
    }
}
