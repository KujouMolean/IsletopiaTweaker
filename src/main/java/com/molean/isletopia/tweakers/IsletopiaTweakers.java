package com.molean.isletopia.tweakers;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.parameter.ParameterCommand;
import com.molean.isletopia.parameter.UniversalParameter;
import com.molean.isletopia.prompter.IssueCommand;
import com.molean.isletopia.tweakers.tweakers.*;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.bukkit.Bukkit.getScheduler;

public final class IsletopiaTweakers extends JavaPlugin implements Listener, PluginMessageListener {


    private static IsletopiaTweakers isletopiaTweakers;

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }



    private static String serverName;

    public static String getServerName() {
        return serverName;
    }

    private static final List<String> onlinePlayers = new ArrayList<>();

    public static List<String> getOnlinePlayers() {
        return onlinePlayers;
    }

    private static final List<String> servers = new ArrayList<>();

    public static List<String> getServers() {
        return servers;
    }

    private static final Map<String, String> visits = new HashMap<>();

    @Override
    public void onEnable() {
        isletopiaTweakers = this;
        ConfigUtils.setupConfig(this);
        ConfigUtils.configOuput("guide.yml");
        new AddMerchant();
        new AnimalProtect();
        new ClockMenu();
        new GuideBook();
        new LavaProtect();
        new NewbieOperation();
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
        new IslandCommand();
        new PlotMobCap();

        getServer().getPluginManager().registerEvents(this, this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        getScheduler().runTaskTimerAsynchronously(this, IsletopiaTweakers::updates, 20, 20);
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
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static void updateServerName() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        out.writeUTF("GetServer");
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static void updateServers() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        out.writeUTF("GetServers");
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
            player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
    }

    public static UUID getUUID(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8));
    }




    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (visits.containsKey(player.getName())) {
            Bukkit.dispatchCommand(player, "plot visit " + visits.get(player.getName()));
            visits.remove(player.getName());
        }
        List<String> visits = UniversalParameter.getParameterAsList(event.getPlayer().getName(), "visits");
        if (visits.size() > 0) {
            player.sendMessage("§8[§3访客提醒§8] §e离线时的访客有:");
            player.sendMessage("§7  " + String.join(",", visits));
            UniversalParameter.setParameter(event.getPlayer().getName(), "visits", null);
        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("PlayerList")) {
            String server = in.readUTF();
            String[] playerList = in.readUTF().split(", ");
            onlinePlayers.clear();
            onlinePlayers.addAll(Arrays.asList(playerList));
        } else if (subChannel.equalsIgnoreCase("GetServer")) {
            serverName = in.readUTF();
        } else if (subChannel.equalsIgnoreCase("GetServers")) {
            String[] serverList = in.readUTF().split(", ");
            servers.clear();
            servers.addAll(Arrays.asList(serverList));
        } else if (subChannel.equalsIgnoreCase("tell")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String tellMessage = msgin.readUTF();
                player.sendMessage(tellMessage);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else if (subChannel.equalsIgnoreCase("visit")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

                String source = msgin.readUTF();
                String target = msgin.readUTF();

                Player sourcePlayer = Bukkit.getPlayer(source);
                if (sourcePlayer != null) {
                    Bukkit.getScheduler().runTask(this, () -> {
                        Bukkit.dispatchCommand(sourcePlayer, "plot visit " + target);
                    });
                } else {
                    visits.put(source, target);
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
