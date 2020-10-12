package com.molean.isletopia.prompter.prompter;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandPrompts {
    private final String[] commandArgs;
    private final Player player;
    private int index;

    public CommandPrompts(String[] commandArgs, Player player) {
        this.commandArgs = commandArgs;
        this.player = player;
    }

    public void handle() {
        for (int i = 0; i < commandArgs.length; i++) {
            if (commandArgs[i].startsWith("@")) {
                index = i;
                String type = StringUtils.getSurroundedString(commandArgs[i], "@", "{");
                if (type == null)
                    continue;
                type = type.toLowerCase();
                String promptArgList = StringUtils.getSurroundedString(commandArgs[i], "{", "}");
                if (promptArgList == null)
                    continue;
                Prompter prompter;
                String[] promptArgs = promptArgList.split(",");
                switch (type) {
                    case "onlineplayerchooser":
                        if (promptArgs.length < 1)
                            return;
                        prompter = new OnlinePlayerPrompter(player, promptArgs[0]);
                        prompter.onComplete(this::complete);
                        prompter.open();
                        break;
                    case "playerchooser":
                        if (promptArgs.length < 1)
                            return;
                        List<String> playernames = new ArrayList<>(Arrays.asList(promptArgs).subList(1, promptArgs.length));
                        prompter = new PlayerPrompter(player, promptArgs[0], playernames);
                        prompter.onComplete(this::complete);
                        prompter.open();
                        break;
                    case "plottrustedchooser":
                        if (promptArgs.length < 1)
                            return;
                        prompter = new PlotTrustedPrompter(player, promptArgs[0]);
                        prompter.onComplete(this::complete);
                        prompter.open();
                        break;
                }
                break;
            }
        }
    }

    public void complete(String name) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            commandArgs[index] = name;
            StringBuilder cmd = new StringBuilder();
            for (String arg : commandArgs) {
                cmd.append(arg).append(" ");
            }
            Bukkit.getServer().dispatchCommand(player, cmd.toString());
            IsletopiaTweakers.getPlugin().getLogger().info(player.getName() + " issued prompter: " + cmd.toString());
        });
    }
}
