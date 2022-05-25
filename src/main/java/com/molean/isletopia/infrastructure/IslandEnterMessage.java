package com.molean.isletopia.infrastructure;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Singleton
public class IslandEnterMessage implements Listener {


    public void sendEnterMessage(Player player, IslandId to) {
        IslandManager.INSTANCE.getLocalIsland(to, island -> {
            if (island == null) {
                return;
            }

            String alias = island.getName();
            String title;
            if (alias == null || alias.isEmpty()) {
                title = "§6%1%:%2%,%3%"
                        .replace("%1%", IsletopiaTweakersUtils.getLocalServerName())
                        .replace("%2%", to.getX() + "")
                        .replace("%3%", to.getZ() + "");
            } else {
                title = "§6%1%:%2%"
                        .replace("%1%", IsletopiaTweakersUtils.getLocalServerName())
                        .replace("%2%", alias);
            }
            String s = UUIDManager.get(island.getUuid());
            if (s == null) {
                s = "Unknown";
            }
            String message = MessageUtils.getMessage(player, "island.enter.subtitle", Pair.of("owner", s));
            player.showTitle(Title.title(Component.text(title), Component.text(message)));
        });

    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerIslandChangeEvent event) {
        LocalIsland to = event.getTo();
        if (to == null) {
            return;
        }
        sendEnterMessage(event.getPlayer(), to.getIslandId());
    }

}
