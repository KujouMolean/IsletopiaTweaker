package com.molean.isletopia.message.handler;

import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.obj.PlaySoundObject;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import com.molean.isletopia.shared.message.RedisMessageListener;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySoundHandler implements MessageHandler<PlaySoundObject> {

    public PlaySoundHandler() {
        RedisMessageListener.setHandler("PlaySound", this, PlaySoundObject.class);
    }

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject,PlaySoundObject message) {

        Player target = Bukkit.getPlayer(message.getPlayer());
        if (target == null) {
            return;
        }
        Sound sound = Sound.valueOf(message.getSoundName());
        target.playSound(target.getLocation(), sound, 1.0f, 1.0f);
    }
}
