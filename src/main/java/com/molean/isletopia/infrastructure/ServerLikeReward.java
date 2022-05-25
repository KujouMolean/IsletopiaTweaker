package com.molean.isletopia.infrastructure.individual;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.shared.utils.UUIDManager;
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

@Singleton
@BukkitCommand("likereward")
public class ServerLikeReward implements CommandExecutor {

    private long lastUpdate = 0;
    private final List<UUID> likeUUIDs = new ArrayList<>();


    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        Player player = (Player) sender;
        Tasks.INSTANCE.async(() -> {

            UUIDManager.getOnline(player.getName(), uuid -> {
                if (uuid == null) {
                    MessageUtils.fail(player, "like.failed.uuid");
                    return;
                }

                if (System.currentTimeMillis() - lastUpdate > 1000 * 30) {
                    updateInformation();
                }

                if (likeUUIDs.isEmpty()) {
                    MessageUtils.fail(player, "like.failed.empty");
                    return;
                }
                if (!likeUUIDs.contains(uuid)) {
                    MessageUtils.fail(player, "like.failed.notFound");
                    return;
                }

                List<String> parameterAsList = UniversalParameter.getParameterAsList(UUIDManager.get("Molean"), "ServerLikes");
                if (parameterAsList.contains(uuid.toString())) {
                    MessageUtils.fail(player, "like.failed.claimed");
                    return;
                }
                UUID molean = UUIDManager.get("Molean");
                assert molean != null;
                UniversalParameter.addParameter(molean, "ServerLikes", uuid.toString());
                CommonResponseObject commonResponseObject = new CommonResponseObject();
                commonResponseObject.setMessage(MessageUtils.getMessage(player, "like.success", Pair.of("player", player.getName())) + "https://zh-cn.namemc.com/server/play.molean.com");
                ServerMessageUtils.sendMessage("proxy", "CommonResponse", commonResponseObject);
                Tasks.INSTANCE.sync((() -> {
                    HashMap<Integer, ItemStack> integerItemStackHashMap = player.getInventory().addItem(new ItemStack(Material.DRAGON_HEAD));
                    for (ItemStack value : integerItemStackHashMap.values()) {
                        player.getWorld().dropItem(player.getLocation(), value);
                    }
                }));
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
