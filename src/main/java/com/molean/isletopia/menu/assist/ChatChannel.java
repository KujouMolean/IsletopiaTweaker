package com.molean.isletopia.menu.assist;

import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.shared.utils.ChatChannelUtils;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class ChatChannel extends ListMenu<String> {

    public ChatChannel(Player player) {
        super(player, Component.text(MessageUtils.getMessage(player,"menu.channel.title")));
        this.components(ChatChannelUtils.availableChannels);
        Set<String> channels = ChatChannelUtils.getChannels(player.getUniqueId());
        this.onClickSync(s -> {
        });
        this.onClickAsync(s -> {
            if (channels.contains(s)) {
                ChatChannelUtils.removeChannel(player.getUniqueId(), s);
                MessageUtils.success(player, MessageUtils.getMessage(player, "channel.exit", Pair.of("channel", s)));
                if (channels.size() == 1) {
                    MessageUtils.notify(player, "channel.empty");
                }
            } else {
                ChatChannelUtils.addChannel(player.getUniqueId(), s);
                MessageUtils.success(player, MessageUtils.getMessage(player, "channel.join", Pair.of("channel", s)));
            }
            new ChatChannel(player).open();
        });

        this.convertFunction(s -> {
            Material material;
            switch (s) {
                case "黑" -> material = Material.BLACK_WOOL;
                case "深蓝" -> material = Material.LAPIS_BLOCK;
                case "深绿" -> material = Material.GREEN_WOOL;
                case "湖蓝" -> material = Material.LIGHT_BLUE_WOOL;
                case "深红" -> material = Material.RED_WOOL;
                case "紫" -> material = Material.PURPLE_WOOL;
                case "金" -> material = Material.GOLD_BLOCK;
                case "灰" -> material = Material.LIGHT_GRAY_WOOL;
                case "深灰" -> material = Material.GRAY_WOOL;
                case "蓝" -> material = Material.BLUE_CONCRETE;
                case "绿" -> material = Material.LIME_WOOL;
                case "天蓝" -> material = Material.LIGHT_BLUE_CONCRETE;
                case "红" -> material = Material.RED_CONCRETE_POWDER;
                case "粉红" -> material = Material.PINK_WOOL;
                case "黄" -> material = Material.YELLOW_WOOL;
                case "白" -> material = Material.WHITE_WOOL;
                default -> material = Material.BARRIER;
            }
            ItemStackSheet itemStackSheet = new ItemStackSheet(material, ChatChannelUtils.getChannelColor(s) + s);
            if (s.equals("白")) {
                itemStackSheet.addLore(MessageUtils.getMessage(player, "channel.main"));
            }
            if (channels.contains(s)) {
                itemStackSheet.addLore(MessageUtils.getMessage(player,"channel.joined"));
                itemStackSheet.addEnchantment(Enchantment.ARROW_DAMAGE, 0);
                itemStackSheet.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            } else {
                itemStackSheet.addLore(MessageUtils.getMessage(player,"channel.notJoined"));
            }
            itemStackSheet.addLore("");
            Collection<UUID> playersInChannel = ChatChannelUtils.getPlayersInChannel(s);
            if (playersInChannel.size() > 0) {
                itemStackSheet.addLore(MessageUtils.getMessage(player, "channel.playerList"));
                int cnt = 0;
                ArrayList<UUID> uuids = new ArrayList<>(playersInChannel);
                StringBuilder line = new StringBuilder();
                line.append(" §7- ");
                for (int i = 0; i < uuids.size(); i++) {
                    line.append(UUIDManager.get(uuids.get(i)));
                    line.append("  ");
                    cnt++;
                    if (cnt % 4 == 0 || cnt == uuids.size()) {
                        itemStackSheet.addLore(line.toString());
                        line = new StringBuilder();
                        line.append(" §7- ");
                    }
                }
            }
            return itemStackSheet.build();
        });
        this.onCloseSync(null);
        this.onCloseAsync(() -> new MainMenu(player).open());
    }
}
