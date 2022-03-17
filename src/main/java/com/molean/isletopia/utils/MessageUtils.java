package com.molean.isletopia.utils;

import com.molean.isletopia.shared.utils.I18n;
import com.molean.isletopia.shared.utils.Pair;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class MessageUtils {

    @SafeVarargs
    public static String getMessage(Player player, String key, Pair<String, String>... pairs) {
        return I18n.getMessage(player.locale(), key, pairs);
    }

    public static void warn(Player player, String message) {
        player.sendMessage(Component.text(getMessage(player, "prefix.warn") + getMessage(player, message)), MessageType.SYSTEM);
    }

    public static void info(Player player, String message) {
        player.sendMessage(Component.text(getMessage(player, "prefix.info") + getMessage(player, message)), MessageType.SYSTEM);
    }

    public static void notify(Player player, String message) {
        player.sendMessage(Component.text(getMessage(player, "prefix.notify") + getMessage(player, message)), MessageType.SYSTEM);
    }

    public static void action(Player player, String message) {
        player.sendActionBar(Component.text("§a" + message));
    }

    public static void strong(Player player, String message) {
        player.sendMessage(Component.text(getMessage(player, "prefix.strong") + getMessage(player, message)), MessageType.SYSTEM);
    }

    public static void fail(Player player, String message) {
        player.sendMessage(Component.text(getMessage(player, "prefix.fail") + getMessage(player, message)), MessageType.SYSTEM);
    }

    public static void success(Player player, String message) {
        player.sendMessage(Component.text(getMessage(player, "prefix.success") + getMessage(player, message)), MessageType.SYSTEM);
    }

}
