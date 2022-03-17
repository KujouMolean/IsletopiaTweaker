package com.molean.isletopia.charge;

import com.molean.isletopia.shared.model.ChargeDetail;

public class ChargeDetailUtils {
    public static final long DISPENSER_TIMES = 2000;
    public static final long PISTON_TIMES = 200;
    public static final long REDSTONE_TIMES = 2000;
    public static final long HOPPER_TIMES = 4000;
    public static final long TNT_TIMES = 50;
    public static final long FURNACE_TIMES = 2000;
    public static final long VEHICLE_TIMES = 2000;
    public static final long WATER_TIMES = 200;//modified
    public static final long FOOD_TIMES = 60;

    public static final long POWER_INITIAL = 100000;
    public static final long POWER_PER_BUY = 10000;
    public static final long POWER_PER_PRODUCE = 25;
    public static final long POWER_PER_ONLINE = 100;

    public static final long FOOD_INITIAL = 100000;
    public static final long FOOD_PER_BUY = 10000;
    public static final long FOOD_PER_PRODUCE = 25;
    public static final long FOOD_PER_ONLINE = 100;

    //获取用户总用电量
    public static long getTotalPowerUsage(ChargeDetail chargeDetail) {
        long powerUsage = 0;
        powerUsage += chargeDetail.getDispenser() / DISPENSER_TIMES;
        powerUsage += chargeDetail.getPiston() / PISTON_TIMES;
        powerUsage += chargeDetail.getRedstone() / REDSTONE_TIMES;
        powerUsage += chargeDetail.getHopper() / HOPPER_TIMES;
        powerUsage += chargeDetail.getTnt() / TNT_TIMES;
        powerUsage += chargeDetail.getFurnace() / FURNACE_TIMES;
        powerUsage += chargeDetail.getVehicle() / VEHICLE_TIMES;
        powerUsage += chargeDetail.getWater() / WATER_TIMES;
        return powerUsage;
    }

    //获取用户发射器用电量
    public static long getDispenserPowerUsage(ChargeDetail chargeDetail) {
        return chargeDetail.getDispenser() / DISPENSER_TIMES;
    }

    //获取用户活塞用电量
    public static long getPistonPowerUsage(ChargeDetail chargeDetail) {
        return chargeDetail.getPiston() / PISTON_TIMES;
    }

    //获取用户红石用电量
    public static long getRedstonePowerUsage(ChargeDetail chargeDetail) {
        return chargeDetail.getRedstone() / REDSTONE_TIMES;
    }

    //获取用户漏斗用电量
    public static long getHopperPowerUsage(ChargeDetail chargeDetail) {
        return chargeDetail.getHopper() / HOPPER_TIMES;
    }

    //获取用户Tnt用电量
    public static long getTntPowerUsage(ChargeDetail chargeDetail) {
        return chargeDetail.getTnt() / TNT_TIMES;

    }

    //获取用户熔炉用电量
    public static long getFurnacePowerUsage(ChargeDetail chargeDetail) {
        return chargeDetail.getFurnace() / FURNACE_TIMES;
    }

    //获取用户矿车用电量
    public static long getVehiclePowerUsage(ChargeDetail chargeDetail) {
        return chargeDetail.getVehicle() / VEHICLE_TIMES;
    }

    //获取用户用水量
    public static long getTotalWaterUsage(ChargeDetail chargeDetail) {
        return chargeDetail.getWater() / WATER_TIMES;
    }

    //获取用户用粮草量
    public static long getTotalFoodUsage(ChargeDetail chargeDetail) {
        return chargeDetail.getFood() / FOOD_TIMES;
    }


    //获取用户总电量
    public static long getTotalPower(ChargeDetail chargeDetail) {
        long powerTotal = 0;
        powerTotal += chargeDetail.getPowerChargeTimes() * POWER_PER_BUY;
        powerTotal += chargeDetail.getPowerProduceTimes() * POWER_PER_PRODUCE;
        powerTotal += chargeDetail.getOnlineMinutes() * POWER_PER_ONLINE;
        powerTotal += POWER_INITIAL;
        return powerTotal;
    }

    public static long getTotalFood(ChargeDetail chargeDetail) {
        long foodTotal = 0;
        foodTotal += chargeDetail.getFoodChargeTimes() * FOOD_PER_BUY;
        foodTotal += chargeDetail.getFoodProduceTimes() * FOOD_PER_PRODUCE;
        foodTotal += chargeDetail.getOnlineMinutes() * FOOD_PER_ONLINE;
        foodTotal += FOOD_INITIAL;
        return foodTotal;
    }

    public static long getLeftPower(ChargeDetail chargeDetail) {
        return ChargeDetailUtils.getTotalPower(chargeDetail) - ChargeDetailUtils.getTotalPowerUsage(chargeDetail);
    }

    public static long getLeftFood(ChargeDetail chargeDetail) {
        return ChargeDetailUtils.getTotalFood(chargeDetail) - ChargeDetailUtils.getTotalFoodUsage(chargeDetail);
    }

    public static long getPowerCost(ChargeDetail chargeDetail) {
        long powerCost = 0;
        long leftPower = getLeftPower(chargeDetail);
        if (leftPower < 0) {
            long t = (long) Math.ceil(-leftPower / (double) ChargeDetailUtils.POWER_PER_BUY);
            for (int i = chargeDetail.getPowerChargeTimes() + 1; i <= t; i++) {
                powerCost += i;
            }
        }
        return powerCost;
    }

    public static long getFoodCost(ChargeDetail chargeDetail) {
        long foodCost = 0;
        long leftFood = getLeftFood(chargeDetail);
        if (leftFood < 0) {
            long t = (long) Math.ceil(-leftFood / (double) ChargeDetailUtils.FOOD_PER_BUY);
            for (int i = chargeDetail.getPowerChargeTimes() + 1; i <= t; i++) {
                foodCost += i;
            }
        }
        return foodCost;
    }

}
