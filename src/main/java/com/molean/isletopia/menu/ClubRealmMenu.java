package com.molean.isletopia.menu;

import com.molean.isletopia.shared.database.ParameterDao;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClubRealmMenu extends ListMenu<String> {

    public ClubRealmMenu(Player player) {
        super(player, Component.text(MessageUtils.getMessage(player, "menu.clubrealm.title")));
        this.components(ParameterDao.targets("ClubRealm"));
        Map<String, Integer> players = new HashMap<>();
        Map<UUID, String> uuidServerMap = ServerInfoUpdater.getUUIDServerMap();

        for (UUID uuid :uuidServerMap.keySet()) {
            String s = uuidServerMap.get(uuid);
            Integer orDefault = players.getOrDefault(s, 0);
            players.put(s, orDefault + 1);
        }
        this.convertFunction(s -> {
            String icon = ParameterDao.get("ClubRealm", s, "Icon");
            String title = ParameterDao.get("ClubRealm", s, "Title");
            String description = ParameterDao.get("ClubRealm", s, "Description");
            Material material = null;
            try {
                material = Material.valueOf(icon);
            } catch (Exception ignored) {
            }
            if (material == null) {
                material = Material.APPLE;
            }
            ItemStackSheet itemStackSheet = new ItemStackSheet(material);
            if (title == null) {
                itemStackSheet.display(s);
            } else {
                title = title
                        .replaceAll("<online>", players.getOrDefault(s,0) + "")
                        .replaceAll("#", " ")
                        .replaceAll("&", "§");
                itemStackSheet.display(title);

            }
            if (description != null) {
                description = description
                        .replaceAll("<online>", players.getOrDefault(s, 0) + "")
                        .replaceAll("#", " ")
                        .replaceAll("&", "§");

                String[] split = description.split("\\[nl]");
                for (String s1 : split) {
                    itemStackSheet.addLore(s1);
                }

            }
            return itemStackSheet.build();
        });
        this.onClickSync(s -> {
            player.performCommand("clubrealm " + s);
        });
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main")).build())
                .onCloseSync(() -> {
                })
                .onCloseAsync(() -> new MainMenu(player).open());

    }
}
