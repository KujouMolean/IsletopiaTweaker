//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.VisitRequest;
import com.molean.isletopia.shared.pojo.resp.VisitResponse;
import com.molean.isletopia.shared.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class VisitRequestHandler implements MessageHandler<VisitRequest>, Listener {
    private final Map<String, LocalIsland> locationMap = new HashMap<>();
    private final Map<String, Long> expire = new HashMap<>();

    public VisitRequestHandler() {
        RedisMessageListener.setHandler("VisitRequest", this, VisitRequest.class);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }
    @EventHandler
    public void on(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        LocalIsland island = this.locationMap.get(name);
        if (island != null) {
            if (System.currentTimeMillis() - this.expire.getOrDefault(name, 0L) < 10000L) {
                island.tp(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void on(PlayerDataSyncCompleteEvent event) {
        String name = event.getPlayer().getName();
        LocalIsland island = this.locationMap.get(name);
        if (island != null) {
            if (System.currentTimeMillis() - this.expire.getOrDefault(name, 0L) < 10000L) {
                island.tp(event.getPlayer());
            }
            this.locationMap.remove(name);
        }
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, VisitRequest visitRequest) {
        String sourcePlayer = visitRequest.getSourcePlayer();
        String targetPlayer = visitRequest.getTargetPlayer();
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setTarget(sourcePlayer);
        boolean allow = true;


        List<LocalIsland> playerLocalServerIslands = IslandManager.INSTANCE.getPlayerLocalServerIslands(UUIDUtils.get(targetPlayer));
        LocalIsland island = null;
        for (LocalIsland playerLocalServerIsland : playerLocalServerIslands) {
            if (playerLocalServerIsland.getId() == visitRequest.getId()) {
                island = playerLocalServerIsland;
            }
        }

        if (island == null) {
            visitResponse.setResponse("invalid");
            visitResponse.setResponseMessage("§8[§3岛屿助手§8] §7对方没有岛屿.");
            ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
        } else {


            Set<UUID> membersMap = island.getMembers();

            if (island.containsFlag("Lock")) {
                allow = false;
            }

            Set<String> operators = new HashSet<>();
            for (OfflinePlayer operator : Bukkit.getOperators()) {
                operators.add(operator.getName());
            }

            if (membersMap.contains(UUIDUtils.get(sourcePlayer)) || targetPlayer.equalsIgnoreCase(sourcePlayer) || operators.contains(sourcePlayer)) {
                allow = true;
            }


            if (!allow) {
                visitResponse.setResponse("refused");
                visitResponse.setResponseMessage("§8[§3岛屿助手§8] §7对方拒绝了你的访问.");
                ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
            } else {
                Player player = Bukkit.getPlayerExact(sourcePlayer);
                if (player != null && player.isOnline()) {
                    System.out.println("handle visit request handler: " + sourcePlayer + " " + player.getName());
                    island.tp(player);
                } else {
                    this.locationMap.put(sourcePlayer, island);
                    this.expire.put(sourcePlayer, System.currentTimeMillis());
                }

                visitResponse.setResponse("accepted");
                visitResponse.setResponseMessage("");
                ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
            }
        }
    }
}
