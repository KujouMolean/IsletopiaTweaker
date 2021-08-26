//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.req.VisitRequest;
import com.molean.isletopia.shared.pojo.resp.VisitResponse;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.UUIDUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import redis.clients.jedis.Jedis;

import java.util.*;

public class VisitRequestHandler implements MessageHandler<VisitRequest>, Listener {
    private final Map<String, Location> locationMap = new HashMap<>();
    private final Map<String, Long> expire = new HashMap<>();

    public VisitRequestHandler() {
        RedisMessageListener.setHandler("VisitRequest", this, VisitRequest.class);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        Location location = this.locationMap.get(name);
        if (location != null) {
            if (System.currentTimeMillis() - this.expire.getOrDefault(name, 0L) < 10000L) {
                event.getPlayer().teleport(location);
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
        UUID sourceUUID = UUIDUtils.get(sourcePlayer);
        UUID targetUUID = UUIDUtils.get(targetPlayer);

        Set<Plot> plots = PlotSquared.get().getPlotAreaManager().getAllPlotAreas()[0].getPlots(targetUUID);
        if (plots.isEmpty()) {
            visitResponse.setResponse("invalid");
            visitResponse.setResponseMessage("§8[§3岛屿助手§8] §7对方没有岛屿.");
            ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
        } else {
            Plot plot = plots.iterator().next();
            UUID allUUID = PlotDao.getAllUUID();
            HashSet<UUID> denied = plot.getDenied();
            HashSet<UUID> trusted = plot.getTrusted();
            if (denied.contains(sourceUUID) || denied.contains(allUUID)) {
                allow = false;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(sourceUUID);
            if (trusted.contains(sourceUUID) || targetPlayer.equalsIgnoreCase(sourcePlayer) || offlinePlayer.isOp()) {
                allow = true;
            }

            if (!allow) {
                if (!offlinePlayer.isOp() && !targetPlayer.equalsIgnoreCase(sourcePlayer) && ServerInfoUpdater.getOnlinePlayers().contains(targetPlayer)) {
                    IsletopiaTweakersUtils.sendVisitNotificationToPlayer(targetPlayer, sourcePlayer, true);
                }

                visitResponse.setResponse("refused");
                visitResponse.setResponseMessage("§8[§3岛屿助手§8] §7对方拒绝了你的访问.");
                ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);

                try (Jedis jedis = RedisUtils.getJedis()) {
                    jedis.set("Lock-" + targetUUID, "true");

                }
            } else {
                if (!targetPlayer.equalsIgnoreCase(sourcePlayer) && !offlinePlayer.isOp()) {
                    if (ServerInfoUpdater.getOnlinePlayers().contains(targetPlayer)) {
                        IsletopiaTweakersUtils.sendVisitNotificationToPlayer(targetPlayer, sourcePlayer, false);
                    } else if (!UniversalParameter.getParameterAsList(targetPlayer, "visits").contains(sourcePlayer)) {
                        UniversalParameter.addParameter(targetPlayer, "visits", sourcePlayer);
                    }
                }
                try (Jedis jedis = RedisUtils.getJedis()) {
                    jedis.set("Lock-" + targetUUID, "false");
                }

                plot.getHome((location) -> {
                    World world = Bukkit.getWorld(location.getWorldName());
                    int x = location.getX();
                    int y = location.getY();
                    int z = location.getZ();
                    float yaw = location.getYaw();
                    float pitch = location.getPitch();
                    Location bukkitLocation = new Location(world, (double) x + 0.5D, (double) y, (double) z + 0.5D, yaw, pitch);
                    Player player = Bukkit.getPlayer(sourceUUID);
                    if (player != null && player.isOnline()) {
                        player.teleport(bukkitLocation);
                    } else {
                        this.locationMap.put(sourcePlayer, bukkitLocation);
                        this.expire.put(sourcePlayer, System.currentTimeMillis());
                        visitResponse.setResponse("accepted");
                        visitResponse.setResponseMessage("");
                        ServerMessageUtils.sendMessage(wrappedMessageObject.getFrom(), "VisitResponse", visitResponse);
                    }
                });
            }
        }
    }
}
