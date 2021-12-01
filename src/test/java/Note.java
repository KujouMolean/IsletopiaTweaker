import org.bukkit.Sound;

public class Note {
    private Sound instrument;
    private float realPitch;

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
        this.realPitch = getRealPitch(key, pitch);
    }

    public Note(Sound instrument, float realPitch) {
        this.instrument = instrument;
        this.realPitch = realPitch;
    }

    private float getRealPitch(byte key, short pitch) {
        pitch += (key - 33) * 100;
        if (pitch < 0) pitch = 0;
        if (pitch > 2400) pitch = 2400;
        return (float) Math.pow(2, (pitch - 1200d) / 1200d);
    }

    public Sound getInstrument() {
        return instrument;
    }

    public void setInstrument(Sound instrument) {
        this.instrument = instrument;
    }

    public float getRealPitch() {
        return realPitch;
    }

    public void setRealPitch(float realPitch) {
        this.realPitch = realPitch;
    }
}
