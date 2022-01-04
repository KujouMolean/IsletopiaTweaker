package com.molean.isletopia.menu.settings.biome;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.task.PlotChunkTask;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BiomeMenu extends ListMenu<Biome> {

    private static final Set<IslandId> changingBiome = new HashSet<>();
    public BiomeMenu(Player player) {
        super(player, Component.text("选择想要切换的生物群系:"));
        List<Biome> values = new ArrayList<>(List.of(Biome.values()));
        values.remove(Biome.CUSTOM);
        this.components(values);
        Biome currentBiome = player.getLocation().getBlock().getBiome();
        this.convertFunction(biome -> {
            String id = biome.name();
            String name = "未知(" + id + ")";
            Material icon = Material.PLAYER_HEAD;
            List<String> creatures = new ArrayList<>();
            List<String> environments = new ArrayList<>();
            try {
                LocalBiome localBiome = LocalBiome.valueOf(id.toUpperCase());
                name = localBiome.getName();
                icon = localBiome.getIcon();
                creatures.addAll(localBiome.getCreatures());
                creatures.removeIf(String::isEmpty);
                environments.addAll(localBiome.getEnvironment());
                environments.removeIf(String::isEmpty);
            } catch (IllegalArgumentException ignore) {
            }
            ItemStackSheet itemStackSheet = new ItemStackSheet(icon, "§f" + name);
            if (!creatures.isEmpty()) {
                itemStackSheet.addLore("§f生物: " + String.join(", ", creatures));
            }
            if (!environments.isEmpty()) {
                itemStackSheet.addLore("§f环境: " + String.join(", ", environments));
            }
            if (currentBiome.name().equalsIgnoreCase(id)) {
                itemStackSheet.addItemFlag(ItemFlag.HIDE_ENCHANTS);
                itemStackSheet.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                String display = itemStackSheet.getDisplay();
                itemStackSheet.setDisplay("§f当前所在生物群系: " + display);
            }
            return itemStackSheet.build();
        });
        this.onClickSync(biome -> {
            LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);

            assert currentPlot != null;

            if (changingBiome.contains(currentPlot.getIslandId())) {
                MessageUtils.fail(player, "你的岛屿正在修改生物群系, 请等待修改完成!");
                player.closeInventory();
                return;
            }

            String biomeName = biome.name();
            String name = "未知";
            try {
                name = LocalBiome.valueOf(biomeName.toUpperCase()).getName();
            } catch (IllegalArgumentException ignore) {
            }
            if (player.getUniqueId().equals(currentPlot.getUuid())) {
                MessageUtils.info(player, "尝试修改岛屿生物群系...(需要180秒)");
                String finalName = name;
                changingBiome.add(currentPlot.getIslandId());
                new PlotChunkTask(currentPlot, chunk -> {
                    for (int i = 0; i < 16; i++) {
                        for (int j = -64; j < 320; j++) {
                            for (int k = 0; k < 16; k++) {
                                chunk.getBlock(i, j, k).setBiome(biome);
                            }
                        }
                    }
                }, () -> {
                    MessageUtils.info(player, "成功修改生物群系为:" + finalName + ".");
                    changingBiome.remove(currentPlot.getIslandId());
                }, 180 * 20).run();
            } else {
                player.kick(Component.text("错误, 非岛主操作岛屿成员."));
            }
            player.closeInventory();
        });
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回主菜单").build());
        this.onCloseAsync(() -> new PlayerMenu(player).open())
                .onCloseSync(() -> {});
    }
}
