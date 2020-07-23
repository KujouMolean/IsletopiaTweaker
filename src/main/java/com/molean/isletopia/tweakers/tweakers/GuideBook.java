package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.ConfigUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuideBook implements CommandExecutor {



    public GuideBook() {
        Bukkit.getPluginCommand("guide").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        Player player = (Player) sender;
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setTitle("用户指南");
        bookMeta.setAuthor("Molean");
        List<String> pages = getPages();
        for (String page : pages) {
            bookMeta.addPage(page);
        }
        itemStack.setItemMeta(bookMeta);
        player.openBook(itemStack);
        return true;
    }

    public List<String> getPages() {
        ConfigUtils.reloadConfig("guide.yml");
        List<String> strings = new ArrayList<>();
        YamlConfiguration config = ConfigUtils.getConfig("guide.yml");
        ConfigurationSection pages = config.getConfigurationSection("pages");
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
