package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.menu.recipe.CraftRecipeMenu;
import com.molean.isletopia.menu.recipe.LocalRecipe;
import com.molean.isletopia.menu.recipe.RecipeListMenu;
import com.molean.isletopia.other.NoticeDialog;
import com.molean.isletopia.shared.utils.RedisUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuCommand implements CommandExecutor, TabCompleter {
    public MenuCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("menu")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("menu")).setTabCompleter(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                if (RedisUtils.getCommand().exists("2021.12.20:" + player.getName()) == 0) {
                    new NoticeDialog("""
                            §d§o天时人事日相催,
                            冬至阳生春又来。
                            §1§l幻想乡~Gensokyo社团新活动： ★冬至雕塑活动★§r
                            ♪活动时间：
                            即日起至2022年1月31日
                            ♪活动内容：
                            修建一个符合活动要求的雕塑或像素画，详细活动要求见群文件→进行中的活动→（进行中）冬至雕塑活动
                            §c§n§l!!!!!请务必仔细阅读要求，避免因作品不符合要求而无法获得奖励!!!!!
                            """,
                            """
                                    ♪活动奖励
                                    参与奖：
                                    §5§o§l潜影盒*1 §r
                                    基础奖：
                                    §b§o§l信标*1§r
                                    §9§o纪念品:随机口味的[汤圆]*1 §r
                                    优胜奖：
                                    §b§o§l信标/海洋之心/收纳袋*1§9§o纪念品:随机口味的[汤圆]*1§6§o纪念品:指定口味的[汤圆]*1
                                    §7§m（据说汤圆一共有⑨款~）
                                      """).accept(player1 -> {
                        RedisUtils.getCommand().set("2021.12.20:" + player.getName(), "true");
                        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerMenu(player1).open());
                    }).open(player);
                } else {
                    new PlayerMenu(player).open();
                }

            });


        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("recipe")) {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    new RecipeListMenu(player).open();
                });
            }
            if (args[0].equalsIgnoreCase("close")) {
                player.closeInventory();
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("recipe")) {
                for (LocalRecipe localRecipe : LocalRecipe.localRecipeList) {
                    for (ItemStack result : localRecipe.results) {
                        if (result.getType().name().equalsIgnoreCase(args[1])) {
                            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                                new CraftRecipeMenu(player, localRecipe).open();
                            });
                        }
                    }

                }

            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
