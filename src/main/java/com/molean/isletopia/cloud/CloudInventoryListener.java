package com.molean.isletopia.cloud;

import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.database.CloudInventoryDao;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.BukkitPlayerUtils;
import com.molean.isletopia.utils.MaterialListUtil;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Singleton
public class CloudInventoryListener implements Listener {
    private final PlayerPropertyManager playerPropertyManager;
    private final PlayerManager playerManager;

    public CloudInventoryListener(PlayerManager playerManager, PlayerPropertyManager playerPropertyManager) {

        this.playerPropertyManager = playerPropertyManager;
        this.playerManager = playerManager;

    }

    @Interval(value = 20, async = true)
    public void autoGetTask() {
        for (Player onlinePlayer : playerManager.getLoggedPlayers()) {
            autoGet(onlinePlayer);
        }
    }

    @Interval(value = 20, async = true)
    public void autoPutTask() {
        for (Player onlinePlayer : playerManager.getLoggedPlayers()) {
            autoPut(onlinePlayer);
        }
    }



    private final static Set<UUID> UUIDS = new CopyOnWriteArraySet<>();

    public  void autoGet(Player player) {
        if (!playerPropertyManager.isLoad(player.getUniqueId())) {
            return;
        }
        if (UUIDS.contains(player.getUniqueId())) {
            return;
        }
        UUIDS.add(player.getUniqueId());
        List<String> autoGet = playerPropertyManager.getPropertyAsStringList(player, "AutoGet");
        List<Material> materials = MaterialListUtil.toMaterialList(autoGet);
        if (materials.size() == 0) {
            UUIDS.remove(player.getUniqueId());
            return;
        }

        new SyncThenAsyncTask<>(() -> {
            PlayerInventory inventory = player.getInventory();
            Map<Material, Integer> map = new HashMap<>();
            Map<Material, ItemStack> rawCache = new HashMap<>();
            for (Material material : materials) {
                rawCache.put(material, new ItemStack(material));
            }
            ArrayList<Integer> slots = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                slots.add(i);
            }
            slots.add(40);
            for (Integer slot : slots) {
                ItemStack item = inventory.getItem(slot);
                if (item == null) {
                    continue;
                }
                if (materials.contains(item.getType()) && item.isSimilar(rawCache.get(item.getType()))) {

                    int amount = item.getAmount();
                    int maxStackSize = item.getType().getMaxStackSize();
                    if (amount < maxStackSize) {
                        Integer orDefault = map.getOrDefault(item.getType(), 0);
                        map.put(item.getType(), orDefault + (maxStackSize - amount));
                    }
                }
            }
            return map;
        }, (map) -> {
            Map<Material, Integer> result = new HashMap<>();
            map.forEach((material, amount) -> {
                if (CloudInventoryDao.consume(player.getUniqueId(), material.name(), amount)) {
                    MessageUtils.action(player, "已为你补充%dx%s".formatted(amount, LangUtils.get(player.locale(), material.translationKey())));
                    result.put(material, amount);
                } else {
                    playerPropertyManager.removeStringListPropertyEntryAsync(player, "AutoGet", material.name());
                    MessageUtils.success(player, "%s补充失败，已关闭自动补充".formatted(LangUtils.get(player.locale(), material.translationKey())));
                }
            });
            Tasks.INSTANCE.sync(() -> {
                result.forEach((material, integer) -> {
                    BukkitPlayerUtils.giveItem(player, material, integer);
                });
                UUIDS.remove(player.getUniqueId());
            });
        }).run();



    }

    public  void autoPut(Player player) {
        if (!playerPropertyManager.isLoad(player.getUniqueId())) {
            return;
        }
        if (UUIDS.contains(player.getUniqueId())) {
            return;
        }
        UUIDS.add(player.getUniqueId());
        List<String> autoPut = playerPropertyManager.getPropertyAsStringList(player, "AutoPut");
        List<Material> materials = MaterialListUtil.toMaterialList(autoPut);
        if (materials.size() == 0) {
            UUIDS.remove(player.getUniqueId());
            return;
        }
        new SyncThenAsyncTask<>(() -> {
            PlayerInventory inventory = player.getInventory();
            Map<Material, Integer> map = new HashMap<>();
            Map<Material, ItemStack> rawCache = new HashMap<>();
            for (Material material : materials) {
                rawCache.put(material, new ItemStack(material));
            }

            for (int i = 9; i < 36; i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null) {
                    continue;
                }
                if (materials.contains(item.getType())) {
                    if (item.isSimilar(rawCache.get(item.getType()))) {
                        Integer origin = map.getOrDefault(item.getType(), 0);
                        map.put(item.getType(), origin + item.getAmount());
                        item.setAmount(0);
                        inventory.setItem(i, null);
                    }
                }
            }
            return map;
        }, (map) -> {
            map.forEach((material, amount) -> {
                if (CloudInventoryDao.produce(player.getUniqueId(), material.name(), amount)) {
                    MessageUtils.action(player, "已提交%dx%s至云仓".formatted(amount, LangUtils.get(player.locale(), material.translationKey())));
                } else {
                    MessageUtils.warn(player, "Unexpected error, contact server admin!");
                }
            });
            UUIDS.remove(player.getUniqueId());
        }).run();


    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        Tasks.INSTANCE.async(() -> {
            autoPut((Player) event.getWhoClicked());
            autoGet((Player) event.getWhoClicked());
        });

    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Tasks.INSTANCE.async(() -> {
            autoGet(event.getPlayer());
        });

    }

    @EventHandler
    public void on(PlayerAttemptPickupItemEvent event) {
        Tasks.INSTANCE.async(() -> {
            autoGet(event.getPlayer());
            autoPut(event.getPlayer());
        });
    }


}
