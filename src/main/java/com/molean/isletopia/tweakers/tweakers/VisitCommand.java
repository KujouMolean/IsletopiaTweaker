package com.molean.isletopia.tweakers.tweakers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
import com.molean.isletopia.network.Response;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VisitCommand implements CommandExecutor, Listener {
    public VisitCommand() {
        Bukkit.getPluginCommand("visit").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            if (IsletopiaTweakers.getVisits().containsKey(player.getName())) {
                String target = IsletopiaTweakers.getVisits().get(player.getName());
                Bukkit.dispatchCommand(player, "plot visit " + target);
                IsletopiaTweakers.getVisits().remove(player.getName());
            }
        });
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
                Request visitRequest = new Request(server, "visit");
                visitRequest.set("player", player.getName());
                visitRequest.set("target", args[0]);
                Response visitResponse = Client.send(visitRequest);
                if (visitResponse.getStatus().equalsIgnoreCase("successfully")) {
                    if (server.equalsIgnoreCase(IsletopiaTweakers.getServerName())) {
                        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                            Bukkit.dispatchCommand(player, "plot visit " + args[0]);
                            IsletopiaTweakers.getVisits().remove(player.getName());
                        });
                    } else {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF(server);
                        player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
                    }
                } else {
                    player.sendMessage("§8[§3岛屿助手§8] §7对方没有岛屿或拒绝了你的访问.");
                }
            }
        });
        return true;
    }
}
