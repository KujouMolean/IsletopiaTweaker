package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.dialog.ConfirmDialog;
import com.molean.isletopia.shared.database.BumpDao;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.model.BumpInfo;
import com.molean.isletopia.shared.pojo.obj.ServerBumpObject;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerBumpReward implements CommandExecutor, TabCompleter {


    private final List<BumpInfo> bumpInfos = new ArrayList<>();

    private long lastUpdate = 0;

    public ServerBumpReward() throws SQLException {
        Objects.requireNonNull(Bukkit.getPluginCommand("bumpreward")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("bumpreward")).setExecutor(this);

    }

    public boolean hasPreviousBump(BumpInfo bumpInfo) {
        for (BumpInfo info : bumpInfos) {
            if (info.getDateTime().isBefore(bumpInfo.getDateTime())) {
                if (info.getDateTime().plusHours(4).isAfter(bumpInfo.getDateTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        new ConfirmDialog(MessageUtils.getMessage((Player) sender, "bump.rules")).accept(player -> {
            Tasks.INSTANCE.async( () -> {
                if (args.length < 1) {
                    MessageUtils.info(player, "bump.usage");
                    MessageUtils.info(player, "bump.usage.example");
                    return;
                }
                UUID uuid = player.getUniqueId();
                if (System.currentTimeMillis() - lastUpdate > 1000 * 30) {
                    updateInformation();
                }


                for (BumpInfo bumpInfo : bumpInfos) {
                    if (args[0].equalsIgnoreCase("debug")) {
                        PluginUtils.getLogger().info(bumpInfo.toString());
                    }

                    if (!bumpInfo.getDateTime().toLocalDate().isEqual(LocalDate.now())) {
                        continue;
                    }


                    //claimed, skip
                    try {
                        if (BumpDao.exist(bumpInfo)) {
                            continue;
                        }
                    } catch (SQLException e) {
                        MessageUtils.fail(player, "bump.failed.database");
                        return;
                    }
                    //not owned, skip
                    if (!bumpInfo.getUsername().equalsIgnoreCase(args[0])) {
                        continue;
                    }

                    //level 6
                    if (getPoints(bumpInfo.getUid()) < 500) {
                        MessageUtils.fail(player, "bump.failed.level");

                        return;
                    }


                    int bonus = 1;
                    if (!hasPreviousBump(bumpInfo)) {
                        bonus = 2;
                    }

                    try {
                        BumpDao.addBumpInfo(bumpInfo);
                    } catch (SQLException e) {
                        MessageUtils.fail(player, "bump.failed.database");
                        return;
                    }

                    ServerBumpObject serverBumpObject = new ServerBumpObject();
                    serverBumpObject.setPlayer(sender.getName());
                    serverBumpObject.setUser(args[0]);
                    serverBumpObject.setItems(new ArrayList<>());
                    ArrayList<ItemStack> itemStacks = new ArrayList<>();
                    Random random = new Random();
                    for (int i = 0; i < bonus; i++) {
                        itemStacks.add(new ItemStack(Material.SHULKER_BOX));
                        serverBumpObject.getItems().add(MessageUtils.getMessage(player, "bump.reward.shulkerBox"));
                        int beaconRand = random.nextInt(100);
                        MessageUtils.info(player, "本次信标随机数为" + beaconRand + "(小于10获得)");
                        int bundleRand = random.nextInt(100);
                        MessageUtils.info(player, "本次收纳袋随机数为" + bundleRand + "(小于5获得)");
                        int heartRand = random.nextInt(100);
                        MessageUtils.info(player, "本次海洋之心随机数为" + heartRand + "(小于10获得)");
                        int elytraRand = random.nextInt(100);
                        MessageUtils.info(player, "本次鞘翅随机数为" + elytraRand + "(小于1获得)");
                        if (beaconRand < 10) {
                            itemStacks.add(new ItemStack(Material.BEACON, 1));
                            serverBumpObject.getItems().add(MessageUtils.getMessage(player, "bump.reward.beacon"));
                            UniversalParameter.addParameter(uuid, "beacon", "true");
                            UniversalParameter.setParameter(uuid, "beaconReason", "bump");
                        }
                        if (bundleRand < 5) {
                            itemStacks.add(new ItemStack(Material.BUNDLE, 1));
                            serverBumpObject.getItems().add(MessageUtils.getMessage(player, "bump.reward.bundle"));
                        }
                        if (heartRand < 10) {
                            itemStacks.add(new ItemStack(Material.HEART_OF_THE_SEA, 1));
                            serverBumpObject.getItems().add(MessageUtils.getMessage(player, "bump.reward.heartOfTheSea"));
                        }
                        if (elytraRand == 0) {
                            itemStacks.add(new ItemStack(Material.ELYTRA, 1));
                            serverBumpObject.getItems().add(MessageUtils.getMessage(player, "bump.reward.elytra"));
                            UniversalParameter.addParameter(uuid, "elytra", "true");
                            UniversalParameter.setParameter(uuid, "elytraReason", "bump");
                        }
                    }


                   Tasks.INSTANCE.sync(() -> {
                        Collection<ItemStack> values = player.getInventory().addItem(itemStacks.toArray(new ItemStack[0])).values();
                        for (ItemStack value : values) {
                            player.getLocation().getWorld().dropItem(player.getLocation(), value);
                        }
                    });
                    ServerMessageUtils.sendMessage("proxy", "ServerBump", serverBumpObject);
                    return;
                }
                MessageUtils.fail(player, "bump.failed.notFound");
            });
        }).open((Player) sender);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {
        return null;
    }

    public int getPoints(int uid) {
        byte[] bytes = new byte[0];
        try {
            URL url = new URL("https://www.mcbbs.net/?" + uid);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.109 Safari/537.36");
            InputStream inputStream = urlConnection.getInputStream();
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String source = new String(bytes, StandardCharsets.UTF_8);
        {


            Pattern compile = Pattern.compile("<li><em>积分</em>(.{1,30})</li><li>");
            Matcher matcher = compile.matcher(source);

            while (matcher.find()) {
                try {
                    return  Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException ignored) {
                }
            }

        }
        {


            Pattern compile = Pattern.compile("<li><em>!credits!</em>(.{1,30})</li><li>");
            Matcher matcher = compile.matcher(source);

            int points = 0;

            while (matcher.find()) {
                try {
                    return Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException ignored) {
                }
            }

        }

        return -1;


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

            if (dateTime.toLocalDate().isEqual(LocalDate.now())) {
                bumpInfos.add(new BumpInfo(uid, username, dateTime));
            }
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
