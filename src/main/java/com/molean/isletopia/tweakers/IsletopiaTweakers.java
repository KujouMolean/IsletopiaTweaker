package com.molean.isletopia.tweakers;

import com.molean.isletopia.network.Client;
import com.molean.isletopia.network.Request;
import com.molean.isletopia.network.Response;
import com.molean.isletopia.parameter.OtherCommand;
import com.molean.isletopia.parameter.ParameterCommand;
import com.molean.isletopia.prompter.InventoryClickListener;
import com.molean.isletopia.prompter.InventoryCloseListener;
import com.molean.isletopia.prompter.IssueCommand;
import com.molean.isletopia.tweakers.tweakers.*;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;

public final class IsletopiaTweakers extends JavaPlugin implements Listener {

    private static IsletopiaTweakers isletopiaTweakers;

    private static Map<String, String> visits = new HashMap<>();

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

    Function<Request, Response> function = request -> {
        Response response = new Response();
        if (request.getType().equalsIgnoreCase("getPlotNumber")) {
            response.setStatus("successfully");
            String player = request.get("player");
            PlotAPI plotAPI = new PlotAPI();
            UUID uuid = plotAPI.getPlotSquared().getImpromptuUUIDPipeline().getSingle(player, 30);
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
        if (request.getType().equalsIgnoreCase("visit")) {
            String player = request.get("player");
            String target = request.get("target");
            PlotAPI plotAPI = new PlotAPI();
            UUID uuid = plotAPI.getPlotSquared().getImpromptuUUIDPipeline().getSingle(target, 500);
            Set<Plot> plots = plotAPI.getPlotSquared().getPlots(uuid);
            if (plots.size() < 1) {
                response.setStatus("no plot");
            } else {
                List<Plot> plotList = new ArrayList<>(plots);
                Plot plot = plotList.get(0);
                if (plot.isDenied(uuid)) {
                    response.setStatus("denied");
                } else {
                    visits.put(player, target);
                    response.setStatus("successfully");
                }
            }
        }
        return response;
    };


    @Override
    public void onEnable() {

        isletopiaTweakers = this;

        ConfigUtils.setupConfig(this);
        ConfigUtils.configOuput("guide.yml");
        ConfigUtils.configOuput("config.yml");
        serverName = ConfigUtils.getConfig("config.yml").getString("serverName");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


        new Client(function).register(serverName);
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
        new Visit();
        new VisitCommand();

        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);

        IssueCommand issueCommand = new IssueCommand();
        getCommand("issue").setExecutor(issueCommand);
        getCommand("issue").setTabCompleter(issueCommand);
        getCommand("sudo").setExecutor(issueCommand);
        getCommand("sudo").setTabCompleter(issueCommand);
        getCommand("parameter").setExecutor(new ParameterCommand());
        getCommand("otherparameter").setExecutor(new OtherCommand());
    }
}
