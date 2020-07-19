package com.molean.isletopiatweakers;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IsletopiaUtils implements CommandExecutor, TabCompleter {
    private static final String[] subcmds = {"maxplayer"};

    public IsletopiaUtils(){
        Bukkit.getPluginCommand("utils").setExecutor(this);
        Bukkit.getPluginCommand("utils").setTabCompleter(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1)
            return false;
        String subcmd = args[0].toLowerCase();
        switch (subcmd) {
            case "maxplayer":
                if (args.length >= 2) {
                    int v = Integer.parseInt(args[1]);
                    try {
                        Object playerlist = org.bukkit.Bukkit.getServer().getClass().getDeclaredMethod("getHandle").invoke(org.bukkit.Bukkit.getServer());
                        Field maxPlayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
                        maxPlayers.setAccessible(true);
                        maxPlayers.set(playerlist, v);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 1) {
            for (String subcmd : subcmds) {
                if (subcmd.startsWith(args[0])) {
                    strings.add(subcmd);
                }
            }
        }
        return strings;
    }
}
