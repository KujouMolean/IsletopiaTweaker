package com.molean.isletopia.parameter;

import com.molean.isletopia.network.MessageUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParameterCommand implements CommandExecutor {
    String[] subCommand = {"set", "unset", "view", "add", "remove"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0)
            return false;
        String cmd = args[0].toLowerCase();
        String key = null, value = null;
        if (args.length >= 2) {
            if (sender instanceof Player)
                key = PlaceholderAPI.setPlaceholders((Player) sender, args[1]);
            else key = args[1];
        }
        if (args.length >= 3) {
            if (sender instanceof Player)
                value = PlaceholderAPI.setPlaceholders((Player) sender, args[2]);
            else value = args[2];
        }
        switch (cmd) {
            case "set":
                if (args.length == 3) {
                    MessageUtils.setParameter(sender.getName(), key, value);
                    return true;
                } else {
                    return false;
                }
            case "unset":
                if (sender instanceof Player)
                    key = PlaceholderAPI.setPlaceholders((Player) sender, args[1]);
                else key = args[1];
                if (args.length == 2) {
                    MessageUtils.unsetParameter(sender.getName(), key);
                    return true;
                } else {
                    return false;
                }
            case "view":
                if (sender instanceof Player)
                    key = PlaceholderAPI.setPlaceholders((Player) sender, args[1]);
                else key = args[1];
                if (args.length == 2) {
                    String s = MessageUtils.getParameter(sender.getName(), key);
                    if (s == null)
                        return true;
                    sender.sendMessage(s.toString());
                    return true;
                } else {
                    return false;
                }
            case "add":
                if (args.length == 3) {
                    MessageUtils.addParameter(sender.getName(), key,value);
                    return true;
                } else {
                    return false;
                }
            case "remove":
                if (args.length == 3) {
                    MessageUtils.removeParameter(sender.getName(), key, value);
                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }
}

