package com.molean.isletopia.statistics.individual;

import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;

public class ResourceConsumeStatistics {
    private final List<ResourceConsume> resourceConsumes = new LinkedList<>();
    private int lastSecond = 0;

    private ResourceConsume getCurrent() {
        int currentTick = Bukkit.getCurrentTick();
        resourceConsumes.removeIf(aResourceConsume -> aResourceConsume.getTime() < currentTick / 20 - 60);
        ResourceConsume resourceConsume;
        if (lastSecond != currentTick / 20 || resourceConsumes.isEmpty()) {
            resourceConsume = new ResourceConsume(currentTick / 20);
            resourceConsumes.add(resourceConsume);
        } else {
            resourceConsume = resourceConsumes.get(resourceConsumes.size() - 1);
        }
        lastSecond = currentTick / 20;
        return resourceConsume;
    }

    public void addPowerUsage(String reason, Long amount) {
        getCurrent().addPowerUsage(reason, amount);
    }

    public void addWaterUsage(String reason, Long amount) {
        getCurrent().addWaterUsage(reason, amount);
    }

    public void addAirUsage(String reason, Long amount) {
        getCurrent().addAirUsage(reason, amount);
    }

    public ResourceConsume getResourceConsume() {
        resourceConsumes.removeIf(resourceConsume -> resourceConsume.getTime() < Bukkit.getCurrentTick() / 20 - 60);
        ResourceConsume result = new ResourceConsume(0);

        double multi = 60.0 / (resourceConsumes.size() == 0 ? 1 : resourceConsumes.size());
        for (ResourceConsume resourceConsume : resourceConsumes) {
            for (String s : resourceConsume.getPowerUsageMap().keySet()) {
                result.addPowerUsage(s, (long) (resourceConsume.getPowerUsageMap().get(s)*multi));
            }
            for (String s : resourceConsume.getAirUsageMap().keySet()) {
                result.addAirUsage(s, (long) (resourceConsume.getAirUsageMap().get(s)*multi));
            }
            for (String s : resourceConsume.getWaterUsageMap().keySet()) {
                result.addWaterUsage(s, (long) (resourceConsume.getWaterUsageMap().get(s) * multi));
            }
        }
        return result;
    }

}
