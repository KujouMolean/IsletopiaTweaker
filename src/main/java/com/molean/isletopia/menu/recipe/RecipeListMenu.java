package com.molean.isletopia.menu.recipe;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class RecipeListMenu extends ListMenu<LocalRecipe> {
    private BukkitTask bukkitTask;
    private int cnt = 0;

    public RecipeListMenu(Player player) {
        super(player, Component.text("扩展合成表"));
        this.convertFunction(localRecipe -> {
            List<ItemStack> icons = localRecipe.icons;
            return icons.get(cnt % icons.size());
        });
        this.components(LocalRecipe.localRecipeList);
        this.onClickAsync(localRecipe -> new CraftRecipeMenu(player, localRecipe).open());
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回主菜单").build());
        this.onCloseAsync(() -> new PlayerMenu(player).open()).onCloseSync(() -> {
        });
    }

    @Override
    public void beforeOpen() {
        super.beforeOpen();
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            this.components(LocalRecipe.localRecipeList);
            cnt++;
        }, 0, 20);
    }

    @Override
    public void afterClose() {
        super.afterClose();
        bukkitTask.cancel();
    }
}
