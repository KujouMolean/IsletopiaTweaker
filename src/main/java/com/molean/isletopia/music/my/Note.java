package com.molean.isletopia.music.my;

import com.molean.isletopia.utils.NoteUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

public class Note {
    private final Sound instrument;
    private final int pitchLevel;

    public Note(byte instrument, byte key, short pitch) {
        switch (instrument) {
            case 1 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_BASS;
            case 2 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_BASEDRUM;
            case 3 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_SNARE;
            case 4 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_HAT;
            case 5 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_GUITAR;
            case 6 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_FLUTE;
            case 7 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_BELL;
            case 8 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_CHIME;
            case 9 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_XYLOPHONE;
            case 10 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE;
            case 11 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_COW_BELL;
            case 12 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO;
            case 13 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_BIT;
            case 14 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_BANJO;
            case 15 -> this.instrument = Sound.BLOCK_NOTE_BLOCK_PLING;
            default -> this.instrument = Sound.BLOCK_NOTE_BLOCK_HARP;
        }
        this.pitchLevel = getRealLevel(key, pitch);
    }

    public Note(Sound instrument, int pitchLevel) {
        this.instrument = instrument;
        this.pitchLevel = pitchLevel;
    }

    private int getRealLevel(byte key, short pitch) {
        pitch += (key - 33) * 100;
        if (pitch < 0) pitch = 0;
        if (pitch > 2400) pitch = 2400;
        return pitch / 100;
    }

    public Sound getInstrument() {
        return instrument;
    }

    public float getPitchLevel() {
        return pitchLevel;
    }

    public void play(Location location) {
        location.getWorld().playSound(location, instrument, 1.0F, NoteUtils.getPitchFromLevel(pitchLevel));
    }

    public @Nullable String encode() {
        int soundLevel = NoteUtils.getSoundLevel(instrument);
        char c1,c2;
        if (soundLevel < 0 || pitchLevel < 0 || soundLevel >= 36 || pitchLevel >= 36) {
            return null;
        }
        if (soundLevel < 10) {
            c1 = (char) ('0' + soundLevel);
        } else {
            c1 = (char) ('a' + (soundLevel - 10));
        }

        if (pitchLevel < 10) {
            c2 = (char) ('0' + pitchLevel);
        } else {
            c2 = (char) ('a' + (pitchLevel - 10));
        }
        return c1 + "" + c2;
    }

    public static Note decode(String str) {
        return decode(str.charAt(0), str.charAt(1));
    }

    public static Note decode(char c1, char c2) {
        int soundIndex;
        if (Character.isDigit(c1)) {
            soundIndex = Integer.parseInt(c1 + "");
        } else {
            soundIndex = 10 +c1- 'a';
        }
        Sound sound = NoteUtils.getSound(soundIndex);
        int pitchIndex;
        if (Character.isDigit(c2)) {
            pitchIndex = Integer.parseInt(c2 + "");
        } else {
            pitchIndex = 10 + c2 - 'a';
        }
        return new Note(sound, pitchIndex);
    }

}
