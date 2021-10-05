package com.molean.isletopia.tutor.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class IronTutor implements Listener {

    private static final Set<Player> PLAYERS = new HashSet<>();
    private static final Map<Player, BossBar> BARS = new HashMap<>();

    public IronTutor() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        IsletopiaTweakers.addDisableTask("Remove all iron tutor bars", () -> {
            BARS.forEach((player, bossBar) -> {
                bossBar.removeAll();
            });
        });
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onJoin(onlinePlayer);
        }
    }

    public static void onJoin(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String tutorStatus = UniversalParameter.getParameter(player.getName(), "TutorStatus");
            if (Objects.equals(tutorStatus, "Iron") && player.isOnline()) {
                BossBar bossBar = Bukkit.createBossBar("新手引导: 合成一块铁锭.", BarColor.GREEN, BarStyle.SEGMENTED_20);
                bossBar.addPlayer(player);
                PLAYERS.add(player);
                BARS.put(player, bossBar);
            }
        });
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        onJoin(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        onQuit(event.getPlayer());
    }

    public static void onQuit(Player player) {
        PLAYERS.remove(player);
        BossBar bossBar = BARS.get(player);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(CraftItemEvent event) {
        HumanEntity whoClicked = event.getWhoClicked();
        if (!(whoClicked instanceof Player player)) {
            return;
        }
        if (!PLAYERS.contains(player)) {
            return;
        }
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof ShapedRecipe shapedRecipe)) {
            return;

        }
        boolean hasDye = false;
        for (ItemStack value : shapedRecipe.getIngredientMap().values()) {
            if (value.getType().name().contains("DYE")) {
                hasDye = true;
            }
        }
        if (!hasDye) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            MessageUtils.info(player, "接下来是更加困难的挑战，请拯救一只村民。");
            MessageUtils.info(player, "用女巫的虚弱药水和金苹果将僵尸村民转化为村民为你所用。");
            PLAYERS.remove(player);
            BARS.get(player).removeAll();
            UniversalParameter.setParameter(player.getName(), "TutorStatus", "Villager");
            VillagerTutor.onJoin(player);
        });



    }

}
