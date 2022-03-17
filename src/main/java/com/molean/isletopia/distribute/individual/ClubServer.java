package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.shared.database.ParameterDao;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.InventoryUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClubServer implements CommandExecutor, TabCompleter {
    public ClubServer() {
        Objects.requireNonNull(Bukkit.getPluginCommand("clubrealm")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("clubrealm")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage("club.realm.usage");
            return true;
        }
        String serverName;
        if (!args[0].startsWith("club_")) {
            serverName = "club_" + args[0];
        } else {
            serverName = args[0];
        }
        Tasks.INSTANCE.async(() -> {
            if (ServerInfoUpdater.getServers().contains(serverName)) {
                String requirements = ParameterDao.get("ClubRealm", serverName, "Requirements");
                String whitelist = ParameterDao.get("ClubRealm", serverName, "Whitelist");
                if (whitelist == null || !Arrays.asList(whitelist.split(",")).contains(player.getUniqueId().toString())) {
                    if (requirements != null && !requirements.isEmpty()) {
                        Material material = null;
                        try {
                            material = Material.valueOf(requirements);
                        } catch (Exception ignored) {
                        }

                        if (material == null) {
                            MessageUtils.fail(player, "club.realm.error");
                            return;
                        }
                        if (args.length <= 1 || !args[1].equals("confirm")) {
                            MessageUtils.strong(player, MessageUtils.getMessage(player, "club.realm.consume", Pair.of("item", LangUtils.get(player.locale(), material.translationKey()))));

                            MessageUtils.strong(player, MessageUtils.getMessage(player, "club.realm.confirm", Pair.of("server", serverName)));
                            return;
                        }
                        if (InventoryUtils.takeItem(player, material, 1)) {
                            ArrayList<String> strings = new ArrayList<>();
                            if (whitelist != null) {
                                strings.addAll(Arrays.asList(whitelist.split(",")));
                            }
                            strings.add(player.getUniqueId().toString());
                            ParameterDao.set("ClubRealm", serverName, "Whitelist", String.join(",", strings));
                        } else {
                            MessageUtils.fail(player, "club.realm.noItem");

                            return;
                        }
                    }
                }
                MessageUtils.strong(player, "club.realm.enter");
                ServerMessageUtils.switchServer(sender.getName(), serverName);
            } else {
                MessageUtils.fail(player, "club.realm.notFound");
            }
        });


        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 1) {
            for (String server : ServerInfoUpdater.getServers()) {
                if (server.startsWith("club_")) {
                    String substring = server.substring(5);
                    if (substring.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) {
                        strings.add(substring);
                    }
                }
            }
        }
        return strings;
    }
}
