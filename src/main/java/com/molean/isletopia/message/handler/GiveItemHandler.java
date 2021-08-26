package com.molean.isletopia.message.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.pojo.req.GiveItemRequestObject;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GiveItemHandler implements MessageHandler<GiveItemRequestObject> {
    public GiveItemHandler() {
        RedisMessageListener.setHandler("GiveItemRequest", this, GiveItemRequestObject.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, GiveItemRequestObject message) {
        String player = message.getPlayer();

        boolean noResponse = false;

        if (player.startsWith("#")) {
            player = player.substring(1);
            noResponse = true;
        }

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
            return;
        }

        CommonResponseObject commonReponseObject = new CommonResponseObject();
        Material material;
        try {
            material = Material.valueOf(message.getMaterial().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            commonReponseObject.setMessage("失败, " + message.getMaterial() + " 不存在!");
            if (!noResponse) {

                ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonReponseObject);
            }
            return;
        }

        ItemStack itemStack = new ItemStack(material, message.getAmount());
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (message.getName() != null && !message.getName().isEmpty()) {
            itemMeta.displayName(Component.text(message.getName()));
        }
        List<Component> componentList = new ArrayList<>();
        if (message.getLores() != null && !message.getLores().isEmpty()) {
            for (String lore : message.getLores()) {
                componentList.add(Component.text(lore));
            }
            itemMeta.lore(componentList);
        }
        itemStack.setItemMeta(itemMeta);
        bukkitPlayer.getInventory().addItem(itemStack);
        commonReponseObject.setMessage("成功, " + message.getPlayer() + " 已经收到 " + message.getMaterial() + "!");
        if (!noResponse) {
            ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonReponseObject);
        }
    }
}