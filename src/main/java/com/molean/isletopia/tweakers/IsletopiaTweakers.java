package com.molean.isletopia.tweakers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
import com.molean.isletopia.network.Response;
import com.molean.isletopia.parameter.ParameterCommand;
import com.molean.isletopia.prompter.InventoryClickListener;
import com.molean.isletopia.prompter.InventoryCloseListener;
import com.molean.isletopia.prompter.IssueCommand;
import com.molean.isletopia.tweakers.tweakers.*;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.*;
import java.util.function.Function;

import static org.bukkit.Bukkit.getScheduler;

public final class IsletopiaTweakers extends JavaPlugin implements Listener, PluginMessageListener {

    private static IsletopiaTweakers isletopiaTweakers;

    private static final Map<String, String> visits = new HashMap<>();

    private static String serverName;

    public static String getServerName() {
        return serverName;
    }

    public static Map<String, String> getVisits() {
        return visits;
    }

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }

    private static List<String> onlinePlayers;

    public static List<String> getOnlinePlayers() {
        return onlinePlayers;
    }

    private static List<String> servers;

    private static Function<Request, Response> function = request -> {
        Response response = new Response();
        Bukkit.getLogger().info("Receive request " + request.getType());
        if (request.getType().equalsIgnoreCase("getPlotNumber")) {
            response.setStatus("successfully");
            String player = request.get("player");
            PlotAPI plotAPI = new PlotAPI();
            UUID uuid = plotAPI.getPlotSquared().getImpromptuUUIDPipeline().getSingle(player, 80);
            if (uuid == null) {
                response.set("return", 0 + "");
            } else {
                Set<Plot> plots = plotAPI.getPlotSquared().getPlots(uuid);
                response.set("return", plots.size() + "");
            }
        }
        if (request.getType().equalsIgnoreCase("getOnlinePlayers")) {
            response.setStatus("successfully");
            List<String> playerNames = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> playerNames.add(player.getName()));
            response.set("players", String.join(",", playerNames));
        }
        if (request.getType().equalsIgnoreCase("broadcast")) {
            response.setStatus("successfully");
            Bukkit.broadcastMessage(request.get("message"));
        }
        if (request.getType().equalsIgnoreCase("updateUUID")) {
            response.setStatus("successfully");
            String player = request.get("player");
            String uuid = request.get("uuid");
            PlotSquared.get().getImpromptuUUIDPipeline().storeImmediately(player, UUID.fromString(uuid));
        }
        if (request.getType().equalsIgnoreCase("chat")) {
            String player = request.get("player");
            String message = request.get("message");
            Bukkit.broadcastMessage("<" + player + "> " + message);
            response.setStatus("successfully");
        }
        if (request.getType().equalsIgnoreCase("sendMessage")) {
            String target = request.get("target");
            String message = request.get("message");
            Player targetPlayer = Bukkit.getPlayer(target);
            if (targetPlayer != null && targetPlayer.isOnline()) {
                targetPlayer.sendMessage(message);
                response.setStatus("successfully");
            } else {
                response.setStatus("not online");
            }
        }
        if (request.getType().equalsIgnoreCase("visit")) {
            String player = request.get("player");
            String target = request.get("target");
            PlotAPI plotAPI = new PlotAPI();
            UUID targetUUID = plotAPI.getPlotSquared().getImpromptuUUIDPipeline().getSingle(target, 30);
            UUID playerUUID = plotAPI.getPlotSquared().getImpromptuUUIDPipeline().getSingle(player, 30);
            Set<Plot> plots = plotAPI.getPlotSquared().getPlots(targetUUID);
            if (plots.size() < 1) {
                response.setStatus("no plot");
                return response;
            } else {
                List<Plot> plotList = new ArrayList<>(plots);
                Plot plot = plotList.get(0);
                if (plot.getOwner().equals(playerUUID) || plot.getTrusted().contains(playerUUID)) {
                    visits.put(player, target);
                    response.setStatus("successfully");
                    return response;
                } else {
                    for (UUID aUUID : plot.getDenied()) {
                        String name = plotAPI.getPlotSquared().getImpromptuUUIDPipeline().getSingle(aUUID, 30);
                        if (name.equals(player) || name.equalsIgnoreCase("*")) {
                            response.setStatus("denied");
                            return response;
                        }
                    }
                    visits.put(player, target);
                    response.setStatus("successfully");
                    return response;
                }

            }
        }
        return response;
    };

    private static final Client client = new Client(function);


    @Override
    public void onEnable() {

        isletopiaTweakers = this;

        ConfigUtils.setupConfig(this);
        ConfigUtils.configOuput("guide.yml");
        ConfigUtils.configOuput("config.yml");
        serverName = ConfigUtils.getConfig("config.yml").getString("serverName");

//        client.register(serverName);

        new AddMerchant();
        new AnimalProtect();
        new ClockMenu();
        new GuideBook();
        new LavaProtect();
        new NewbieOperation();
        new ObsidianRecovery();
        new PlayerChatTweaker();
        new PreventCreeperBreak();
        new RemoveDisgustingMob();
        new TeleportSign();
        new RegistRecipe();
        new VisitCommand();
        new IssueCommand();
        new ParameterCommand();
        new TellCommand();
        new FertilizeFlower();

        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        getScheduler().runTaskTimerAsynchronously(this, IsletopiaTweakers::updates, 20, 20);

    }

    @Override
    public void onDisable() {
        client.unregister(serverName);
    }

    public static void updates() {
        updateOnlinePlayers();
        updateServerName();
        updateServers();
    }

    public static void updateOnlinePlayers() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");
        out.writeUTF("PlayerList");
        Bukkit.getServer().sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static void updateServerName() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        out.writeUTF("GetServer");
        Bukkit.getServer().sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static void updateServers() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        out.writeUTF("GetServers");
        Bukkit.getServer().sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equalsIgnoreCase("PlayerList")) {
            String[] playerList = in.readUTF().split(", ");
            onlinePlayers.clear();
            onlinePlayers.addAll(Arrays.asList(playerList));
        }
        if (subchannel.equalsIgnoreCase("GetServer")) {
            serverName = in.readUTF();
        }
        if (subchannel.equalsIgnoreCase("GetServers")) {
            String[] serverList = in.readUTF().split(", ");
            servers.clear();
            servers.addAll(Arrays.asList(serverList));
        }
        if (subchannel.equalsIgnoreCase("GetServers")) {
            String[] serverList = in.readUTF().split(", ");
            servers.clear();
            servers.addAll(Arrays.asList(serverList));
        }
        if (subchannel.equalsIgnoreCase("visit")) {

        }
    }
}
