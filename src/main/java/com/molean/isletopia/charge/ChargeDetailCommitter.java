package com.molean.isletopia.charge;

import com.molean.isletopia.shared.database.ChargeDao;
import com.molean.isletopia.shared.model.ChargeDetail;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.task.Tasks;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChargeDetailCommitter {

    private static final Map<IslandId, ChargeDetail> playerChargeDetailMap = new HashMap<>();

    public void commit() {
        //每分钟提交一次岛屿消费数据
        playerChargeDetailMap.remove(null);
        new HashMap<>(playerChargeDetailMap).forEach((s, playerChargeDetail) -> {
            playerChargeDetail.setLastCommitTime(LocalDateTime.now());
            try {
                ChargeDao.set(s, playerChargeDetail);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public ChargeDetailCommitter() {
        Tasks.INSTANCE.intervalAsync(60 * 20, this::commit);
        Tasks.INSTANCE.addDisableTask("Last charge bill commit ", this::commit);
    }

    //获取当前周
    public static int getWeek(LocalDateTime localDateTime) {
        return localDateTime.get(WeekFields.ISO.weekOfYear());
    }

    public static @NotNull ChargeDetail get(IslandId islandId) {
        if (!playerChargeDetailMap.containsKey(islandId)) {
            //本地缓存不存在账单, 从数据库读取账单
            ChargeDetail chargeDetail = null;
            try {
                chargeDetail = Objects.requireNonNullElse(ChargeDao.get(islandId), new ChargeDetail());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            playerChargeDetailMap.put(islandId, chargeDetail);
        }
        //从缓存中读取账单
        ChargeDetail chargeDetail = playerChargeDetailMap.get(islandId);
        if (getWeek(chargeDetail.getStartTime()) != getWeek(LocalDateTime.now())) {
            //账单过期, 新建账单
            chargeDetail = new ChargeDetail();
            playerChargeDetailMap.put(islandId, chargeDetail);
        }
        return chargeDetail;
    }
}
