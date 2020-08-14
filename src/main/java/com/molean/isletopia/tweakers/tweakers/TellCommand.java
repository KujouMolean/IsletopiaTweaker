package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
import com.molean.isletopia.network.Response;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
            Request request = new Request("dispatcher", "sendMessage");
            request.set("target", args[0]);
            request.set("message", message);
            Response response = Client.send(request);
            if (response != null && response.getStatus().equalsIgnoreCase("successfully")) {
                player.sendMessage(message);
            } else {
                player.sendMessage("§c发送失败, 对方不在线.");
                Bukkit.getLogger().info(player.getName() + " tells " + args[0] + " failed.");
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = IsletopiaTweakers.getPlayerNames();
            playerNames.removeIf(s -> !s.startsWith(args[0]));
            return playerNames;
        } else {
            return new ArrayList<>();
        }
    }


}