//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.molean.isletopia.message.handler;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.message.core.ServerMessage;
import com.molean.isletopia.message.core.ServerMessageListener;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.obj.VisitRequest;
import com.molean.isletopia.message.obj.VisitResponse;
import com.molean.isletopia.utils.BungeeUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VisitRequestHandler implements ServerMessageListener, Listener {
    private final Map<String, Location> locationMap = new HashMap<>();
    private final Map<String, Long> expire = new HashMap<>();

    public VisitRequestHandler() {
        ServerMessageManager.registerHandler("VisitRequest", this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void handleMessage(ServerMessage serverMessage) {
        serverMessage.setStatus("done");
        Gson gson = new Gson();
        VisitRequest visitRequest = gson.fromJson(serverMessage.getMessage(), VisitRequest.class);
        String sourcePlayer = visitRequest.getSourcePlayer();
        String targetPlayer = visitRequest.getTargetPlayer();
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setTarget(sourcePlayer);
        boolean allow = true;
        UUID sourceUUID = ServerInfoUpdater.getUUID(sourcePlayer);
        UUID targetUUID = ServerInfoUpdater.getUUID(targetPlayer);
        Set<Plot> plots = PlotSquared.get().getFirstPlotArea().getPlots(targetUUID);
        if (plots.isEmpty()) {
            visitResponse.setResponse("invalid");
            visitResponse.setResponseMessage("§8[§3岛屿助手§8] §7对方没有岛屿.");
            ServerMessageManager.sendMessage(serverMessage.getSource(), "VisitResponse", visitResponse);
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
                    BungeeUtils.sendVisitNotificationToPlayer(targetPlayer, sourcePlayer, true);
                }

                visitResponse.setResponse("refused");
                visitResponse.setResponseMessage("§8[§3岛屿助手§8] §7对方拒绝了你的访问.");
                ServerMessageManager.sendMessage(serverMessage.getSource(), "VisitResponse", visitResponse);
            } else {
                if (!targetPlayer.equalsIgnoreCase(sourcePlayer) && !offlinePlayer.isOp()) {
                    if (ServerInfoUpdater.getOnlinePlayers().contains(targetPlayer)) {
                        BungeeUtils.sendVisitNotificationToPlayer(targetPlayer, sourcePlayer, false);
                    } else if (!UniversalParameter.getParameterAsList(targetPlayer, "visits").contains(sourcePlayer)) {
                        UniversalParameter.addParameter(targetPlayer, "visits", sourcePlayer);
                    }
                }

                plot.getHome((location) -> {
                    World world = Bukkit.getWorld(location.getWorld());
                    int x = location.getX();
                    int y = location.getY();
                    int z = location.getZ();
                    float yaw = location.getYaw();
                    float pitch = location.getPitch();
                    Location bukkitLocation = new Location(world, (double)x + 0.5D, (double)y, (double)z + 0.5D, yaw, pitch);
                    Player player = Bukkit.getPlayer(sourceUUID);
                    if (player != null && player.isOnline()) {
                        player.teleport(bukkitLocation);
                    } else {
                        this.locationMap.put(sourcePlayer, bukkitLocation);
                        this.expire.put(sourcePlayer, System.currentTimeMillis());
                        visitResponse.setResponse("accepted");
                        visitResponse.setResponseMessage("");
                        ServerMessageManager.sendMessage(serverMessage.getSource(), "VisitResponse", visitResponse);
                    }
                });
            }
        }
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        Location location = this.locationMap.get(name);
        if (location != null) {
            if (System.currentTimeMillis() - (Long)this.expire.getOrDefault(name, 0L) < 10000L) {
                event.getPlayer().teleport(location);
            }

            this.locationMap.remove(name);
        }

    }
}
