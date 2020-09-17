package com.molean.isletopia.infrastructure.infrastructures;

import com.molean.isletopia.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GuideBook implements CommandExecutor {


    public GuideBook() {
        Objects.requireNonNull(Bukkit.getPluginCommand("guide")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        String type;
        if (args.length == 0) {
            type = "pages";
        } else {
            type = args[0];
        }
        Player player = (Player) sender;
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        assert bookMeta != null;
        bookMeta.setTitle("用户指南");
        bookMeta.setAuthor("Molean");
        List<String> pages = getPages(type);
        for (String page : pages) {
            bookMeta.addPage(page);
        }
        itemStack.setItemMeta(bookMeta);
        player.openBook(itemStack);
        return true;
    }

    public List<String> getPages(String type) {
        ConfigUtils.reloadConfig("guide.yml");
        List<String> strings = new ArrayList<>();
        YamlConfiguration config = ConfigUtils.getConfig("guide.yml");
        ConfigurationSection pages = config.getConfigurationSection(type);
        assert pages != null;
        Set<String> keys = pages.getKeys(false);
        for (String key : keys) {
            List<String> list = pages.getStringList(key);
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : list) {
                stringBuilder.append(s).append("\n");
            }
            strings.add(stringBuilder.toString());
        }
        return strings;
    }
}
