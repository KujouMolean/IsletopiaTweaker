package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class PlayerServerFirework implements CommandExecutor {
    private final Map<Player, Boolean> fireworkMap = new HashMap<>();
    private final Random random = new Random();

    public PlayerServerFirework() {
        Objects.requireNonNull(Bukkit.getPluginCommand("firework")).setExecutor(this);
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            for (Player player : fireworkMap.keySet()) {
                if(fireworkMap.get(player)){
                    if(player.isOnline()){
                        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
                            spawnFirework(player.getLocation().add(0, 2, 0));
                        }, random.nextInt(20));
                    }
                }
            }
        }, 20, 20);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length==1){
            if (args[0].equalsIgnoreCase("all")) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    fireworkMap.put(onlinePlayer, true);
                }
            }else if (args[0].equalsIgnoreCase("stop")) {
                fireworkMap.clear();
            }else {
                Player player = Bukkit.getPlayer(args[0]);
                if(player!=null){
                    Boolean orDefault = fireworkMap.getOrDefault(player, false);
                    fireworkMap.put(player, !orDefault);
                }
            }


        }else{
            Boolean orDefault = fireworkMap.getOrDefault((Player) sender, false);
            fireworkMap.put((Player) sender, !orDefault);
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
