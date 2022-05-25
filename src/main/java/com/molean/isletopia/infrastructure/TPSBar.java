package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MSPTUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;


@BukkitCommand("tpsbar")
public class TPSBar implements Listener, CommandExecutor {
    private PlayerPropertyManager playerPropertyManager;

    public TPSBar(PlayerPropertyManager playerPropertyManager) {
        this.playerPropertyManager = playerPropertyManager;
        BossBar bossBar = BossBar.bossBar(Component.text("test"), 0, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);

        Tasks.INSTANCE.intervalAsync(20, () -> {
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
        });
        Tasks.INSTANCE.addDisableTask("Remove tps bar", () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hideBossBar(bossBar);
            }
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        boolean powerBar = playerPropertyManager.getPropertyAsBoolean(player, "TPSBar");
        playerPropertyManager.setPropertyAsync(player, "TPSBar", !powerBar + "");
        return true;
    }
}
