package com.molean.isletopia.statistics.individual;

import java.util.HashMap;
import java.util.Map;

public class ResourceConsume {
    private final Map<String, Long> powerUsageMap = new HashMap<>();
    private final Map<String, Long> waterUsageMap = new HashMap<>();
    private final Map<String, Long> airUsageMap = new HashMap<>();
    private final int time;

    public ResourceConsume(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void addPowerUsage(String reason, Long amount) {
        Long power = powerUsageMap.getOrDefault(reason, 0L);
        powerUsageMap.put(reason, power + amount);
    }
    public void addAirUsage(String reason, Long amount) {
        Long power = airUsageMap.getOrDefault(reason, 0L);
        airUsageMap.put(reason, power + amount);
    }
    public void addWaterUsage(String reason, Long amount) {
        Long power = waterUsageMap.getOrDefault(reason, 0L);
        waterUsageMap.put(reason, power + amount);
    }

    public Map<String, Long> getPowerUsageMap() {
        return powerUsageMap;
    }

    public Map<String, Long> getWaterUsageMap() {
        return waterUsageMap;
    }

    public Map<String, Long> getAirUsageMap() {
        return airUsageMap;
    }
}
