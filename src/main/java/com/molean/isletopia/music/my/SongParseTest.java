package com.molean.isletopia.music.my;

import com.molean.isletopia.utils.InputStreamUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SongParseTest {

    public static List<List<Note>> parse(InputStream inputStream) {
        List<List<Note>> music = new ArrayList<>();
        try (final DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            short length = InputStreamUtil.readShort(dataInputStream);
            byte version = dataInputStream.readByte();
            byte vanillaInstrumentCount = dataInputStream.readByte();

            short songLength = InputStreamUtil.readShort(dataInputStream);
            short layerCount = InputStreamUtil.readShort(dataInputStream);
            String songName = InputStreamUtil.readString(dataInputStream);
            String songAuthor = InputStreamUtil.readString(dataInputStream);
            String songOriginalAuthor = InputStreamUtil.readString(dataInputStream);
            String songDescription = InputStreamUtil.readString(dataInputStream);
            short songTempo = InputStreamUtil.readShort(dataInputStream);
            byte autoSaving = dataInputStream.readByte();
            byte autoSavingDuration = dataInputStream.readByte();
            byte timeSignature = dataInputStream.readByte();
            int minutesSpent = InputStreamUtil.readInt(dataInputStream);
            int leftClicks = InputStreamUtil.readInt(dataInputStream);
            int rightClicks = InputStreamUtil.readInt(dataInputStream);
            int noteBlocksAdded = InputStreamUtil.readInt(dataInputStream);
            int noteBlocksRemoved = InputStreamUtil.readInt(dataInputStream);
            String fileName = InputStreamUtil.readString(dataInputStream);
            byte loop = dataInputStream.readByte();
            byte maxLoopCount = dataInputStream.readByte();
            short loopStartTick = dataInputStream.readShort();
            short nextTick = InputStreamUtil.readShort(dataInputStream);
            while (nextTick != 0) {
                ArrayList<Note> notes = new ArrayList<>();
                short nextLayer = InputStreamUtil.readShort(dataInputStream);
                while (nextLayer != 0) {
                    byte noteBlockInstrument = dataInputStream.readByte();
                    byte noteBlockKey = dataInputStream.readByte();
                    byte noteBlockVelocity = dataInputStream.readByte();
                    byte noteBlockPanning = dataInputStream.readByte();
                    byte[] bff = new byte[2];
                    bff[1] = dataInputStream.readByte();
                    bff[0] = dataInputStream.readByte();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bff);
                    DataInputStream temp = new DataInputStream(byteArrayInputStream);
                    short noteBlockPitch = temp.readShort();
                    notes.add(new Note(noteBlockInstrument, noteBlockKey, noteBlockPitch));

                    nextLayer = InputStreamUtil.readShort(dataInputStream);
                }
                for (int i = 0; i < nextTick - 1; i++) {
                    music.add(null);
                }
                music.add(notes);

                nextTick = InputStreamUtil.readShort(dataInputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return music;
    }

}
