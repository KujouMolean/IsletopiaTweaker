package com.molean.isletopia.bungee.individual;

import com.google.common.collect.Iterables;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.BukkitMessageListener;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.bungee.CommonResponseObject;
import com.molean.isletopia.shared.bungee.ElytraRequestObject;
import com.molean.isletopia.shared.utils.BukkitBungeeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ElytraRequestHandler implements MessageHandler<ElytraRequestObject> {
    public ElytraRequestHandler() {
        BukkitMessageListener.setHandler("ElytraRequest", this, ElytraRequestObject.class);
    }

    @Override
    public void handle(ElytraRequestObject message) {
        String player = message.getPlayer();
        String reason = message.getReason();
        UniversalParameter.addParameter("Molean", "elytra", player);
        UniversalParameter.setParameter(player, "elytraReason", reason);
        String resp = player + " 获得了鞘翅权限, 原因是: " + reason;
        CommonResponseObject commonResponseObject = new CommonResponseObject(resp);
        Player first = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        assert first != null;
        BukkitBungeeUtils.sendBungeeMessage(first, "CommonResponse", commonResponseObject);

    }
}
