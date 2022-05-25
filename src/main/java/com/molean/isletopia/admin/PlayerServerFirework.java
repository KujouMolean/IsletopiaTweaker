package com.molean.isletopia.admin.individual;

import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.task.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

@BukkitCommand("firework")
public class PlayerServerFirework implements CommandExecutor {
    private final HashSet<String> fireworkMap = new HashSet<>();
    private final Random random = new Random();

    public PlayerServerFirework() {
        Objects.requireNonNull(Bukkit.getPluginCommand("firework")).setExecutor(this);
        Tasks.INSTANCE.interval(20, () -> {
            fireworkMap.removeIf(s -> Bukkit.getOnlinePlayers().stream()
                    .noneMatch(player -> player.getName().equalsIgnoreCase(s)));

            for (String player : fireworkMap) {
                Tasks.INSTANCE.timeout(random.nextInt(20), () -> {
                    Player bukkitPlayer = Bukkit.getPlayerExact(player);
                    if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                        spawnFirework(bukkitPlayer.getLocation().add(0, 2, 0));
                    }
                });
            }
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.isOp()) {
            if (args[0].equalsIgnoreCase("all")) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    fireworkMap.add(onlinePlayer.getName());
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                fireworkMap.clear();
            } else {
                if (fireworkMap.contains(args[0])) {
                    fireworkMap.remove(args[0]);
                } else {
                    fireworkMap.add(args[0]);
                }
            }
        } else {
            if (fireworkMap.contains(sender.getName())) {
                fireworkMap.remove(sender.getName());
            } else {
                fireworkMap.add(sender.getName());
            }
        }

        return true;
    }

    private void spawnFirework(Location location) {
        Firework entity = (Firework) location.getWorld().spawnEntity(location.clone().add(0, 2, 0), EntityType.FIREWORK);
        FireworkMeta itemMeta = entity.getFireworkMeta();
        Random random = new Random();
        itemMeta.setPower(random.nextInt(3) + 1);
        for (int i = 0; i < random.nextInt(10) + 1; i++) {
            FireworkEffect build = FireworkEffect.builder()
                    .flicker(false)
                    .trail(false)
                    .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
                    .withColor(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                    .withFade(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                    .build();

            itemMeta.addEffect(build);
        }
        entity.detonate();
        entity.setBounce(true);
        entity.setFireworkMeta(itemMeta);
    }
}
