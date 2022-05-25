package com.molean.isletopia.cloud;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.distribute.DataLoadTask;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.database.CloudInventoryDao;
import com.molean.isletopia.shared.model.CloudInventorySlot;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MaterialListUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

@Singleton
public class CloudInventoryService {
    private final Map<UUID, Set<Material>> map = new HashMap<>();

    private final PlayerManager playerManager;
    private PlayerPropertyManager playerPropertyManager;

    public CloudInventoryService(PlayerManager playerManager, PlayerPropertyManager playerPropertyManager) {
        this.playerManager = playerManager;
        DataLoadTask<Set<Material>> cloudInventoryLoadTask = new DataLoadTask<>("CloudInventory");
        cloudInventoryLoadTask.setAsyncLoad((player, listConsumer) -> {
            try {
                listConsumer.accept(query(player.getUniqueId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        cloudInventoryLoadTask.setSyncRestore((player, materials, consumer) -> {
            map.put(player.getUniqueId(), materials);
            consumer.accept(player);
        });
        playerManager.registerDataLoading(cloudInventoryLoadTask);
    }

    private Set<Material> query(UUID uuid) throws SQLException {
        List<CloudInventorySlot> inventorySlotsSnapshot = CloudInventoryDao.getInventorySlotsSnapshot(uuid);
        Set<Material> materials = new HashSet<>();
        for (CloudInventorySlot cloudInventorySlot : inventorySlotsSnapshot) {
            materials.add(Material.valueOf(cloudInventorySlot.getMaterial()));
        }
        return materials;
    }


    public void createSlot(Player player, Material material, Consumer<@Nullable Exception> consumer) {
        playerManager.validate(player);
        Tasks.INSTANCE.async(() -> {
            try {
                CloudInventoryDao.create(player.getUniqueId(), material.name());
                consumer.accept(null);
                map.get(player.getUniqueId()).add(material);

            } catch (Exception e) {
                consumer.accept(e);
            }
        });
    }


    public Set<Material> list(Player player) {
        playerManager.validate(player);
        return map.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    public void containsSlot(Player player, Material material, Consumer<@Nullable Boolean> consumer) {
        playerManager.validate(player);
        Tasks.INSTANCE.async(() -> {
            try {
                consumer.accept(CloudInventoryDao.containsSlot(player.getUniqueId(), material.name()));
            } catch (Exception e) {
                consumer.accept(null);
            }
        });
    }

    public void removeSlot(Player player, Material material, Consumer<@Nullable Exception> consumer) {
        playerManager.validate(player);
        Tasks.INSTANCE.async(() -> {
            try {
                CloudInventoryDao.delete(player.getUniqueId(), material.name());
                consumer.accept(null);
                map.get(player.getUniqueId()).remove(material);
            } catch (Exception e) {
                consumer.accept(e);
            }
        });
    }

    public void produce(Player player, Material material, int amount, Consumer<@NotNull Boolean> consumer) {
        playerManager.validate(player);
        Tasks.INSTANCE.async(() -> {
            try {
                CloudInventoryDao.produce(player.getUniqueId(), material.name(), amount);
                consumer.accept(true);
            } catch (Exception e) {
                consumer.accept(false);
            }
        });
    }

    public void consume(Player player, Material material, int amount, Consumer<@NotNull Boolean> consumer) {
        playerManager.validate(player);
        Tasks.INSTANCE.async(() -> {
            try {
                CloudInventoryDao.produce(player.getUniqueId(), material.name(), amount);
                consumer.accept(true);
            } catch (Exception e) {
                consumer.accept(false);
            }
        });
    }

    public List<Material> getEnabledAutoGetMaterialList(Player player) {
        List<String> autoGet = playerPropertyManager.getPropertyAsStringList(player, "AutoGet");
        return MaterialListUtil.toMaterialList(autoGet);
    }

    public List<Material> getEnabledAutoPutMaterialList(Player player) {
        List<String> autoGet = playerPropertyManager.getPropertyAsStringList(player, "AutoPut");
        return MaterialListUtil.toMaterialList(autoGet);
    }

}
