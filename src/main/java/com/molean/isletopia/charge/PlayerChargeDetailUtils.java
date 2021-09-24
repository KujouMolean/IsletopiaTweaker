package com.molean.isletopia.charge;

public class PlayerChargeDetailUtils {
    public static final long DISPENSER_TIMES = 2000;
    public static final long PISTON_TIMES = 200;
    public static final long REDSTONE_TIMES = 2000;
    public static final long HOPPER_TIMES = 4000;
    public static final long TNT_TIMES = 50;
    public static final long FURNACE_TIMES = 2000;
    public static final long VEHICLE_TIMES = 2000;
    public static final long WATER_TIMES = 100;

    public static final long POWER_INITIAL = 1000;
    public static final long POWER_PER_BUY = 10000;
    public static final long POWER_PER_PRODUCE = 10;
    public static final long POWER_PER_ONLINE = 50;

    public static final long WATER_INITIAL = 1000;
    public static final long WATER_PER_BUY = 10000;
    public static final long WATER_PER_PRODUCE = 10;
    public static final long WATER_PER_ONLINE = 50;


    //获取用户总用电量
    public static long getTotalPowerUsage(PlayerChargeDetail playerChargeDetail) {
        long powerUsage = 0;
        powerUsage += playerChargeDetail.getDispenser() / DISPENSER_TIMES;
        powerUsage += playerChargeDetail.getPiston() / PISTON_TIMES;
        powerUsage += playerChargeDetail.getRedstone() / REDSTONE_TIMES;
        powerUsage += playerChargeDetail.getHopper() / HOPPER_TIMES;
        powerUsage += playerChargeDetail.getTnt() / TNT_TIMES;
        powerUsage += playerChargeDetail.getFurnace() / FURNACE_TIMES;
        powerUsage += playerChargeDetail.getVehicle() / VEHICLE_TIMES;
        return powerUsage;
    }

    //获取用户发射器用电量
    public static long getDispenserPowerUsage(PlayerChargeDetail playerChargeDetail) {
        return playerChargeDetail.getDispenser() / DISPENSER_TIMES;
    }

    //获取用户活塞用电量
    public static long getPistonPowerUsage(PlayerChargeDetail playerChargeDetail) {
        return playerChargeDetail.getPiston() / PISTON_TIMES;
    }

    //获取用户红石用电量
    public static long getRedstonePowerUsage(PlayerChargeDetail playerChargeDetail) {
        return playerChargeDetail.getRedstone() / REDSTONE_TIMES;
    }

    //获取用户漏斗用电量
    public static long getHopperPowerUsage(PlayerChargeDetail playerChargeDetail) {
        return playerChargeDetail.getHopper() / HOPPER_TIMES;
    }

    //获取用户Tnt用电量
    public static long getTntPowerUsage(PlayerChargeDetail playerChargeDetail) {
        return playerChargeDetail.getTnt() / TNT_TIMES;

    }

    //获取用户熔炉用电量
    public static long getFurnacePowerUsage(PlayerChargeDetail playerChargeDetail) {
        return playerChargeDetail.getFurnace() / FURNACE_TIMES;
    }

    //获取用户矿车用电量
    public static long getVehiclePowerUsage(PlayerChargeDetail playerChargeDetail) {
        return playerChargeDetail.getVehicle() / VEHICLE_TIMES;
    }

    //获取用户用水量
    public static long getTotalWaterUsage(PlayerChargeDetail playerChargeDetail) {
        return playerChargeDetail.getWater() / WATER_TIMES;
    }

    //获取用户总电量
    public static long getTotalPower(PlayerChargeDetail playerChargeDetail) {
        long powerTotal = 0;
        powerTotal += playerChargeDetail.getPowerChargeTimes() * POWER_PER_BUY;
        powerTotal += playerChargeDetail.getPowerProduceTimes() * POWER_PER_PRODUCE;
        powerTotal += playerChargeDetail.getOnlineMinutes() * POWER_PER_ONLINE;
        powerTotal += POWER_INITIAL;
        return powerTotal;
    }

    public static long getTotalWater(PlayerChargeDetail playerChargeDetail) {
        long waterTotal = 0;
        waterTotal += playerChargeDetail.getWaterChargeTimes() * WATER_PER_BUY;
        waterTotal += playerChargeDetail.getWaterProduceTimes() * WATER_PER_PRODUCE;
        waterTotal += playerChargeDetail.getOnlineMinutes() * WATER_PER_ONLINE;
        waterTotal += WATER_INITIAL;
        return waterTotal;
    }

    public static long getLeftPower(PlayerChargeDetail playerChargeDetail) {
        return PlayerChargeDetailUtils.getTotalPower(playerChargeDetail) - PlayerChargeDetailUtils.getTotalPowerUsage(playerChargeDetail);
    }

    public static long getLeftWater(PlayerChargeDetail playerChargeDetail) {
        return PlayerChargeDetailUtils.getTotalWater(playerChargeDetail) - PlayerChargeDetailUtils.getTotalWaterUsage(playerChargeDetail);
    }


    public static long getPowerCost(PlayerChargeDetail playerChargeDetail) {
        long powerCost = 0;
        long leftPower = getLeftPower(playerChargeDetail);
        if (leftPower < 0) {
            long t = (long) Math.ceil(-leftPower / (double) PlayerChargeDetailUtils.POWER_PER_BUY);
            for (int i = playerChargeDetail.getPowerChargeTimes() + 1; i <= t; i++) {
                powerCost += i;
            }
        }
        return powerCost;
    }

    public static long getWaterCost(PlayerChargeDetail playerChargeDetail) {
        long waterCost = 0;
        long leftWater = getLeftWater(playerChargeDetail);
        if (leftWater < 0) {
            long t = (long) Math.ceil(-leftWater / (double) PlayerChargeDetailUtils.POWER_PER_BUY);
            for (int i = playerChargeDetail.getWaterChargeTimes()+1; i <= t; i++) {
                waterCost += i;
            }
        }
        return waterCost;
    }
}
