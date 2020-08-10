package com.molean.isletopia.prompter.prompter;

import com.molean.isletopia.prompter.util.StringUtils;
import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandPrompts {
    private String[] commandArgs;
    private Player player;
    private int index;
    private boolean sudo;

    public CommandPrompts(String[] commandArgs, Player player, boolean sudo) {
        this.commandArgs = commandArgs;
        this.player = player;
        this.sudo = sudo;
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
                        List<String> playernames = new ArrayList<>();
                        for (int j = 1; j < promptArgs.length; j++) {
                            playernames.add(promptArgs[j]);
                        }
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
            boolean isOp = player.isOp();
            try {
                if (sudo) player.setOp(true);
                Bukkit.getServer().dispatchCommand(player, cmd.toString());
                IsletopiaTweakers.getPlugin().getLogger().info(player.getName() + " issued prompter: " + cmd.toString());
            } finally {
                if (sudo) player.setOp(isOp);
            }
        });
    }
}
