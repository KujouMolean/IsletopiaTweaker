package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.NoteUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Music implements CommandExecutor {
    public Music() {
        Objects.requireNonNull(Bukkit.getPluginCommand("music")).setExecutor(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 2) {
            MessageUtils.notify(sender, "/music 音符间隔(t) 音符序列 ...");
            return true;
        }
        int interval;
        try {
            interval = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            MessageUtils.notify(sender, "/music 音符间隔(t) 音符序列 ...");
            return true;
        }
        if (interval > 20 || interval < 1) {
            MessageUtils.notify(sender, "间隔无效，请重新输入");
            return true;
        }
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(args));
        strings.remove(0);
        NoteUtils.playMulti((Player) sender, interval, strings);
        return true;
    }
}
