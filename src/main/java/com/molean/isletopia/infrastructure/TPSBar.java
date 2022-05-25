package com.molean.isletopia.infrastructure;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.annotations.DisableTask;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.utils.MSPTUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


@Singleton
@CommandAlias("tpsbar")
public class TPSBar extends BaseCommand {
    private final PlayerPropertyManager playerPropertyManager;

    BossBar bossBar = BossBar.bossBar(Component.text("test"), 0, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);

    public TPSBar(PlayerPropertyManager playerPropertyManager) {
        this.playerPropertyManager = playerPropertyManager;
    }


    @Interval(value = 20, async = true)
    public void updateBar() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!playerPropertyManager.isLoad(onlinePlayer.getUniqueId())) {
                continue;

            }
            if (playerPropertyManager.getPropertyAsBoolean(onlinePlayer, "TPSBar")) {
                onlinePlayer.showBossBar(bossBar);
            } else {
                onlinePlayer.hideBossBar(bossBar);
            }

        }
        double mspt = MSPTUtils.get();
        double tps = 20;
        float progress;
        BossBar.Color color = BossBar.Color.GREEN;
        if (mspt > 50) {
            tps = 1000 / mspt;
            progress = 1;
            if (mspt < 65) {
                color = BossBar.Color.YELLOW;
            } else {
                color = BossBar.Color.RED;
            }
        } else {
            progress = (float) (mspt / 50.0f);
        }
        Component deserialize = MiniMessage.miniMessage()
                .deserialize("<yellow><server>:</yellow> <gray>TPS(<tps>) MSPT(<mspt>)</gray>"
                        , Placeholder.component("server", Component.text(ServerInfoUpdater.getServerName()))
                        , Placeholder.component("tps", Component.text("%.2f".formatted(tps)))
                        , Placeholder.component("mspt", Component.text("%.2f".formatted(mspt))));

        bossBar.name(deserialize);
        bossBar.progress(progress);
        bossBar.color(color);

    }


    @DisableTask
    public void disableTask() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hideBossBar(bossBar);
        }
    }

    @Default
    public void onCommand(Player player) {
        boolean powerBar = playerPropertyManager.getPropertyAsBoolean(player, "TPSBar");
        playerPropertyManager.setPropertyAsync(player, "TPSBar", !powerBar + "");
    }
}
