package com.molean.isletopia.parameter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ParameterCommand implements CommandExecutor {

    public ParameterCommand() {
        Bukkit.getPluginCommand("parameter").setExecutor(this);
        Bukkit.getPluginCommand("parameterAdmin").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0)
            return false;
        String opt = args[0].toLowerCase();
        String cmd = command.getName().toLowerCase();
        String target = null, key = null, value = null;

        if (cmd.equalsIgnoreCase("parameterAdmin")) {
            if (!sender.isOp()) {
                return false;
            }
            if (args.length < 2)
                return false;

            target = args[1];
            if (args.length >= 3) {
                key = args[2];
            }
            if (args.length >= 4) {
                value = args[3];
            }
        } else if (cmd.equalsIgnoreCase("parameter")) {
            target = sender.getName();
            if (args.length >= 2) {
                key = args[1];
            }
            if (args.length >= 3) {
                value = args[2];
            }
        }

        switch (opt) {
            case "set":
                if (target != null && key != null && value != null) {
                    UniversalParameter.setParameter(target, key, value);
                }
                break;
            case "unset":
                if (target != null && key != null) {
                    UniversalParameter.unsetParameter(target, key);
                }
                break;
            case "view":
                if (target != null && key != null) {
                    String s = UniversalParameter.getParameter(target, key);
                    sender.sendMessage(s);
                }
                break;
            case "add":
                if (target != null && key != null && value != null) {
                    UniversalParameter.addParameter(target, key, value);
                }
                break;
            case "remove":
                if (target != null && key != null && value != null) {
                    UniversalParameter.removeParameter(target, key, value);
                }
                break;
        }
        return false;
    }
}

