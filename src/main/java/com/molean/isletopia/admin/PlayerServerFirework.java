package com.molean.isletopia.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.task.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashSet;
import java.util.Random;

@CommandAlias("firework")
@Singleton
public class PlayerServerFirework extends BaseCommand {
    private final HashSet<String> fireworkMap = new HashSet<>();
    private final Random random = new Random();


    @Interval(20)
    public void firework() {
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
    }


    @Default
    public void onDefault(String target) {
        if (target.equalsIgnoreCase("all")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                fireworkMap.add(onlinePlayer.getName());
            }
        } else if (target.equalsIgnoreCase("stop")) {
            fireworkMap.clear();
        } else {
            if (fireworkMap.contains(target)) {
                fireworkMap.remove(target);
            } else {
                fireworkMap.add(target);
            }
        }
    }

    @Default
    public void onDefault(Player player) {
        if (fireworkMap.contains(player.getName())) {
            fireworkMap.remove(player.getName());
        } else {
            fireworkMap.add(player.getName());
        }
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
