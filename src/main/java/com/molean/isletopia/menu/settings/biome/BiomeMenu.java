package com.molean.isletopia.menu.settings.biome;

import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

public class BiomeMenu extends ListMenu<Biome> {


    public BiomeMenu(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter, Player player) {

        super(player, Component.text(MessageUtils.getMessage(player, "menu.member.biome.title")));
        List<Biome> values = new ArrayList<>(List.of(Biome.values()));
        values.remove(Biome.CUSTOM);
        this.components(values);
        Biome currentBiome = player.getLocation().getBlock().getBiome();
        this.convertFunction(biome -> {
            String name = MessageUtils.getMessage(player, "menu.member.biome.unknown", Pair.of("biome", biome.name()));
            Material icon = Material.PLAYER_HEAD;
            try {
                LocalBiome localBiome = LocalBiome.valueOf(biome.name().toUpperCase());
                String key = "biome." + biome.getKey().namespace() + "." + biome.getKey().value();
                name = LangUtils.get(player.locale(), key);
                icon = localBiome.getIcon();
            } catch (IllegalArgumentException ignore) {
            }
            ItemStackSheet itemStackSheet = new ItemStackSheet(icon, "§f" + name);
            if (currentBiome.name().equalsIgnoreCase(biome.name())) {
                itemStackSheet.addItemFlag(ItemFlag.HIDE_ENCHANTS);
                itemStackSheet.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                String display = itemStackSheet.display();
                itemStackSheet.display(MessageUtils.getMessage(player, "menu.biome.current", Pair.of("current", display)));
            }
            return itemStackSheet.build();
        });
        this.onClickSync(biome -> {
            LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);

            assert currentPlot != null;

            if (currentPlot.isChangingBiome()) {
                MessageUtils.fail(player, "menu.biome.changing");
                player.closeInventory();
                return;
            }


            String name = MessageUtils.getMessage(player, "menu.member.biome.unknown", Pair.of("biome", biome.name()));
            ;
            try {
                String key = "biome." + biome.getKey().namespace() + "." + biome.getKey().value();
                name = LangUtils.get(player.locale(), key);
            } catch (IllegalArgumentException ignore) {
            }
            if (player.getUniqueId().equals(currentPlot.getUuid())) {
                MessageUtils.info(player, "menu.biome.start");
                String finalName = name;

                currentPlot.setBiome(player.getWorld(), biome, () -> {
                    MessageUtils.info(player, MessageUtils.getMessage(player, "menu.biome.success", Pair.of("name", finalName)));
                });
            } else {
                player.kick(Component.text(MessageUtils.getMessage(player, "island.command.noPerm")));
            }
            player.closeInventory();
        });
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main")).build());
        this.onCloseAsync(() -> new MainMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .onCloseSync(null);
    }
}
