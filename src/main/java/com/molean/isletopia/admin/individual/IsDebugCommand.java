package com.molean.isletopia.admin.individual;

import com.google.gson.Gson;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.mail.InboxObject;
import com.molean.isletopia.mail.MailObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class IsDebugCommand implements CommandExecutor {
    public IsDebugCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("isdebug")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        String inbox = UniversalParameter.getParameter(player.getName(), "Inbox");

        InboxObject inboxObject;
        if (inbox != null && !inbox.isEmpty()) {
            inboxObject = new Gson().fromJson(inbox, InboxObject.class);
        } else {
            inboxObject = new InboxObject();
        }


        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && !item.getType().isAir()) {
                itemStacks.add(player.getInventory().getItem(i));
                player.getInventory().setItem(i, null);
            }

        }
        MailObject mailObject = new MailObject(player.getName(), args[0], args[1], itemStacks);
        if (inboxObject.getMailObjectList() == null) {
            inboxObject.setMailObjectList(new ArrayList<>());
        }
        inboxObject.getMailObjectList().add(mailObject);

        UniversalParameter.setParameter(player.getName(), "Inbox", new Gson().toJson(inboxObject));

        return true;
    }

}
