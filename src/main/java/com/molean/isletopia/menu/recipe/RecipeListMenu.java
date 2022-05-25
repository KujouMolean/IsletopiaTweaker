package com.molean.isletopia.menu.recipe;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class RecipeListMenu extends ListMenu<LocalRecipe> {
    private BukkitTask bukkitTask;
    private int cnt = 0;


    private final List<LocalRecipe> localRecipeList = new ArrayList<>(LocalRecipe.localRecipeList);

    private static ItemStack updateLang(Player player, ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }
        TextComponent component = (TextComponent) itemMeta.displayName();
        if (component != null) {
            String content = component.content();
            itemMeta.displayName(Component.text(MessageUtils.getMessage(player, content)));
        }
        List<Component> lores = itemMeta.lore();
        if (lores != null) {
            for (int i = 0; i < lores.size(); i++) {
                String content = ((TextComponent) lores.get(i)).content();
                lores.set(i, Component.text(content));
            }
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public RecipeListMenu(SidebarManager sidebarManager, PlayerPropertyManager playerPropertyManager, ChargeCommitter chargeCommitter, Player player) {

        super(player, Component.text(MessageUtils.getMessage(player, "menu.recipe.list.title")));
        this.convertFunction(localRecipe -> {
            List<ItemStack> icons = localRecipe.icons;
            return icons.get(cnt % icons.size());
        });

        for (LocalRecipe localRecipe : localRecipeList) {
            for (int i = 0; i < localRecipe.icons.size(); i++) {
                localRecipe.icons.set(i, updateLang(player, localRecipe.icons.get(i)));
            }
            for (int i = 0; i < localRecipe.results.size(); i++) {
                localRecipe.results.set(i, updateLang(player, localRecipe.results.get(i)));
            }
            for (int i = 0; i < localRecipe.sources.size(); i++) {
                for (int j = 0; j < localRecipe.sources.get(i).length; j++) {
                    localRecipe.sources.get(i)[j] = updateLang(player, localRecipe.sources.get(i)[j]);
                }
            }
            for (int i = 0; i < localRecipe.types.size(); i++) {
                localRecipe.types.set(i, updateLang(player, localRecipe.types.get(i)));

            }
        }

        this.components(this.localRecipeList);
        this.onClickAsync(localRecipe -> new CraftRecipeMenu(playerPropertyManager, sidebarManager, chargeCommitter, player, localRecipe).open());
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main")).build());
        this.onCloseAsync(() -> new MainMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .onCloseSync(null);
    }

    @Override
    public void beforeOpen() {
        super.beforeOpen();
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            this.components(this.localRecipeList);
            cnt++;
        }, 0, 20);
    }

    @Override
    public void afterClose() {
        super.afterClose();
        bukkitTask.cancel();
    }
}
