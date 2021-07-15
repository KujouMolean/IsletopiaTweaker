package com.molean.isletopia.bungee.individual;

import com.google.common.collect.Iterables;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.BukkitMessageListener;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.bungee.BeaconRequestObject;
import com.molean.isletopia.shared.bungee.CommonResponseObject;
import com.molean.isletopia.shared.utils.BukkitBungeeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BeaconRequestHandler implements MessageHandler<BeaconRequestObject> {
    public BeaconRequestHandler() {
        BukkitMessageListener.setHandler("BeaconRequest", this, BeaconRequestObject.class);
    }

    @Override
    public void handle(BeaconRequestObject message) {
        String player = message.getPlayer();
        String reason = message.getReason();
        UniversalParameter.addParameter("Molean", "beacon", player);
        UniversalParameter.setParameter(player, "beaconReason", reason);
        String resp = player + " 获得了信标权限, 原因是: " + reason;
        CommonResponseObject commonResponseObject = new CommonResponseObject(resp);
        Player first = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        assert first != null;
        BukkitBungeeUtils.sendBungeeMessage(first, "CommonResponse", commonResponseObject);
    }
}
