package com.molean.isletopia.story.command;


import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.story.story.Story;
import com.molean.isletopia.story.story.StoryManager;
import com.molean.isletopia.story.story.StoryTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class StoryCommand implements CommandExecutor, TabCompleter {
    public StoryCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("story")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("story")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            help("");
            return true;
        }
        if (!(sender instanceof Player)) {
            return true;
        }
        String opt = args[0].toLowerCase();
        switch (opt) {
            case "play":
                if (args.length < 3)
                    help("play");
                else
                    play((Player) sender, args[1],args[2]);
                break;
            case "list":
                list((Player) sender);
                break;
            case "reload":
                reload((Player) sender);
                break;
            default:
                help("");
        }


        return true;
    }

    private void reload(Player sender) {
        if (sender.isOp()) {
            StoryManager.reload();
        }
    }

    public void help(String helpItem) {
        String item = helpItem.toLowerCase();
        switch (item) {
            case "":
                break;
            case "play":
                break;
            case "list":
                break;
        }
    }

    public void play(Player player, String namespace, String name) {

        Story story = StoryManager.getStory(namespace, name);
        if (story == null) {
            player.sendMessage("故事不存在.");
            return;
        }
        StoryTask storyTask = new StoryTask(player, story);
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), storyTask);

    }

    public void list(Player player) {
        List<String> namespaces = StoryManager.getNamespaces();
        for (String namespace : namespaces) {
            player.sendMessage(namespace + ": " + String.join(",", StoryManager.getNames(namespace)));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
