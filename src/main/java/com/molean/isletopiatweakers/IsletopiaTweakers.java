package com.molean.isletopiatweakers;

import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public final class IsletopiaTweakers extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    private PlotAPI plotAPI;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("utils").setExecutor(this);
        getCommand("utils").setTabCompleter(this);
    }

    @EventHandler
    public void MobSpawn(EntitySpawnEvent event) {
        if (EntityType.BAT.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Action.RIGHT_CLICK_BLOCK != event.getAction())
            return;
        if (!Material.BUCKET.equals(event.getMaterial()))
            return;
        if (!Material.OBSIDIAN.equals(event.getClickedBlock().getType()))
            return;
        event.getClickedBlock().setType(Material.LAVA);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        BlockState blockReplacedState = event.getBlockReplacedState();
        BlockData blockData = blockReplacedState.getBlockData();
        String asString = blockData.getAsString();
        if ("minecraft:lava[level=0]".equalsIgnoreCase(asString)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (EntityType.CREEPER.equals(event.getEntityType()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        String msgLower = msg.toLowerCase();
        List<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
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
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1)
            return false;

        String subcmd = args[0].toLowerCase();
        switch (subcmd) {
            case "maxplayer":
                if (args.length >= 2) {
                    int v = Integer.parseInt(args[1]);
                    try {
                        Object playerlist = Bukkit.getServer().getClass().getDeclaredMethod("getHandle").invoke(Bukkit.getServer());
                        Field maxPlayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
                        maxPlayers.setAccessible(true);
                        maxPlayers.set(playerlist, v);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    private static String[] subcmds = {"maxplayer"};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 1) {
            for (String subcmd : subcmds) {
                if (subcmd.startsWith(args[0])) {
                    strings.add(subcmd);
                }
            }
        }
        return strings;
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null)
            return;
        if (event.getClickedBlock().getType() == Material.AIR)
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (!(event.getClickedBlock().getState() instanceof Sign))
            return;
        String line = ((Sign) event.getClickedBlock().getState()).getLine(0);
        line = line.replaceAll("§.", "");
        if (!line.matches("\\[[a-zA-Z0-9].*\\]"))
            return;
        String target = line.substring(1, line.length() - 1);
        String cmd = "plot visit " + target;
        Bukkit.dispatchCommand(event.getPlayer(), cmd);
        getLogger().info("Player issued command by sign: " + cmd);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {

        String line = event.getLine(0);
        if (line.matches("\\[[a-zA-Z0-9].*\\]")) {
            String target = line.substring(1, line.length() - 1);
            event.setLine(0, "[§b" + target + "§r]");
        }

    }

    @EventHandler
    public void AnimalProtectEvent(EntityDamageByEntityEvent event) {
        if (plotAPI == null) {
            plotAPI = new PlotAPI();
        }
        if (event.getEntity() instanceof Animals || event.getEntity() instanceof Villager) {
            if (event.getDamager() instanceof Player) {
                event.setCancelled(true);
                Player player = (Player) event.getDamager();
                PlotPlayer plotPlayer = plotAPI.wrapPlayer(player.getUniqueId());
                Plot currentPlot = plotPlayer.getCurrentPlot();
                if (currentPlot != null) {
                    List<UUID> builder = new ArrayList<>();
                    UUID owner = currentPlot.getOwner();
                    builder.add(owner);
                    HashSet<UUID> trusted = currentPlot.getTrusted();
                    builder.addAll(trusted);
                    if (builder.contains(player.getUniqueId())) {
                        event.setCancelled(false);
                    }
                }
            }
        }
    }
}
