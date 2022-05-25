package com.molean.isletopia.distribute;

import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.visit.MultiVisitMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@BukkitCommand("visit")
public class VisitCommand implements CommandExecutor, TabCompleter, Listener {
    private final PlayerPropertyManager playerPropertyManager;
    private final SidebarManager sidebarManager;
    private final ChargeCommitter chargeCommitter;

    public VisitCommand(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter) {
        this.chargeCommitter = chargeCommitter;
        this.sidebarManager = sidebarManager;
        this.playerPropertyManager = playerPropertyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        //visit player
        //visit server3 1 5
        //visit player n
        //visit #123123
        Player sourcePlayer = (Player) sender;
        Tasks.INSTANCE.async(() -> {
            if (args.length < 1) {
                return;
            }
            if (args[0].matches("server[0-9]{1,3}")) {
                if (args.length < 3) {
                    MessageUtils.fail(sourcePlayer, "visit.pos");
                    return;
                }
                int x, z;
                try {
                    x = Integer.parseInt(args[1]);
                    z = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    MessageUtils.fail(sourcePlayer, MessageUtils.getMessage(sourcePlayer, "visit.pos.notInt", Pair.of("args1", args[1]), Pair.of("args2", args[2])));
                    return;
                }
                IslandId islandId = new IslandId(args[0], x, z);
                Island island = IslandManager.INSTANCE.getIsland(islandId);
                if (island == null) {
                    MessageUtils.fail(sourcePlayer, MessageUtils.getMessage(sourcePlayer, "visit.island", Pair.of("island", islandId.toString())));

                    return;
                }
                IsletopiaTweakersUtils.universalPlotVisitByMessage(sourcePlayer, island.getIslandId());
                return;
            }

            if (args[0].matches("#[0-9]{1,10}")) {
                String s = args[0].replaceAll("#", "");
                int i = 0;
                try {
                    i = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    MessageUtils.fail(sourcePlayer, "visit.id");
                    return;
                }
                Island island = IslandManager.INSTANCE.getIsland(i);
                if (island == null) {
                    MessageUtils.fail(sourcePlayer, MessageUtils.getMessage(sourcePlayer, "visit.id.noIsland", Pair.of("id", i + "")));
                    return;
                }
                IsletopiaTweakersUtils.universalPlotVisitByMessage(sourcePlayer, island.getIslandId());
                return;
            }

            if (args[0].matches("#?[0-9a-zA-Z_]{3,18}")) {
                String target = args[0];
                UUID targetUUID = UUIDManager.get(target);
                if (targetUUID == null) {
                    targetUUID = UUIDManager.get("#" + target);
                }
                if (targetUUID == null) {
                    MessageUtils.fail(sourcePlayer, "island.visit.failed.noRegistration");
                    return;
                }

                List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(targetUUID);
                if (playerIslands.size() == 0) {
                    MessageUtils.fail(sourcePlayer, "island.visit.failed.noIsland");
                    return;
                }
                playerIslands.sort(Comparator.comparingInt(Island::getId));
                new MultiVisitMenu(playerPropertyManager, sidebarManager, chargeCommitter, sourcePlayer, playerIslands).open();

                return;
            }

            MessageUtils.fail(sourcePlayer, "visit.notSupport");
        });

        return true;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            if (!args[0].startsWith("#")) {
                List<String> playerNames = new ArrayList<>(UUIDManager.INSTANCE.getSnapshot().values());
                for (String server : ServerInfoUpdater.getServers()) {
                    if (server.startsWith("server")) {
                        playerNames.add(server);
                    }
                }
                playerNames.removeIf(s -> !s.toLowerCase().startsWith(args[0].toLowerCase()));
                return playerNames;
            }
        }
        return new ArrayList<>();
    }
}
