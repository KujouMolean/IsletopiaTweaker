package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.other.ConfirmDialog;
import com.molean.isletopia.shared.database.BumpDao;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.model.BumpInfo;
import com.molean.isletopia.shared.pojo.obj.ServerBumpObject;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.utils.MessageUtils;
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
        BumpDao.checkTable();
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

        new ConfirmDialog("""
                领取顶帖奖励前，你必须要知道的几件事情：1.请不要在论坛过度水贴以至于被封号；2.请不要在论坛开小号为服务器顶帖；3.不要花钱购买第三方顶帖卡。违反以上规则你会被封禁。
                """).accept(player -> {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                if (args.length < 1) {
                    sender.sendMessage("请按要求输入：/bumpreward MCBBS用户名");
                    sender.sendMessage("例如 /bumpreward Molean");
                    return;
                }
                UUID uuid = player.getUniqueId();
                if (System.currentTimeMillis() - lastUpdate > 1000 * 30) {
                    updateInformation();
                }


                for (BumpInfo bumpInfo : bumpInfos) {
                    if (args[0].equalsIgnoreCase("debug")) {
                        System.out.println(bumpInfo);
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
                        MessageUtils.fail(sender,"数据库错误，请联系管理员。");
                        return;
                    }
                    //not owned, skip
                    if (!bumpInfo.getUsername().equalsIgnoreCase(args[0])) {
                        continue;
                    }


                    int bonus = 1;
                    if (!hasPreviousBump(bumpInfo)) {
                        bonus = 2;
                    }

                    try {
                        BumpDao.addBumpInfo(bumpInfo);
                    } catch (SQLException e) {
                        MessageUtils.fail(sender,"数据库错误，请联系管理员。");
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
                        serverBumpObject.getItems().add("潜影盒");
                        if (random.nextInt(100) < 10) {
                            itemStacks.add(new ItemStack(Material.BEACON, 1));
                            serverBumpObject.getItems().add("信标");
                            UniversalParameter.addParameter(uuid, "beacon", "true");
                            UniversalParameter.setParameter(uuid, "beaconReason", "顶贴");
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
                            UniversalParameter.addParameter(uuid, "elytra","true");
                            UniversalParameter.setParameter(uuid, "elytraReason", "顶贴");
                        }
                    }


                    Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                        Collection<ItemStack> values = player.getInventory().addItem(itemStacks.toArray(new ItemStack[0])).values();
                        for (ItemStack value : values) {
                            player.getLocation().getWorld().dropItem(player.getLocation(), value);
                        }
                    });
                    ServerMessageUtils.sendMessage("waterfall", "ServerBump", serverBumpObject);
                    return;
                }
                MessageUtils.fail(sender,"你还没有顶帖，或者ID输入错误，请查证后重新领取。");
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

            if (dateTime.toLocalDate().isEqual(LocalDate.now())) {
                bumpInfos.add(new BumpInfo(uid, username, dateTime));
            }
        }
        lastUpdate = System.currentTimeMillis();
    }
}
