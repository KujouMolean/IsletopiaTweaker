package com.molean.isletopia.bungee.individual;

import com.molean.isletopia.shared.BukkitMessageListener;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.bungee.PlaySoundObject;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySoundHandler implements MessageHandler<PlaySoundObject> {

    public PlaySoundHandler() {
        BukkitMessageListener.setHandler("PlaySound", this,PlaySoundObject.class);
    }

    @Override
    public void handle(PlaySoundObject message) {

        Player target = Bukkit.getPlayer(message.getPlayer());
        if (target == null) {
            return;
        }
        Sound sound = Sound.valueOf(message.getSoundName());
        target.playSound(target.getLocation(), sound, 1.0f, 1.0f);
    }
}
