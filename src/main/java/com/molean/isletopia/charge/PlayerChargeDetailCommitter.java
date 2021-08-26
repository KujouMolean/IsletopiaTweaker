package com.molean.isletopia.charge;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlayerChargeDetailCommitter {

    private static final Map<String, PlayerChargeDetail> playerChargeDetailMap = new HashMap<>();
    private static final Gson gson = new Gson();

    public PlayerChargeDetailCommitter() {
        //每分钟执行一次
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            //每分钟提交一次岛屿消费数据

            playerChargeDetailMap.remove(null);

            playerChargeDetailMap.forEach((s, playerChargeDetail) -> {
                playerChargeDetail.setLastCommitTime(System.currentTimeMillis());
                String playerChargeDetailString = gson.toJson(playerChargeDetail);
                UniversalParameter.setParameter(s, "PlayerChargeDetail", playerChargeDetailString);
            });




        }, 0, 60 * 20L);
    }

    public static int getWeek(Long time) {
        Timestamp timestamp = new Timestamp(time);
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.get(WeekFields.ISO.weekOfYear());
    }

    public static PlayerChargeDetail getPlayerChargeDetailFromDB(String player) {
        String playerChargeDetailString = UniversalParameter.getParameter(player, "PlayerChargeDetail");
        if (playerChargeDetailString == null || playerChargeDetailString.isEmpty()) {
            PlayerChargeDetail playerChargeDetail = new PlayerChargeDetail();
            playerChargeDetail.setStartTime(System.currentTimeMillis());
            playerChargeDetailString = gson.toJson(playerChargeDetail);
            UniversalParameter.setParameter(player, "PlayerChargeDetail", playerChargeDetailString);
        }
        PlayerChargeDetail playerChargeDetail = gson.fromJson(playerChargeDetailString, PlayerChargeDetail.class);
        playerChargeDetailMap.put(player, playerChargeDetail);
        return playerChargeDetail;
    }

    public static @Nullable PlayerChargeDetail getLastWeekPlayerChargeDetail(String player) {
        get(player);

        String lastWeekPlayerChargeDetail = UniversalParameter.getParameter(player, "LastWeekPlayerChargeDetail");
        if (lastWeekPlayerChargeDetail == null || lastWeekPlayerChargeDetail.isEmpty()) {
            return null;
        } else {
            PlayerChargeDetail playerChargeDetail = gson.fromJson(lastWeekPlayerChargeDetail, PlayerChargeDetail.class);
            if (getWeek(playerChargeDetail.getStartTime()) != getWeek(System.currentTimeMillis()) - 1) {
                UniversalParameter.unsetParameter(player, "LastWeekPlayerChargeDetail");
                return null;
            } else if (PlayerChargeDetailUtils.getWaterCost(playerChargeDetail) == 0 &&
                    PlayerChargeDetailUtils.getPowerCost(playerChargeDetail) == 0) {
                UniversalParameter.unsetParameter(player, "LastWeekPlayerChargeDetail");
                return null;
            } else {
                return playerChargeDetail;
            }
        }
    }

    public static void updateLastWeekPlayerChargeDetail(String player, PlayerChargeDetail playerChargeDetail) {
        UniversalParameter.setParameter(player, "LastWeekPlayerChargeDetail", gson.toJson(playerChargeDetail));
        getLastWeekPlayerChargeDetail(player);
    }


    public static @NotNull PlayerChargeDetail get(String player) {
        if (!playerChargeDetailMap.containsKey(player)) {
            PlayerChargeDetail playerChargeDetail = getPlayerChargeDetailFromDB(player);
            playerChargeDetailMap.put(player, playerChargeDetail);
        }

        PlayerChargeDetail playerChargeDetail = playerChargeDetailMap.get(player);
        if (getWeek(playerChargeDetail.getStartTime()) != getWeek(System.currentTimeMillis())) {
            if (getWeek(playerChargeDetail.getStartTime()) == getWeek(System.currentTimeMillis()) - 1) {
                if (PlayerChargeDetailUtils.getPowerCost(playerChargeDetail) > 0 ||
                        PlayerChargeDetailUtils.getWaterCost(playerChargeDetail) > 0) {

                    UniversalParameter.setParameter(player, "LastWeekPlayerChargeDetail", gson.toJson(playerChargeDetail));
                }
            }
            playerChargeDetail = new PlayerChargeDetail();
            playerChargeDetail.setStartTime(System.currentTimeMillis());
            playerChargeDetailMap.put(player, playerChargeDetail);
        }
        return playerChargeDetail;
    }
}
