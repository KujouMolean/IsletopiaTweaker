package com.molean.isletopia.player;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerPropertyLoadCompleteEvent;
import com.molean.isletopia.shared.database.PlayerParameterDao;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum PlayerPropertyManager implements Listener {
    INSTANCE;

    private final Map<UUID, Map<String, String>> propertiesMap = new ConcurrentHashMap<>();

    PlayerPropertyManager() {
        PluginUtils.registerEvents(this);
        Tasks.INSTANCE.async(() -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                update(onlinePlayer);
            }
        });
    }

    public boolean isLoad(UUID uuid) {
        return propertiesMap.containsKey(uuid);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Tasks.INSTANCE.async(() -> {
            update(event.getPlayer());
            if (!event.getPlayer().isOnline()) {
                return;
            }
            PlayerPropertyLoadCompleteEvent playerPropertyLoadCompleteEvent = new PlayerPropertyLoadCompleteEvent(event.getPlayer());
            PluginUtils.callEvent(playerPropertyLoadCompleteEvent);
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        propertiesMap.remove(event.getPlayer().getUniqueId());
    }

    private void update(Player player) {
        Map<String, String> properties = PlayerParameterDao.properties(player.getUniqueId());
        propertiesMap.put(player.getUniqueId(), properties);
    }

    public void setPropertyAsync(Player player, String key, String value) {
        setPropertyAsync(player, key, value, null);

    }

    public void setPropertyAsync(Player player, String key, String value,Runnable asyncRunnable) {
        Tasks.INSTANCE.async(() -> {
            UniversalParameter.setParameter(player.getUniqueId(), key, value);
            update(player);
            if (asyncRunnable != null) {
                asyncRunnable.run();
            }
        });
    }

    public String getProperty(Player player, String key) {
        if (!propertiesMap.containsKey(player.getUniqueId())) {
            IsletopiaTweakers.getPlugin().getLogger().warning("load properties " + player.getName() + "-" + key + " in main thread!");
            update(player);
        }
        return propertiesMap.get(player.getUniqueId()).get(key);
    }


    public boolean getPropertyAsBoolean(Player player, String key) {
        return "true".equalsIgnoreCase(getProperty(player, key));
    }

    @Nullable
    public Integer getPropertyAsInteger(Player player, String key) {
        String property = getProperty(player, key);
        if (property == null) {
            return null;
        }
        try {
            return Integer.parseInt(property);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public int getPropertyAsInt(Player player, String key) {
        Integer propertyAsInteger = getPropertyAsInteger(player, key);
        return Objects.requireNonNullElse(propertyAsInteger, 0);
    }

    @Nullable
    public LocalDateTime getPropertyAsLocalDateTime(Player player, String key) {
        String property = getProperty(player, key);
        if (property == null) {
            return null;
        }
        return LocalDateTime.parse(property, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void setDateTimeAsync(Player player, String key, LocalDateTime localDateTime) {
        String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        setPropertyAsync(player, key, format);
    }


}
