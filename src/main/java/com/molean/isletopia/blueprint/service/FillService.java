package com.molean.isletopia.blueprint.service;

import com.molean.isletopia.blueprint.obj.MaterialContainerImpl;
import org.bukkit.inventory.ItemStack;

public class FillService {
    public enum Result {
        SUCCESS, NOT_NEED
    }

    public static Result fill(MaterialContainerImpl bluePrintInstance, ItemStack itemStack) {

        return Result.SUCCESS;
    }

    public static Result fillByShulkerBox(MaterialContainerImpl bluePrintInstance, ItemStack itemStack) {
        return Result.SUCCESS;
    }

}
