package com.molean.isletopia.charge;

import com.google.gson.Gson;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
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
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            //每分钟提交一次岛屿消费数据
            playerChargeDetailMap.remove(null);
            playerChargeDetailMap.forEach((s, playerChargeDetail) -> {
                playerChargeDetail.setLastCommitTime(System.currentTimeMillis());
                String playerChargeDetailString = gson.toJson(playerChargeDetail);
                UniversalParameter.setParameter(s, "PlayerChargeDetail", playerChargeDetailString);
            });
        }, 0, 60 * 20L);

        IsletopiaTweakers.addDisableTask("Stop commit player charge", bukkitTask::cancel);
    }

    //获取当前周
    public static int getWeek(Long time) {
        Timestamp timestamp = new Timestamp(time);
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.get(WeekFields.ISO.weekOfYear());
    }

    //从数据库获取用户当前账单
    private static PlayerChargeDetail getPlayerChargeDetailFromDB(String player) {
        String playerChargeDetailString = UniversalParameter.getParameter(player, "PlayerChargeDetail");
        if (playerChargeDetailString == null || playerChargeDetailString.isEmpty()) {
            //数据库不存在账单, 新建
            PlayerChargeDetail playerChargeDetail = new PlayerChargeDetail();
            playerChargeDetail.setStartTime(System.currentTimeMillis());
            playerChargeDetailString = gson.toJson(playerChargeDetail);
            //保存到数据库
            UniversalParameter.setParameter(player, "PlayerChargeDetail", playerChargeDetailString);
        }
        //从数据库读取账单
        PlayerChargeDetail playerChargeDetail = gson.fromJson(playerChargeDetailString, PlayerChargeDetail.class);
        playerChargeDetailMap.put(player, playerChargeDetail);
        return playerChargeDetail;
    }


    public static @NotNull PlayerChargeDetail get(String player) {
        if (!playerChargeDetailMap.containsKey(player)) {
            //本地缓存不存在账单, 从数据库读取账单
            PlayerChargeDetail playerChargeDetail = getPlayerChargeDetailFromDB(player);
            playerChargeDetailMap.put(player, playerChargeDetail);
        }
        //从缓存中读取账单
        PlayerChargeDetail playerChargeDetail = playerChargeDetailMap.get(player);
        if (getWeek(playerChargeDetail.getStartTime()) != getWeek(System.currentTimeMillis())) {
            //账单过期, 新建账单
            playerChargeDetail = new PlayerChargeDetail();
            playerChargeDetail.setStartTime(System.currentTimeMillis());
            playerChargeDetailMap.put(player, playerChargeDetail);
        }
        return playerChargeDetail;
    }
}
