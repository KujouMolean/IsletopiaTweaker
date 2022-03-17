package com.molean.isletopia.menu;

import com.molean.isletopia.shared.database.ClubDao;
import com.molean.isletopia.shared.model.Club;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClubListMenu extends ListMenu<Club> {

    private static List<Club> get() {
        try {
            return new ArrayList<>(ClubDao.getClubs());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ClubListMenu(Player player) {
        this(player, get());
    }

    public ClubListMenu(Player player, List<Club> clubs) {
        super(player, Component.text("社团列表"));

        this.components(new ArrayList<>(clubs));

        this.convertFunction(club -> {
            String icon = club.getIcon();
            Material material = Material.APPLE;
            try {
                material = Material.valueOf(icon.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            return ItemStackSheet.fromString(material, club.toString()).build();
        });
        this.onClickAsync(club -> {
            new ClubInfoMenu(player, club).open();
        });
    }


}
