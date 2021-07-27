package com.molean.isletopia.message.handler;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.obj.SkinValueObject;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import org.bukkit.Bukkit;

public class SkinValueHandler implements MessageHandler<SkinValueObject> {

    public SkinValueHandler() {
        RedisMessageListener.setHandler("SkinValue", this,SkinValueObject.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject,SkinValueObject message) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () ->
                UniversalParameter.setParameter(message.getPlayer(), "SkinValue", message.getSkinValue()));
    }
}
