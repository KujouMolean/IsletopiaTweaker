package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.shared.utils.UUIDManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ParameterCommand implements CommandExecutor {

    public ParameterCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("parameter")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0)
            return false;
        String opt = args[0].toLowerCase();
        String cmd = command.getName().toLowerCase();
        String target = null, key = null, value = null;
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

        switch (opt) {
            case "set":
                if (target != null && key != null && value != null) {
                    UniversalParameter.setParameter(UUIDManager.get(target), key, value);
                }
                break;
            case "unset":
                if (target != null && key != null) {
                    UniversalParameter.unsetParameter(UUIDManager.get(target), key);
                }
                break;
            case "view":
                if (target != null && key != null) {
                    String s = UniversalParameter.getParameter(UUIDManager.get(target), key);
                    assert s != null;
                    sender.sendMessage(s);
                }
                break;
            case "add":
                if (target != null && key != null && value != null) {
                    UniversalParameter.addParameter(Objects.requireNonNull(UUIDManager.get(target)), key, value);

                }
                break;
            case "remove":
                if (target != null && key != null && value != null) {
                    UniversalParameter.removeParameter(UUIDManager.get(target), key, value);
                }
                break;
        }
        return false;
    }
}

