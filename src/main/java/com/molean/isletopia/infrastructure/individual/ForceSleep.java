package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.other.ConfirmDialog;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ForceSleep implements Listener, CommandExecutor, TabCompleter {
    private static final Map<UUID, Long> map = new HashMap<>();


    public ForceSleep() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("goodnight")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("goodnight")).setExecutor(this);
    }

    @EventHandler
    public void on(PlayerDataSyncCompleteEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String sleepDate = UniversalParameter.getParameter(event.getPlayer().getUniqueId(), "WakeUpTime");
            if (sleepDate == null) {
                return;
            }
            LocalDateTime parse = LocalDateTime.parse(sleepDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));

            if (parse.isBefore(LocalDateTime.now())) {
                UniversalParameter.unsetParameter(event.getPlayer().getUniqueId(), "WakeUpTime");
            } else {
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    event.getPlayer().kick(Component.text("#你其实在睡觉，现在是在梦里。"));
                });
            }

        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        map.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (!(entity instanceof Cat cat)) {
            return;
        }
        if (!EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }
        if (!cat.isTamed()) {
            return;
        }
        if (LocalDateTime.now().getHour() < 22) {
            return;
        }
        AnimalTamer owner = cat.getOwner();
        if (owner == null) {
            return;
        }
        UUID ownerUUID = owner.getUniqueId();
        if (cat.hasMetadata("GreetingTime")) {
            List<MetadataValue> greetingTime = cat.getMetadata("GreetingTime");
            if (greetingTime.size() != 0) {
                MetadataValue metadataValue = greetingTime.get(0);
                long l = metadataValue.asLong();
                if (System.currentTimeMillis() - l < 60 * 60 * 1000) {
                    MessageUtils.notify(player, "这只猫猫好像刚刚见过。");
                    return;
                }
            }
        }

        if (!ownerUUID.equals(player.getUniqueId())) {
            return;
        }

        MessageUtils.notify(player, "你和猫猫说了晚安，赶快去睡觉吧。");
        map.put(player.getUniqueId(), System.currentTimeMillis());
        cat.removeMetadata("GreetingTime", IsletopiaTweakers.getPlugin());
        cat.setMetadata("GreetingTime", new FixedMetadataValue(IsletopiaTweakers.getPlugin(), System.currentTimeMillis()));
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (LocalDateTime.now().getHour() < 7) {
            MessageUtils.fail(player, "都熬这么晚了，直接通宵吧。");
            return true;
        }
        if (LocalDateTime.now().getHour() < 22) {
            MessageUtils.fail(player, "现在太早了，不能睡觉。");
            return true;
        }
        long l = System.currentTimeMillis();
        if (l - map.getOrDefault(player.getUniqueId(), 0L) > 10 * 60 * 1000) {
            MessageUtils.fail(player, "睡觉前要和猫猫说晚安。");
            return true;
        }

        new ConfirmDialog("""
                睡觉后可获得1个幻翼膜，请确保背包有一个空位。
                确认睡觉后，你将在明早7点前无法进入服务器。
                是否接受？
                """).accept(player1 -> {
            player1.getInventory().addItem(new ItemStack(Material.PHANTOM_MEMBRANE));
            LocalDateTime localDateTime = LocalDate.now().plusDays(1).atTime(7, 0);
            String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
            UniversalParameter.setParameter(player1.getUniqueId(), "WakeUpTime", format);
            player1.kick(Component.text("#做个好梦~"));
            CommonResponseObject commonResponseObject = new CommonResponseObject();
            commonResponseObject.setMessage("这是来自 " + player.getName() + " 的晚安!");
            ServerMessageUtils.sendMessage("waterfall", "CommonResponse", commonResponseObject);

        }).open(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return List.of();
    }
}
