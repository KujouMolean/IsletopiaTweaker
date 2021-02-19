package com.molean.isletopia.infrastructure.individual;

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

            List<List<BaseComponent>> pages = getPages(player,type);
            if (pages == null) {
                sender.sendMessage(MessageUtils.getMessage("error.guide.non-exist"));
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

    public List<List<BaseComponent>> getPages(Player player, String type) {
        String guide;
        if(player.getLocale().toLowerCase().startsWith("zh")){
            guide = "guide.yml";
        }else{
            guide = "guide_en.yml";
        }
        ConfigUtils.reloadConfig(guide);
        List<List<BaseComponent>> strings = new ArrayList<>();
        YamlConfiguration config = ConfigUtils.getConfig(guide);
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

                if (s.contains("#")) {
                    String[] sSplit = s.split("#");
                    if (sSplit.length != 3) {
                        continue;
                    }
                    TextComponent textComponent = new TextComponent(sSplit[0]);
                    switch (sSplit[1]) {
                        case "cmd":
                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, sSplit[2]));
                            break;
                        case "url":
                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, sSplit[2]));
                            break;
                        case "page":
                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, sSplit[2]));
                            break;
                        case "copy":
                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, sSplit[2]));
                            break;
                        case "file":
                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, sSplit[2]));
                            break;
                    }
                    baseComponents.addAll(Arrays.asList(new ComponentBuilder(textComponent).create()));
                } else {
                    TextComponent textComponent = new TextComponent(s);
                    s = s.replaceAll("§.", "");
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guide " + s));
                    baseComponents.addAll(Arrays.asList(new ComponentBuilder(textComponent).create()));
                }

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
