package com.molean.isletopia.tweakers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
import com.molean.isletopia.network.Response;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VisitCommand implements CommandExecutor {
    public VisitCommand() {
        Bukkit.getPluginCommand("visit").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length < 1)
            return true;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Request request = new Request("dispatcher", "getPlayerServer");
            request.set("player", args[0]);
            Response response = Client.send(request);
            if (response.getStatus().equalsIgnoreCase("successfully")) {
                String server = response.get("return");
                if (server.equalsIgnoreCase(IsletopiaTweakers.getServerName())) {
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        Bukkit.dispatchCommand(player, "plot visit " + args[0]);
                    });
                } else {
                    Request visitRequest = new Request(server, "visit");
                    visitRequest.set("player", player.getName());
                    visitRequest.set("target", args[0]);
                    Response visitResponse = Client.send(visitRequest);
                    if (visitResponse.getStatus().equalsIgnoreCase("successfully")) {
                        dispatcher(player, server);
                    }
                }
            }
        });
        return true;
    }

    public static void dispatcher(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }
}
