package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class UserTokenGenerator implements CommandExecutor {

    public UserTokenGenerator() {
        Objects.requireNonNull(Bukkit.getPluginCommand("usertoken")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.getName().startsWith("#")) {
            MessageUtils.fail(sender, "仅离线用户可设置皮肤");
            return true;
        }

        int token = new Random().nextInt(90000000) + 10000000;
        RedisUtils.getCommand().setex("UserToken:" + token, 60 * 5L, sender.getName());
        MessageUtils.notify(sender, "你的用户凭证为: " + token);
        MessageUtils.notify(sender, "该凭证仅在5分钟内有效!(如果重新生成,则直接失效)");

        return true;
    }
}
