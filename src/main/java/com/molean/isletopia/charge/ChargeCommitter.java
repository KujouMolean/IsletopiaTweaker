package com.molean.isletopia.charge;

import com.molean.isletopia.shared.annotations.DisableTask;
import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.database.ChargeDao;
import com.molean.isletopia.shared.model.ChargeDetail;
import com.molean.isletopia.shared.model.IslandId;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Singleton
public class ChargeDetailCommitter {

    private final Map<IslandId, ChargeDetail> playerChargeDetailMap = new HashMap<>();
    private final Set<IslandId> tobeQuery = new CopyOnWriteArraySet<>();

    private ChargeDetail tempChargeDetail = new ChargeDetail();


    @Interval(value = 20 * 60, async = true)
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

    @Interval(50)
    public void refreshTemp() {
        tempChargeDetail = new ChargeDetail();
    }

    @DisableTask
    public void disable() {
        this.commit();

    }

    //获取当前周
    public int getWeek(LocalDateTime localDateTime) {
        return localDateTime.get(WeekFields.ISO.weekOfYear());
    }

    @Interval(value = 50, async = true)
    private void query() {
        for (IslandId islandId : tobeQuery) {
            ChargeDetail chargeDetail = null;
            try {
                chargeDetail = Objects.requireNonNullElse(ChargeDao.get(islandId), new ChargeDetail());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            playerChargeDetailMap.put(islandId, chargeDetail);
        }
    }

    public @NotNull ChargeDetail get(IslandId islandId) {
        if (!playerChargeDetailMap.containsKey(islandId)) {
            //本地缓存不存在账单, 请求从数据库读取账单
            tobeQuery.add(islandId);
            return tempChargeDetail;
        }
        ChargeDetail chargeDetail = playerChargeDetailMap.get(islandId);
        if (getWeek(chargeDetail.getStartTime()) != getWeek(LocalDateTime.now())) {
            chargeDetail = new ChargeDetail();
            playerChargeDetailMap.put(islandId, chargeDetail);
        }
        return chargeDetail;
    }
}
