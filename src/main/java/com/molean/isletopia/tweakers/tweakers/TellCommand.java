package com.molean.isletopia.tweakers.tweakers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TellCommand implements CommandExecutor, TabCompleter {
    public TellCommand() {
        Bukkit.getPluginCommand("tell").setExecutor(this);
        Bukkit.getPluginCommand("tell").setTabCompleter(this);
        Bukkit.getPluginCommand("msg").setExecutor(this);
        Bukkit.getPluginCommand("msg").setTabCompleter(this);
        Bukkit.getPluginCommand("w").setTabCompleter(this);
        Bukkit.getPluginCommand("w").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length < 2)
            return true;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            StringBuilder rawMessage = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                rawMessage.append(args[i]).append(" ");
            }
            String message = "§7" + player.getName() + " -> " + args[0] + ": " + rawMessage;

            if (!IsletopiaTweakers.getOnlinePlayers().contains(args[0])) {
                return;
            }

            try {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("ForwardToPlayer");
                out.writeUTF(args[0]);
                out.writeUTF("tell");
                ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                DataOutputStream msgout = new DataOutputStream(msgbytes);
                msgout.writeUTF(message);
                out.writeShort(msgbytes.toByteArray().length);
                out.write(msgbytes.toByteArray());
                player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
                player.sendMessage(message);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = IsletopiaTweakers.getOnlinePlayers();
            playerNames.removeIf(s -> !s.startsWith(args[0]));
            return playerNames;
        } else {
            return new ArrayList<>();
        }
    }
}