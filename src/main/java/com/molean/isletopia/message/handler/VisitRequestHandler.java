//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.VisitRequest;
import com.molean.isletopia.shared.pojo.resp.VisitResponse;
import com.molean.isletopia.shared.utils.RedisUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class VisitRequestHandler implements MessageHandler<VisitRequest>, Listener {
    private final Map<String, Island> locationMap = new HashMap<>();
    private final Map<String, Long> expire = new HashMap<>();

    public VisitRequestHandler() {
        RedisMessageListener.setHandler("VisitRequest", this, VisitRequest.class);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerDataSyncCompleteEvent event) {
        String name = event.getPlayer().getName();
        Island island = this.locationMap.get(name);
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


        List<Island> playerLocalServerIslands = IslandManager.INSTANCE.getPlayerLocalServerIslands(targetPlayer);
        Island island = null;
        for (Island playerLocalServerIsland : playerLocalServerIslands) {
            if (playerLocalServerIsland.getId() == visitRequest.getId()) {
                island = playerLocalServerIsland;
            }
        }

        if (island == null) {
            visitResponse.setResponse("invalid");
            visitResponse.setResponseMessage("§8[§3岛屿助手§8] §7对方没有岛屿.");
            ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
        } else {
            List<String> members = island.getMembers();

            if (island.containsFlag("Lock")) {
                allow = false;
            }

            Set<String> operators = new HashSet<>();
            for (OfflinePlayer operator : Bukkit.getOperators()) {
                operators.add(operator.getName());
            }

            if (members.contains(sourcePlayer) || targetPlayer.equalsIgnoreCase(sourcePlayer) || operators.contains(sourcePlayer)) {
                allow = true;
            }


            if (!allow) {
                visitResponse.setResponse("refused");
                visitResponse.setResponseMessage("§8[§3岛屿助手§8] §7对方拒绝了你的访问.");
                ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);

                RedisUtils.getCommand().set("Lock-" + targetPlayer, "true");

            } else {
                RedisUtils.getCommand().set("Lock-" + targetPlayer, "false");

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
