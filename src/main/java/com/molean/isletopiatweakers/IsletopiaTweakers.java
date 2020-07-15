package com.molean.isletopiatweakers;

import com.molean.advancedcommand.AdvancedCommand;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import fr.xephi.authme.events.LoginEvent;
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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static org.bukkit.Material.*;

public final class IsletopiaTweakers extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    private PlotAPI plotAPI;
    private static IsletopiaTweakers isletopiaTweakers;

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }

    @Override
    public void onEnable() {
        org.bukkit.Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("utils").setExecutor(this);
        getCommand("utils").setTabCompleter(this);
        getServer().getPluginCommand("account").setExecutor(this);
        isletopiaTweakers = this;
        //萤石
        Bukkit.resetRecipes();
        RecipeUtils.registerShaped("tweaker_craft_glowstone", new ItemStack(Material.GLOWSTONE),
                TORCH, TORCH, TORCH,
                TORCH, STONE, TORCH,
                TORCH, TORCH, TORCH);
        //合成粘土
        RecipeUtils.registerShaped("tweaker_craft_clay", new ItemStack(CLAY_BALL, 32),
                DIRT, DIRT, DIRT,
                DIRT, BLACK_DYE, DIRT,
                DIRT, DIRT, DIRT);
        //合成金锭
        RecipeUtils.registerShaped("tweaker_craft_gold_ingot", new ItemStack(GOLD_INGOT),
                YELLOW_DYE, YELLOW_DYE, YELLOW_DYE,
                YELLOW_DYE, IRON_INGOT, YELLOW_DYE,
                YELLOW_DYE, YELLOW_DYE, YELLOW_DYE);
        RecipeUtils.registerSmithingRecipie("tweaker_smithing_crimson_nylium", new ItemStack(CRIMSON_NYLIUM), NETHERRACK, CRIMSON_FUNGUS);
        RecipeUtils.registerSmithingRecipie("tweaker_smithing_warped_nylium", new ItemStack(WARPED_NYLIUM), NETHERRACK, WARPED_FUNGUS);

        RecipeUtils.registerCampfire("tweaker_campfire_warped_nylium", new ItemStack(IRON_NUGGET), CARROT, 1.0F, 60);
        RecipeUtils.registerCampfire("tweaker_campfire_diamond", new ItemStack(DIAMOND), POISONOUS_POTATO, 1.0F, 600);

        RecipeUtils.registerSmoking("tweaker_smoking_soul_sand", new ItemStack(SOUL_SAND), SAND, 1.0F, 150);
        RecipeUtils.registerSmoking("tweaker_smoking_netherrack", new ItemStack(NETHERRACK), COBBLESTONE, 1.0F, 150);
        RecipeUtils.registerStonecutting("tweaker_stonecut_gravel", new ItemStack(GRAVEL), COBBLESTONE);
        RecipeUtils.registerBlasting("tweaker_blasting_gravel", new ItemStack(GRAVEL), COBBLESTONE, 1.0F, 75);
        RecipeUtils.registerBlasting("tweaker_blasting_sand", new ItemStack(SAND), GRAVEL, 1.0F, 75);
        RecipeUtils.registerFurnace("tweaker_blasting_quartz", new ItemStack(QUARTZ), GLASS, 1.0F, 75);


        //转换树苗
        RecipeUtils.registerShapeless("tweaker_craft_oak_sapling", new ItemStack(OAK_SAPLING), DARK_OAK_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_spruce_sapling", new ItemStack(SPRUCE_SAPLING), OAK_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_birch_sapling", new ItemStack(BIRCH_SAPLING), SPRUCE_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_jungle_sapling", new ItemStack(JUNGLE_SAPLING), BIRCH_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_acacia_sapling", new ItemStack(ACACIA_SAPLING), JUNGLE_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_dark_oak_sapling", new ItemStack(DARK_OAK_SAPLING), ACACIA_SAPLING);
        RecipeUtils.registerShapeless("tweaker_craft_obsidian", new ItemStack(OBSIDIAN, 4), ENCHANTING_TABLE);
    }


    @EventHandler
    public void onTransfer(EntityTransformEvent event) {
        if (event.getTransformedEntity() instanceof PigZombie) {
            event.getEntity().remove();
        }
        if (event.getTransformedEntity() instanceof Zoglin) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        ItemStack item = event.getItem();
        if (item.getType().equals(Material.WATER_BUCKET)) {
            double[] tps = org.bukkit.Bukkit.getTPS();
            if (tps[0] < 19) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void MobSpawn(EntitySpawnEvent event) {
        if (EntityType.BAT.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
        if (EntityType.ZOMBIFIED_PIGLIN.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
        if (EntityType.ZOGLIN.equals(event.getEntity().getType())) {
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("utils")){
            if (args.length < 1)
                return false;

            String subcmd = args[0].toLowerCase();
            switch (subcmd) {
                case "maxplayer":
                    if (args.length >= 2) {
                        int v = Integer.parseInt(args[1]);
                        try {
                            Object playerlist = org.bukkit.Bukkit.getServer().getClass().getDeclaredMethod("getHandle").invoke(org.bukkit.Bukkit.getServer());
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
        }else if(command.getName().equalsIgnoreCase("account")){
            if (args.length != 2) {
                sender.sendMessage("§c格式错误, 请输入/account <邮箱> <密码>.");
                return true;
            }
            Player player = (Player) sender;
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                if (authen(player.getName(), args[0], args[1])) {
                    sender.sendMessage("§2验证成功, 已发放奖励. 为保证阁下正版账号安全, 请立刻修改密码!!");
                    Bukkit.getScheduler().runTask(IsletopiaTweakers.this, () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "aach add 1 Custom.genuine " + player.getName());
                    });

                } else {
                    sender.sendMessage("§c验证失败, 请检查邮箱和密码.");
                }
            });
            return true;
        }
        return true;
    }
    public static boolean authen(String playername, String email, String pass) {
        String url_post = "https://authserver.mojang.com/authenticate";
        try {
            URL url = new URL(url_post);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            JSONObject obj = new JSONObject();
            obj.put("username", email);
            obj.put("password", pass);
            JSONObject obj2 = new JSONObject();
            obj2.put("name", "Minecraft");
            obj2.put("version", 1);
            obj.put("agent", obj2);
            out.writeBytes(obj.toString());
            out.flush();
            out.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stbu = new StringBuilder();
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), StandardCharsets.UTF_8);
                stbu.append(lines);
            }

            String i = stbu.toString();
            JSONObject x = (JSONObject) (new JSONParser()).parse(i);
            String m = x.get("selectedProfile").toString();
            JSONObject y = (JSONObject) (new JSONParser()).parse(m);
            reader.close();
            connection.disconnect();
            if (playername.equals(y.get("name"))) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static final String[] subcmds = {"maxplayer"};

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
        org.bukkit.Bukkit.dispatchCommand(event.getPlayer(), cmd);
        getLogger().info(event.getPlayer().getName() + " issued command by sign: " + cmd);
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
            if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                event.setCancelled(true);
                Projectile projectile = (Projectile) event.getDamager();
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player) {
                    Player player = (Player) shooter;
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
                return;
            }
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

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Material material = event.getMaterial();
        Action action = event.getAction();
        if (!material.equals(Material.CLOCK))
            return;
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            event.getPlayer().performCommand("issue plot visit ${island,%player_name%}");
        }
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            event.getPlayer().performCommand("deluxemenu open main");
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        Set<Plot> plots = PlotSquared.get().getPlots(PlotPlayer.wrap(player));
        if (plots.size() == 0) {
            event.getPlayer().performCommand("plot auto");
            placeItem(event.getPlayer().getInventory());
        }
        if (!player.getInventory().contains(Material.CLOCK)) {
            player.getInventory().addItem(newUnbreakableItem(Material.CLOCK, "§f[§d主菜单§f]§r",
                    List.of("§f[§f左键单击§f]§r §f回到§r §f主岛屿§r", "§f[§7右键单击§f]§r §f打开§r §f主菜单§r")));
        }
    }

    public void placeItem(PlayerInventory inventory) {

        ItemStack menu = newUnbreakableItem(Material.CLOCK, "§f[§d主菜单§f]§r",
                List.of("§f[§f左键单击§f]§r §f回到§r §f主岛屿§r", "§f[§7右键单击§f]§r §f打开§r §f主菜单§r"));
        ItemStack helmet = newUnbreakableItem(Material.LEATHER_HELMET, "§f[§d新手帽子§f]§r", List.of());
        ItemStack chestPlate = newUnbreakableItem(Material.LEATHER_CHESTPLATE, "§f[§d新手上衣§f]§r", List.of());
        ItemStack leggings = newUnbreakableItem(Material.LEATHER_LEGGINGS, "§f[§d新手裤子§f]§r", List.of());
        ItemStack boots = newUnbreakableItem(Material.LEATHER_BOOTS, "§f[§d新手靴子§f]§r", List.of());
        ItemStack sword = newUnbreakableItem(Material.WOODEN_SWORD, "§f[§d新手木剑§f]§r", List.of());
        ItemStack shovel = newUnbreakableItem(Material.WOODEN_SHOVEL, "§f[§d新手木锹§f]§r", List.of());
        ItemStack pickAxe = newUnbreakableItem(Material.WOODEN_PICKAXE, "§f[§d新手木镐§f]§r", List.of());
        ItemStack axe = newUnbreakableItem(Material.WOODEN_AXE, "§f[§d新手木斧§f]§r", List.of());
        ItemStack hoe = newUnbreakableItem(Material.WOODEN_HOE, "§f[§d新手木锄§f]§r", List.of());
        ItemStack food = new ItemStack(Material.APPLE, 16);

        inventory.setHelmet(helmet);
        inventory.setChestplate(chestPlate);
        inventory.setLeggings(leggings);
        inventory.setBoots(boots);
        inventory.addItem(menu, food, sword, axe, pickAxe, hoe, shovel);
    }

    public ItemStack newUnbreakableItem(Material material, String name, List<String> lores) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
