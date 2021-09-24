package com.molean.isletopia.mail;

import com.google.gson.Gson;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.other.ConfirmDialog;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MailCommand implements CommandExecutor, TabCompleter {
    public MailCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("mail")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("mail")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("/mail 玩家 标题 内容");
            return true;
        }

        new ConfirmDialog("你的物品栏会被全部随右键发送给该玩家，是否确认？").accept(player -> {
            String target = args[0];
            String title = args[1];
            StringBuilder message = new StringBuilder("\n");
            for (int i = 2; i < args.length; i++) {
                message.append(args[i]);
                message.append("\n");
            }
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && !item.getType().isAir()) {
                    itemStacks.add(player.getInventory().getItem(i));
                    player.getInventory().setItem(i, null);
                }

            }

            MailObject mailObject = new MailObject(player.getName(), title, message.toString(), itemStacks);

            String inbox = UniversalParameter.getParameter(target, "Inbox");

            InboxObject inboxObject;
            if (inbox != null && !inbox.isEmpty()) {
                inboxObject = new Gson().fromJson(inbox, InboxObject.class);
            } else {
                inboxObject = new InboxObject();
            }
            if (inboxObject.getMailObjectList() == null) {
                inboxObject.setMailObjectList(new ArrayList<>());

            }
            inboxObject.getMailObjectList().add(mailObject);
            UniversalParameter.setParameter(target, "Inbox", new Gson().toJson(inboxObject));
        }).open((Player) sender);


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return null;
        } else {
            return new ArrayList<>();
        }
    }
}
