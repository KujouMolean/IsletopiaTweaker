package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
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
            Client.send(request);
        });
//        String msg = event.getMessage();
//        String msgLower = msg.toLowerCase();
//        List<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
//            for (Player target : players) {
//                if (msgLower.contains(target.getName().toLowerCase())) {
//                    target.playSound(target.getLocation(),
//                            Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
//                    Pattern pattern = Pattern.compile(target.getName(), Pattern.CASE_INSENSITIVE);
//                    msg = pattern.matcher(msg).replaceAll(ChatColor.AQUA + target.getName()
//                            + ChatColor.RESET);
//                }
//        }
//        event.setMessage(msg);
    }
}
