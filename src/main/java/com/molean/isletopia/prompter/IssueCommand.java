package com.molean.isletopia.prompter;

import com.molean.isletopia.network.UniversalParameter;
import com.molean.isletopia.prompter.prompter.CommandPrompts;
import com.molean.isletopia.prompter.util.StringUtils;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IssueCommand implements CommandExecutor, TabCompleter {

    public IssueCommand() {
        Bukkit.getPluginCommand("sudo").setExecutor(this);
        Bukkit.getPluginCommand("sudo").setTabCompleter(this);
        Bukkit.getPluginCommand("issue").setExecutor(this);
        Bukkit.getPluginCommand("issue").setTabCompleter(this);
    }

    private static String parse(String sender, String str) {
        while (true) {
            String surroundedString = StringUtils.getSurroundedString(str, "${", "}");
            if (surroundedString == null)
                return PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(sender), str);
            String[] split = surroundedString.split(",");
            Object o = UniversalParameter.getParameter(sender, split[0]);
            String value = null;
            if (o != null) {
                value = o.toString();
            }
            if (value == null || "".equals(value)) {
                if (split.length > 1) {
                    value = parse(sender, split[1]);
                } else value = "";

            }
            str = str.replace("${" + surroundedString + "}", value);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            StringBuilder cmd = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                args[i] = parse(sender.getName(), args[i]);
                cmd.append(args[i]).append(" ");
            }

            if (cmd.toString().indexOf('@') >= 0) {
                new CommandPrompts(args, (Player) sender, command.getName().equalsIgnoreCase("sudo")).handle();
                return;
            }
            boolean isOp = sender.isOp();
            try {
                if (command.getName().equalsIgnoreCase("sudo")) {
                    if (sender instanceof Player)
                        sender.setOp(true);
                }
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    Bukkit.getServer().dispatchCommand(sender, cmd.toString());
                });

            } finally {
                if (command.getName().equalsIgnoreCase("sudo")) {
                    if (sender instanceof Player)
                        sender.setOp(isOp);
                }
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
