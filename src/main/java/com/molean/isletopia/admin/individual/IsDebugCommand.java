package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.music.my.Note;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.molean.isletopia.music.my.SongParseTest.parse;

public class IsDebugCommand implements CommandExecutor, Listener {

    public IsDebugCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("isdebug")).setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void serialize(List<List<Note>> music) {
        ArrayList<String> strings = new ArrayList<>();
        int max = 0;
        for (List<Note> notes : music) {
            if (notes != null) {
                max = Math.max(notes.size(), max);

            }
        }
        for (int i = 0; i < max; i++) {
            strings.add("");
        }
        for (List<Note> notes : music) {
            if (notes == null) {
                for (int i = 0; i < strings.size(); i++) {
                    String s = strings.get(i);
                    s += "#";
                    strings.set(i, s);
                }
                continue;
            }
            ArrayList<Note> tempNotes = new ArrayList<>(notes);
            for (int i = 0; i < strings.size(); i++) {
                String s = strings.get(i);
                if (!tempNotes.isEmpty()) {
                    s += tempNotes.get(0).encode();
                    tempNotes.remove(0);
                } else {
                    s += "#";
                }
                strings.set(i, s);
            }
        }
        for (String string : strings) {
            System.out.println(string);

        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
//        Player player = (Player) sender;
        File file = new File("/Users/molean/IdeaProjects/IsletopiaTweaker/songs/1.nbs");
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<List<Note>> parse = parse(fileInputStream);
        serialize(parse);
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), (task) -> {
            if (parse.isEmpty()) {
                task.cancel();
                return;
            }
            List<Note> notes = parse.get(0);
            parse.remove(0);
            if (notes == null) {
                return;
            }
            for (Note note : notes) {
//                note.play(player.getLocation());
            }
        }, 1, 1);
        return true;
    }


}
