package com.molean.isletopia.menu.recipe;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class CraftRecipeMenu extends ChestMenu {
    private final LocalRecipe localRecipe;
    private BukkitTask bukkitTask;

    public CraftRecipeMenu(Player player, LocalRecipe localRecipe) {
        super(player, 4, Component.text("扩展合成表"));
        this.localRecipe = localRecipe;
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f返回");
        for (int i = 27; i < 36; i++) {
            item(i, father.build(), () -> {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    new RecipeListMenu(player).open();
                });
            });
        }
    }

    @Override
    public void beforeOpen() {
        super.beforeOpen();
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), new Runnable() {
            private int cnt = 0;
            @Override
            public void run() {
                item(10, localRecipe.types.get(cnt % localRecipe.types.size()));
                item(3, localRecipe.sources.get(cnt % localRecipe.sources.size())[0]);
                item(4, localRecipe.sources.get(cnt % localRecipe.sources.size())[1]);
                item(5, localRecipe.sources.get(cnt % localRecipe.sources.size())[2]);
                item(12, localRecipe.sources.get(cnt % localRecipe.sources.size())[3]);
                item(13, localRecipe.sources.get(cnt % localRecipe.sources.size())[4]);
                item(14, localRecipe.sources.get(cnt % localRecipe.sources.size())[5]);
                item(21, localRecipe.sources.get(cnt % localRecipe.sources.size())[6]);
                item(22, localRecipe.sources.get(cnt % localRecipe.sources.size())[7]);
                item(23, localRecipe.sources.get(cnt % localRecipe.sources.size())[8]);
                item(16, localRecipe.results.get(cnt % localRecipe.results.size()));
                cnt++;
            }
        }, 0, 20);
    }

    @Override
    public void afterClose() {
        super.afterClose();
        bukkitTask.cancel();
    }
}
