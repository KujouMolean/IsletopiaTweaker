package com.molean.isletopia.tutor.individual;

import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.other.ConfirmDialog;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SkipTutorCommand implements CommandExecutor {
    public SkipTutorCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("skiptutor")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        new ConfirmDialog("""
                跳过新手引导的操作不可逆,你确认这么做吗?
                """).accept(player1 -> {
            UniversalParameter.setParameter(player.getName(), "TutorStatus", "Skip");
            HelpTutor.onQuit(player);
            IronTutor.onQuit(player);
            LogTutor.onQuit(player);
            MobFarmTutor.onQuit(player);
            StoneTutor.onQuit(player);
            VillagerTutor.onQuit(player);
        }).open(player);

        return true;
    }
}
