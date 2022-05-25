package com.molean.isletopia.message;

import com.molean.isletopia.shared.annotations.MessageHandlerType;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.obj.PlaySoundObject;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


@MessageHandlerType(PlaySoundObject.class)
public class PlaySoundHandler implements MessageHandler<PlaySoundObject> {

    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, PlaySoundObject message) {

        Player target = Bukkit.getPlayerExact(message.getPlayer());
        if (target == null) {
            return;
        }
        Sound sound = Sound.valueOf(message.getSoundName());
        target.playSound(target.getLocation(), sound, 1.0f, 1.0f);
    }
}
