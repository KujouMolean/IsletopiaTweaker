package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandId;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.LangUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProductionBar implements Listener, CommandExecutor {
    private static final Map<IslandId, Map<Material, Deque<Long>>> map = new HashMap<>();
    private static final Map<IslandId, Map<Material, Integer>> maxMap = new HashMap<>();
    private static final Map<IslandId, List<BossBar>> bossBarsPerIsland = new HashMap<>();

    public ProductionBar() {
        Objects.requireNonNull(Bukkit.getPluginCommand("productionbar")).setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            checkPlayerBar(onlinePlayer);
        }
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            HashSet<IslandId> islandIds = new HashSet<>();
            for (Player bar : bars) {
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(bar);
                if (currentIsland != null) {
                    islandIds.add(currentIsland.getIslandId());
                }
            }

            for (IslandId islandId : islandIds) {
                if (bossBarsPerIsland.containsKey(islandId)) {
                    for (BossBar bossBar : bossBarsPerIsland.get(islandId)) {
                        bossBar.removeAll();
                        bossBar.setVisible(false);
                    }
                    bossBarsPerIsland.get(islandId).clear();

                }
                bossBarsPerIsland.put(islandId, new ArrayList<>());
                List<BossBar> bossBars = this.bossBarsPerIsland.get(islandId);

                Map<Material, Integer> materialIntegerMap = productionPerMin(islandId);
                ArrayList<Pair<Material, Integer>> pairs = new ArrayList<>();
                materialIntegerMap.forEach((material, integer) -> {
                    pairs.add(new Pair<>(material, integer));
                });
                pairs.sort(Comparator.comparingInt(Pair::getValue));
                Collections.reverse(pairs);

                for (int i = 0; i < pairs.size() && i < 4; i++) {
                    BossBar bossBar = Bukkit.createBossBar(null, BarColor.BLUE, BarStyle.SOLID);
                    Integer max = maxMap.get(islandId).get(pairs.get(i).getKey());
                    Integer now = pairs.get(i).getValue();
                    double progress = now / (double) max;
                    if (progress < 0) {
                        progress = 0;
                    }
                    if (progress > 1) {
                        progress = 1;
                    }
                    bossBar.setProgress(progress);
                    bossBar.setTitle(LangUtils.get(pairs.get(i).getKey().translationKey()) + "当前产量:" + now + "/min 最大值:" + max + "/min");
                    bossBars.add(bossBar);
                }
            }
            for (IslandId islandId : islandIds) {
                Island island = IslandManager.INSTANCE.getIsland(islandId);
                if (island == null) {
                    continue;
                }
                HashSet<Player> playersInIsland = island.getPlayersInIsland();
                for (Player player : playersInIsland) {
                    if (bars.contains(player)) {
                        for (BossBar bossBar : bossBarsPerIsland.get(islandId)) {
                            bossBar.addPlayer(player);
                        }
                    }
                }
            }
        }, 0, 20);
        IsletopiaTweakers.addDisableTask("Disable production bar update, and remove all bars", () -> {
            bukkitTask.cancel();
            bossBarsPerIsland.forEach((islandId, bossBars) -> {
                for (BossBar bossBar : bossBars) {
                    bossBar.removeAll();
                }
            });
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerIslandChange(PlayerIslandChangeEvent event) {
        Island currentIsland = event.getFrom();
        if (currentIsland == null) {
            return;

        }
        List<BossBar> bossBars = bossBarsPerIsland.get(currentIsland.getIslandId());
        if (bossBars == null) {
            return;
        }
        for (BossBar bossBar : bossBars) {
            bossBar.removePlayer(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(event.getLocation());
        if (currentIsland == null) {
            return;
        }
        IslandId islandId = currentIsland.getIslandId();
        if (!map.containsKey(islandId)) {
            map.put(islandId, new HashMap<>());
        }
        ItemStack itemStack = entity.getItemStack();
        Map<Material, Deque<Long>> currentMap = map.get(islandId);
        if (!currentMap.containsKey(itemStack.getType())) {
            currentMap.put(itemStack.getType(), new LinkedList<>());
        }
        Deque<Long> deque = currentMap.get(itemStack.getType());
        long l = System.currentTimeMillis();
        for (int i = 0; i < itemStack.getAmount(); i++) {
            deque.add(l);
        }
    }

    public static Map<Material, Integer> productionPerMin(IslandId islandId) {
        Map<Material, Deque<Long>> materialDequeMap = map.get(islandId);
        HashMap<Material, Integer> materialIntegerHashMap = new HashMap<>();
        if (materialDequeMap == null) {
            return materialIntegerHashMap;
        }
        long l = System.currentTimeMillis() - 60 * 1000;

        if (!maxMap.containsKey(islandId)) {
            maxMap.put(islandId, new HashMap<>());
        }
        Map<Material, Integer> maxMapPerIsland = maxMap.get(islandId);
        HashSet<Material> tobeRemove = new HashSet<>();
        materialDequeMap.forEach((material, longs) -> {
            while (!longs.isEmpty() && longs.getFirst() < l) {
                longs.removeFirst();
            }
            if (longs.isEmpty()) {
                tobeRemove.add(material);
                return;
            }
            Integer max = maxMapPerIsland.getOrDefault(material, 0);
            maxMapPerIsland.put(material, Math.max(longs.size(), max));
            materialIntegerHashMap.put(material, longs.size());
        });
        for (Material material : tobeRemove) {
            materialDequeMap.remove(material);
        }

        return materialIntegerHashMap;
    }

    private final HashSet<Player> bars = new HashSet<>();

    public void checkPlayerBar(Player player) {

        String hasBar = UniversalParameter.getParameter(player.getName(), "ProductionBar");
        if (hasBar != null && !hasBar.isEmpty()) {
            bars.add(player);
        } else {
            bars.remove(player);
            Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
            if (currentIsland == null) {
                return;

            }
            List<BossBar> bossBars = bossBarsPerIsland.get(currentIsland.getIslandId());
            if (bossBars == null) {
                return;
            }
            for (BossBar bossBar : bossBars) {
                bossBar.removePlayer(player);

            }

        }
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            checkPlayerBar(event.getPlayer());
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        bars.remove(event.getPlayer());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        String waterBar = UniversalParameter.getParameter(player.getName(), "ProductionBar");
        if (waterBar != null && !waterBar.isEmpty()) {
            UniversalParameter.unsetParameter(player.getName(), "ProductionBar");
        } else {
            UniversalParameter.setParameter(player.getName(), "ProductionBar", "true");
        }
        checkPlayerBar(player);


        return true;
    }
}
