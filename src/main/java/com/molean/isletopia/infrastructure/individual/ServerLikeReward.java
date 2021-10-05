package com.molean.isletopia.infrastructure.individual;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServerLikeReward implements CommandExecutor {

    private long lastUpdate = 0;
    private final List<UUID> likeUUIDs = new ArrayList<>();

    public ServerLikeReward() {
        Objects.requireNonNull(Bukkit.getPluginCommand("likereward")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {

            UUIDUtils.getOnlineUUID(sender.getName(), uuid -> {
                if (uuid == null) {
                    MessageUtils.fail(sender, "读取你的UUID失败!");
                    return;
                }

                if (System.currentTimeMillis() - lastUpdate > 1000 * 30) {
                    updateInformation();
                }

                if (likeUUIDs.isEmpty()) {
                    MessageUtils.fail(sender, "从NameMC读取点赞列表失败!");
                    return;
                }
                if (!likeUUIDs.contains(uuid)) {
                    MessageUtils.fail(sender, "你当前ID没有给服务器点赞!");
                    return;
                }

                List<String> parameterAsList = UniversalParameter.getParameterAsList("Molean", "ServerLikes");
                if (parameterAsList.contains(uuid.toString())) {
                    MessageUtils.fail(sender, "你已经领取过了!");
                    return;
                }
                Player player = (Player) sender;
                UniversalParameter.addParameter("Molean", "ServerLikes", uuid.toString());

                CommonResponseObject commonResponseObject = new CommonResponseObject();
                commonResponseObject.setMessage("玩家" + player.getName() + "在NameMC为服务器点赞,获得龙首奖励! 地址:https://zh-cn.namemc.com/server/play.molean.com");
                ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    HashMap<Integer, ItemStack> integerItemStackHashMap = player.getInventory().addItem(new ItemStack(Material.DRAGON_HEAD));
                    for (ItemStack value : integerItemStackHashMap.values()) {
                        player.getWorld().dropItem(player.getLocation(), value);
                    }
                });
            });
        });
        return true;
    }

    public void updateInformation() {
        byte[] bytes;
        try {
            URL url = new URL("https://api.namemc.com/server/play.molean.com/likes");
            InputStream inputStream = url.openStream();
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String source = new String(bytes, StandardCharsets.UTF_8);
        @SuppressWarnings("all")
        List<String> list = new Gson().fromJson(source, new TypeToken<List<String>>() {
        }.getType());
        likeUUIDs.clear();
        for (String s : list) {
            likeUUIDs.add(UUID.fromString(s));
        }

        lastUpdate = System.currentTimeMillis();
    }
}
