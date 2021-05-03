package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerBumpReward implements CommandExecutor, TabCompleter {

    public static class BumpInfo {
        private int uid;
        private String username;
        private LocalDateTime dateTime;

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
                if (bumpInfo.dateTime.toLocalDate().isEqual(LocalDate.now()) && bumpInfo.username.equalsIgnoreCase(args[0])) {
                    String format = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    UniversalParameter.setParameter(sender.getName(), "lastBumpReward", format);
                    Player player = (Player) sender;
                    player.getInventory().addItem(new ItemStack(Material.SHULKER_BOX, 1));


                    Random random = new Random();
                    if (random.nextInt(100) < 10) {
                        player.getInventory().addItem(new ItemStack(Material.BEACON, 1));
                    }
                    if (random.nextInt(100) < 10) {
                        player.getInventory().addItem(new ItemStack(Material.HEART_OF_THE_SEA, 1));
                    }
                    if (random.nextInt(100) == 0) {
                        player.getInventory().addItem(new ItemStack(Material.ELYTRA, 1));
                        UniversalParameter.addParameter("Molean", "elytra",player.getName());
                    }

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
        String source = new String(bytes);

        Pattern pattern1 = Pattern.compile("<td><a " +
                "href=\"home.php\\?mod=space&amp;uid=(.{3,30})\" " +
                "target=\"_blank\">(.{3,30})</a></td>\n" +
                "<td>(.{3,30})</td>\n" +
                "<td >提升\\(服务器提升卡\\)</td>");
        Pattern pattern2 = Pattern.compile("<td><a " +
                "href=\"home.php\\?mod=space&amp;uid=(.{3,30})\" " +
                "target=\"_blank\">(.{3,30})</a></td>\n" +
                "<td><span title=\"(.{3,30})\">.{1,20}</span></td>\n" +
                "<td >提升\\(服务器提升卡\\)</td>");

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
