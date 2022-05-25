package com.molean.isletopia.message;

import com.molean.isletopia.shared.annotations.MessageHandlerType;
import com.molean.isletopia.shared.MessageHandler;
import com.molean.isletopia.shared.pojo.req.PlaySoundRequest;
import com.molean.isletopia.shared.pojo.WrappedMessageObject;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@MessageHandlerType(PlaySoundRequest.class)
public class PlaySoundRequestHandler implements MessageHandler<PlaySoundRequest> {
    @Override
    public void handle(WrappedMessageObject wrappedMessageObject, PlaySoundRequest message) {
        String targetPlayer = message.getTargetPlayer();
        Player player = Bukkit.getPlayerExact(targetPlayer);
        if (player == null) {
            return;
        }
        Sound sound = null;
        try {
            sound = Sound.valueOf(message.getSoundName());
        } catch (IllegalArgumentException ignored) {

        }
        if (sound == null) {
            return;
        }
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }
}
