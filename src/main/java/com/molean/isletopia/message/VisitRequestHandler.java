//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.database.PlayerDataDao;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.VisitRequest;
import com.molean.isletopia.shared.pojo.resp.VisitResponse;
import com.molean.isletopia.utils.PlayerSerializeUtils;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Singleton
public class VisitRequestHandler implements MessageHandler<VisitRequest>, Listener {
    private final Map<UUID, LocalIsland> locationMap = new HashMap<>();
    private final Map<UUID, Long> expire = new HashMap<>();

    public VisitRequestHandler() {
        RedisMessageListener.setHandler("VisitRequest", this, VisitRequest.class);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        LocalIsland island = this.locationMap.get(uuid);
        if (island != null) {
            if (System.currentTimeMillis() - this.expire.getOrDefault(uuid, 0L) < 10000L) {
                island.tp(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void on(PlayerLoggedEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        LocalIsland island = this.locationMap.get(uuid);
        if (island != null) {
            if (System.currentTimeMillis() - this.expire.getOrDefault(uuid, 0L) < 10000L) {
                island.tp(event.getPlayer());
            }
            this.locationMap.remove(uuid);
        }
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, VisitRequest visitRequest) {
        UUID sourcePlayer = visitRequest.getSourcePlayer();
        IslandId islandId = visitRequest.getIslandId();
        VisitResponse visitResponse = new VisitResponse();
        if (!islandId.getServer().equals(ServerInfoUpdater.getServerName())) {
            visitResponse.setResponse("invalid");
            visitResponse.setResponseMessage("visit.request.error");
            ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
        }

        visitResponse.setTarget(sourcePlayer);

        IslandManager.INSTANCE.getLocalIsland(islandId, island -> {
            if (island == null) {
                visitResponse.setResponse("invalid");
                visitResponse.setResponseMessage("visit.request.invalid");
                ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
            } else {
                boolean allow = true;
                Set<UUID> membersMap = island.getMembers();

                if (island.containsFlag("Lock")) {
                    allow = false;
                }
                if (membersMap.contains(sourcePlayer)) {
                    allow = true;
                }
                if (sourcePlayer.equals(island.getUuid())) {
                    allow = true;
                }
                for (OfflinePlayer operator : Bukkit.getOperators()) {
                    if (operator.getUniqueId().equals(sourcePlayer)) {
                        allow = true;
                        break;
                    }
                }

                if (!allow) {
                    visitResponse.setResponse("refused");
                    visitResponse.setResponseMessage("visit.request.refuse");
                    ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
                } else {
                    Player player = Bukkit.getPlayer(sourcePlayer);
                    if (player != null && player.isOnline()) {
                        PluginUtils.getLogger().info("handle visit request handler: " + sourcePlayer + " " + player.getName());

                        island.tp(player);
                    } else {
                        try {
                            if (PlayerDataDao.exist(sourcePlayer)) {
                                byte[] query = PlayerDataDao.query(sourcePlayer);
                                Location safeSpawnLocation = island.adjustSafeTpLocation(island.getSpawnLocation());
                                double x = safeSpawnLocation.getX();
                                double y = safeSpawnLocation.getY();
                                double z = safeSpawnLocation.getZ();
                                PlayerSerializeUtils.modifySpawnLocation(sourcePlayer, query, x, y, z);
                            }
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                        this.locationMap.put(sourcePlayer, island);
                        this.expire.put(sourcePlayer, System.currentTimeMillis());
                    }

                    visitResponse.setResponse("accepted");
                    visitResponse.setResponseMessage("");
                    ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
                }
            }
        });

    }
}
