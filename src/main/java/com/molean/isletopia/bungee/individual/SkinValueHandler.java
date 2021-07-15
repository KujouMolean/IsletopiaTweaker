package com.molean.isletopia.bungee.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.BukkitMessageListener;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.bungee.SkinValueObject;
import org.bukkit.Bukkit;

public class SkinValueHandler implements MessageHandler<SkinValueObject> {

    public SkinValueHandler() {
        BukkitMessageListener.setHandler("SkinValue", this,SkinValueObject.class);
    }

    @Override
    public void handle(SkinValueObject message) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () ->
                UniversalParameter.setParameter(message.getPlayer(), "SkinValue", message.getSkinValue()));
    }
}
