package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.ConfigUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GuideBook implements CommandExecutor {
    public GuideBook() {
        Objects.requireNonNull(Bukkit.getPluginCommand("guide")).setExecutor(this);
        List<String> resources = new ArrayList<>();
        resources.add("guide_en.yml");
        resources.add("guide.yml");
        for (String resource : resources) {
            InputStream inputStream = IsletopiaTweakers.getPlugin().getResource(resource);
            String file = IsletopiaTweakers.getPlugin().getDataFolder() + "/" + resource;
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                if (inputStream != null) {
                    outputStream.write(inputStream.readAllBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            bookMeta.setAuthor("Molean");
            bookMeta.setTitle("Guide");

            List<List<Component>> pages = getPages(player, type);
            if (pages == null) {
                sender.sendMessage("§c没有该类型的指南.");
                return;
            }
            for (List<Component> page : pages) {
                bookMeta.addPages(page.toArray(new Component[0]));

            }
            itemStack.setItemMeta(bookMeta);
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> player.openBook(itemStack));
        });
        return true;
    }

    public List<List<Component>> getPages(Player player, String type) {
        ConfigUtils.reloadConfig("guide.yml");
        List<List<Component>> strings = new ArrayList<>();
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

    public List<Component> parsePage(String text) {
        String[] split = text.split("%");
        List<Component> components = new ArrayList<>();

        boolean hasLeft = false;
        for (String s : split) {
            if (hasLeft) {
                hasLeft = false;

                if (s.contains("#")) {
                    String[] sSplit = s.split("#");
                    if (sSplit.length != 3) {
                        continue;
                    }
                    Component textComponent = Component.text(sSplit[0]);
                    switch (sSplit[1]) {
                        case "cmd":
                            textComponent = textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, sSplit[2]));
                            break;
                        case "url":
                            textComponent = textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, sSplit[2]));
                            break;
                        case "page":
                            textComponent = textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.CHANGE_PAGE, sSplit[2]));
                            break;
                        case "copy":
                            textComponent = textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, sSplit[2]));
                            break;
                        case "file":
                            textComponent = textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_FILE, sSplit[2]));
                            break;
                    }
                    components.add(textComponent);
                } else {
                    Component textComponent = Component.text(s);
                    s = s.replaceAll("§.", "");
                    textComponent = textComponent.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/guide " + s));
                    components.add(textComponent);
                }

            } else {
                hasLeft = true;
                Component textComponent = Component.text(s);
                textComponent = textComponent.clickEvent(null);
                components.add(textComponent);
            }
        }
        return components;
    }
}
