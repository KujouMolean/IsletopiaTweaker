package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.task.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class NoteUtils {
    private static final List<Float> pitches = new ArrayList<>();
    private static final List<Sound> sounds = new ArrayList<>();

    static {
        for (int i = 0; i <= 24; i++) {
            pitches.add((float) Math.pow(2, (i - 12.0) / 12.0));
        }

        sounds.add(Sound.BLOCK_NOTE_BLOCK_BASS);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_SNARE);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_HAT);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_BASEDRUM);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_BELL);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_FLUTE);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_CHIME);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_GUITAR);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_COW_BELL);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_BIT);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_BANJO);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_PLING);
        sounds.add(Sound.BLOCK_NOTE_BLOCK_HARP);
    }

    public static void test(Location location, Sound sound, float pitch) {
        location.getWorld().playSound(location, sound, 1f, pitch);
    }


    public static float getPitchFromLevel(int level) {
        return pitches.get(level);
    }

    public static int getSoundLevel(Sound sound) {
        for (int i = 0; i < sounds.size(); i++) {
            if (sounds.get(i).equals(sound)) {
                return i;
            }
        }
        return -1;
    }

    public static Sound getSound(int level) {
        if (level < 0) {

            return null;
        }
        return sounds.get(level%sounds.size());
    }


    public static void playMulti(Player player, int interval, List<String> multi) {
        for (String s : multi) {
            playSingle(player, interval, s);
        }

    }

    public static void playSingle(Player player, int interval, String str) {

        String s = str.toLowerCase(Locale.ROOT);
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (!Character.isLetterOrDigit(c) && c != '#') {
                MessageUtils.fail(player, str + " 是无效输入");
                return;
            }
        }

        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), new Consumer<>() {
            int i = 0;

            @Override
            public void accept(BukkitTask task) {
                if (i + 1 >= chars.length) {
                    task.cancel();
                    return;
                }
                if (!player.isOnline()) {
                    task.cancel();
                    return;
                }
                if (chars[i] == '#' || chars[i + 1] == '#') {
                    i++;
                    return;
                }

                int soundIndex;
                if (Character.isDigit(chars[i])) {
                    soundIndex = Integer.parseInt(chars[i] + "");
                } else {
                    soundIndex = 10 + chars[i] - 'a';
                }
                Sound sound = sounds.get(soundIndex % sounds.size());
                int pitchIndex;
                if (Character.isDigit(chars[i + 1])) {
                    pitchIndex = Integer.parseInt(chars[i + 1] + "");
                } else {
                    pitchIndex = 10 + chars[i + 1] - 'a';
                }
                double pitch = pitches.get(pitchIndex % pitches.size());

                Tasks.INSTANCE.sync( () -> {
                    test(player.getLocation(), sound, (float) pitch);
                });
                i += 2;
            }
        }, interval, interval);

    }
}
