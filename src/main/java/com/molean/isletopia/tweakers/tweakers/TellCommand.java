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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TellCommand implements CommandExecutor, TabCompleter {
    public TellCommand() {
        Bukkit.getPluginCommand("tell").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length < 2)
            return true;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String message = args[1];
            Request request = new Request("dispatcher", "sendMessage");
            request.set("target", args[0]);
            request.set("message", message);
            Response response = Client.send(request);
            if (response.getStatus().equalsIgnoreCase("successfully")) {
                player.sendMessage(message);
            } else {
                player.sendMessage("§c发送失败, 对方不在线.");
            }
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return getPlayerNames();
        } else {
            return new ArrayList<String>();
        }
    }

    private static List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        Request request = new Request("dispatcher", "getOnlinePlayers");
        Response response = Client.send(request);
        if (response != null) {
            String[] respondedNames = response.get("players").split(",");
            for (String respondedName : respondedNames) {
                if (!respondedName.trim().equalsIgnoreCase("")) {
                    names.add(respondedName);
                }
            }
        } else {
            Bukkit.getLogger().severe("Failed get player from dispatcher server.");
        }
        return names;
    }
}