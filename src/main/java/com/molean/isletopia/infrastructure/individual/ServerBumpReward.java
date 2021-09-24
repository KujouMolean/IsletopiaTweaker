package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.pojo.obj.ServerBumpObject;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerBumpReward implements CommandExecutor, TabCompleter {
    public static class BumpInfo {
        private final int uid;
        private final String username;
        private final LocalDateTime dateTime;
        public BumpInfo(int uid, String username, LocalDateTime dateTime) {
            this.uid = uid;
            this.username = username;
            this.dateTime = dateTime;
        }
        @Override
        public String toString() {
            return "BumpInfo{" +
                    "uid=" + uid +
                    ", username='" + username + '\'' +
                    ", dateTime=" + dateTime +
                    '}';
        }
    }

    private final List<BumpInfo> bumpInfos = new ArrayList<>();

    private long lastUpdate = 0;

    public ServerBumpReward() {
        Objects.requireNonNull(Bukkit.getPluginCommand("bumpreward")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("bumpreward")).setExecutor(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (args.length < 1) {
                sender.sendMessage("请按要求输入：/bumpreward MCBBS用户名");
                sender.sendMessage("例如 /bumpreward Molean");
                return;
            }

            if (System.currentTimeMillis() - lastUpdate > 1000 * 30) {
                updateInformation();
            }

            String lastBumpReward = UniversalParameter.getParameter(args[0], "lastBumpReward");
            if (lastBumpReward != null && !lastBumpReward.isEmpty()) {
                LocalDate parse = LocalDate.parse(lastBumpReward, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (parse.isEqual(LocalDate.now())) {
                    sender.sendMessage("该MCBBS账号的今日奖励已被领取。");
                    return;
                }
            }

            for (BumpInfo bumpInfo : bumpInfos) {
                if (args[0].equalsIgnoreCase("debug")) {
                    System.out.println(bumpInfo);
                }
                if (bumpInfo.dateTime.toLocalDate().isEqual(LocalDate.now()) && bumpInfo.username.equalsIgnoreCase(args[0])) {
                    String format = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    UniversalParameter.setParameter(args[0], "lastBumpReward", format);
                    Player player = (Player) sender;
                    player.getInventory().addItem(new ItemStack(Material.SHULKER_BOX, 1));

                    ServerBumpObject serverBumpObject = new ServerBumpObject();
                    serverBumpObject.setPlayer(sender.getName());
                    serverBumpObject.setUser(args[0]);
                    serverBumpObject.setItems(new ArrayList<>());
                    serverBumpObject.getItems().add("潜影盒");

                    Random random = new Random();

                    ArrayList<ItemStack> itemStacks = new ArrayList<>();

                    if (random.nextInt(100) < 10) {
                        itemStacks.add(new ItemStack(Material.BEACON, 1));
                        serverBumpObject.getItems().add("信标");
                        UniversalParameter.addParameter("Molean", "beacon", player.getName());
                        UniversalParameter.setParameter(player.getName(), "beaconReason", "顶贴");
                    }
                    if (random.nextInt(100) < 5) {
                        itemStacks.add(new ItemStack(Material.BUNDLE, 1));
                        serverBumpObject.getItems().add("收纳袋");
                    }
                    if (random.nextInt(100) < 10) {
                        itemStacks.add(new ItemStack(Material.HEART_OF_THE_SEA, 1));
                        serverBumpObject.getItems().add("海洋之心");
                    }
                    if (random.nextInt(100) == 0) {
                        itemStacks.add(new ItemStack(Material.ELYTRA, 1));
                        serverBumpObject.getItems().add("鞘翅");
                        UniversalParameter.addParameter("Molean", "elytra", player.getName());
                        UniversalParameter.setParameter(player.getName(), "elytraReason", "顶贴");
                    }

                    Collection<ItemStack> values = player.getInventory().addItem(itemStacks.toArray(new ItemStack[0])).values();
                    for (ItemStack value : values) {
                        player.getLocation().getWorld().dropItem(player.getLocation(), value);
                    }

                    ServerMessageUtils.sendMessage("waterfall", "ServerBump", serverBumpObject);
                    return;
                }
            }
            sender.sendMessage("你还没有顶帖，或者ID输入错误，请查证后重新领取。");
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        return null;
    }


    public void updateInformation() {
        byte[] bytes = new byte[0];
        try {
            URL url = new URL("https://www.mcbbs.net/forum.php?" +
                    "mod=misc&action=viewthreadmod&tid=1160598&mobile=no");
            InputStream inputStream = url.openStream();
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String source = new String(bytes, StandardCharsets.UTF_8);

        Pattern pattern1 = Pattern.compile("uid=(.{1,10})\" target=.{5,10}>(.{2,30})</a></td>\\n.{0,2}<td>(.{10,20})</td>\\n.{1,10}服务器.{0,10}提升卡");
        Pattern pattern2 = Pattern.compile("uid=(.{1,10})\" target=.{5,10}>(.{2,30})</a></td>\\n.{0,2}<td><span title=\"(.{10,20})\">.{1,20}</span></td>\\n.{1,10}服务器.{0,10}提升卡");

        Matcher matcher1 = pattern1.matcher(source);
        Matcher matcher2 = pattern2.matcher(source);

        bumpInfos.clear();
        while (matcher1.find()) {
            int uid = Integer.parseInt(matcher1.group(1));
            String username = matcher1.group(2);
            LocalDateTime dateTime = LocalDateTime.parse(matcher1.group(3), DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"));

            bumpInfos.add(new BumpInfo(uid, username, dateTime));
        }
        while (matcher2.find()) {
            int uid = Integer.parseInt(matcher2.group(1));
            String username = matcher2.group(2);
            LocalDateTime dateTime = LocalDateTime.parse(matcher2.group(3), DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"));

            bumpInfos.add(new BumpInfo(uid, username, dateTime));
        }
        lastUpdate = System.currentTimeMillis();
    }


}
