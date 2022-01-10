package com.molean.isletopia.player;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.database.ParameterDao;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.task.Tasks;
import org.apache.commons.lang3.ThreadUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public enum PlayerPropertyManager implements Listener {
    INSTANCE;

    private final Map<UUID, Map<String, String>> propertiesMap = new HashMap<>();

    PlayerPropertyManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (UUID uuid : new ArrayList<>(propertiesMap.keySet())) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) {
                    propertiesMap.remove(uuid);
                }
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                update(onlinePlayer);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, 20, 20);
    }

    private void update(Player player) {
        Map<String, String> properties = ParameterDao.properties(player.getUniqueId());
        propertiesMap.put(player.getUniqueId(), properties);

    }

    public void setPropertyAsync(Player player, String key, String value) {
        Tasks.INSTANCE.async(() -> {
            UniversalParameter.setParameter(player.getUniqueId(), key, value);
        });
    }

    public String getProperty(Player player, String key) {
        if (!propertiesMap.containsKey(player.getUniqueId())) {
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
