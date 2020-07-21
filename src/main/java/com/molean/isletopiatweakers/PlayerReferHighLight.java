package com.molean.isletopiatweakers;

import com.molean.advancedcommand.AdvancedCommand;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class PlayerReferHighLight implements Listener {
    public PlayerReferHighLight() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        if(!event.isCancelled()){
            Group group = Neon.getBot().getGroup(483653595);
            group.sendMessageAsync(event.getPlayer().getName() + ":" + event.getMessage());
        }

        String msg = event.getMessage();
        String msgLower = msg.toLowerCase();
        List<Player> players = new ArrayList<>(org.bukkit.Bukkit.getServer().getOnlinePlayers());
        if (msg.contains("全体玩家")) {
            for (Player target : players) {
                target.playSound(target.getLocation(),
                        Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            }
            msg = msg.replaceAll("全体玩家", ChatColor.AQUA + "全体玩家"
                    + ChatColor.RESET);
        } else {
            for (Player target : players) {
                if (msgLower.contains(target.getName().toLowerCase())) {
                    target.playSound(target.getLocation(),
                            Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                    Pattern pattern = Pattern.compile(target.getName(), Pattern.CASE_INSENSITIVE);
                    msg = pattern.matcher(msg).replaceAll(ChatColor.AQUA + target.getName()
                            + ChatColor.RESET);
                }
            }
        }
        event.setMessage(msg);
        Set<Player> recipients = event.getRecipients();
        Player player = event.getPlayer();
        recipients.removeIf(player1 -> {
            String ignore = AdvancedCommand.getParameter(player1, "chatIgnore");
            if (ignore == null)
                return false;
            String[] split = ignore.split(",");
            return Arrays.asList(split).contains(player.getName());
        });
    }
}
