package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
import com.molean.isletopia.network.Response;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatTweaker implements Listener {
    public PlayerChatTweaker() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Request request = new Request("dispatcher", "chat");
            request.set("message", event.getMessage());
            request.set("player", event.getPlayer().getName());
            Response response = Client.send(request);
            if (response == null) {
                Bukkit.getLogger().info("Send " + event.getPlayer().getName() + " chat error.");
            }
        });
    }
}
