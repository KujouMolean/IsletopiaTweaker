package com.molean.isletopia.infrastructure.infrastructures;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.ConfigUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
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

import java.util.*;

public class GuideBook implements CommandExecutor {


    public GuideBook() {
        Objects.requireNonNull(Bukkit.getPluginCommand("guide")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (!(sender instanceof Player))
                return;
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
            List<List<BaseComponent>> pages = getPages(type);
            if (pages == null) {
                sender.sendMessage("§c没有该类型的指南.");
                return;
            }
            for (List<BaseComponent> page : pages) {
                bookMeta.spigot().addPage(page.toArray(new BaseComponent[0]));

            }
            itemStack.setItemMeta(bookMeta);
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.openBook(itemStack);
            });
        });
        return true;
    }

    public List<List<BaseComponent>> getPages(String type) {
        ConfigUtils.reloadConfig("guide.yml");
        List<List<BaseComponent>> strings = new ArrayList<>();
        YamlConfiguration config = ConfigUtils.getConfig("guide.yml");
        ConfigurationSection pages = config.getConfigurationSection(type);
        if (pages == null) {
            return null;
        }
        Set<String> keys = pages.getKeys(false);
        for (String key : keys) {
            List<String> list = pages.getStringList(key);
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : list) {
                stringBuilder.append(s).append("\n");
            }
            strings.add(parsePage(stringBuilder.toString()));
        }
        return strings;
    }

    public List<BaseComponent> parsePage(String text) {
        String[] split = text.split("%");
        List<BaseComponent> baseComponents = new ArrayList<>();

        boolean hasLeft = false;
        for (String s : split) {
            if (hasLeft) {
                hasLeft = false;
                TextComponent textComponent = new TextComponent(s);
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guide " + s));
                baseComponents.addAll(Arrays.asList(new ComponentBuilder(textComponent).create()));
            } else {
                hasLeft = true;
                TextComponent textComponent = new TextComponent(s);
                textComponent.setClickEvent(null);
                baseComponents.addAll(Arrays.asList(new ComponentBuilder(textComponent).create()));
            }
        }
        return baseComponents;
    }
}
