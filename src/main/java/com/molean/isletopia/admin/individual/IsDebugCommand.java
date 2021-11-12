package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.modifier.individual.PlayerHeadDrop;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ScoreboardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class IsDebugCommand implements CommandExecutor, Listener {

    public IsDebugCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("isdebug")).setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void spawn(Block block, boolean solid, int size, ItemStack skull) {
        Location loc = block.getLocation();
        if (solid) {
            loc.getBlock().setType(Material.BARRIER);
        }
        this.spawnBlock(loc, size,skull);
    }

    private void spawnBlock(Location loc, int size,ItemStack skull) {
        double multiplier;
        double yMultiplier;
        double addX;
        double addY;
        double addZ;

        if (size == 2) {// fullBlock <-- Psst, try this with md_5's skin, it looks like a pufferfish :P
            double add = 0.2;
            multiplier = .25 + add;

            yMultiplier = multiplier;

            addX = -0.078125 * 2;
            addY = -0.875;
            addZ = -0.078125 * 2;
        } else if (size == 1) {// bigBlock
            multiplier = .5935;
            yMultiplier = .5935;

            addX = -0.813 / 2;
            addY = -1.03;
            addZ = -0.813 / 2;
        } else {// smallBlock
            multiplier = .415;
            yMultiplier = .415;

            addX = -0.12;
            addY = -0.15;
            addZ = -0.12;
        }

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    ArmorStand stand = loc.getWorld().spawn(loc.clone().add(0, -1, 0).add(multiplier * (x + 1) + addX, yMultiplier * (y + 1) + addY, multiplier * (z + 1) + addZ), ArmorStand.class);
                    stand.setCustomNameVisible(false);
                    stand.setGravity(false);
                    stand.setVisible(false);
                    stand.setSmall(size == 0);
                    stand.getEquipment().setHelmet(skull);
                }
            }
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Block block = player.getLocation().getBlock();
        spawn(block, true, 0, HeadUtils.getSkullFromValue("猪", PlayerHeadDrop.drops.get(EntityType.PIG)));
        return true;
    }


}
