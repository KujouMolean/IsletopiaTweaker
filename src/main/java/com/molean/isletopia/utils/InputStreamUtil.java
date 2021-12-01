package com.molean.isletopia.utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class InputStreamUtil {

    private InputStreamUtil() {

    }

    public static short readShort(final DataInputStream dataInputStream) throws IOException {
        final int byte1 = dataInputStream.readUnsignedByte();
        final int byte2 = dataInputStream.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
    }

    public static int readInt(final DataInputStream dataInputStream) throws IOException {
        final int byte1 = dataInputStream.readUnsignedByte();
        final int byte2 = dataInputStream.readUnsignedByte();
        final int byte3 = dataInputStream.readUnsignedByte();
        final int byte4 = dataInputStream.readUnsignedByte();
        return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
    }

    public static String readString(final DataInputStream dataInputStream) throws IOException {
        final byte[] bytes = new byte[readInt(dataInputStream)];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = dataInputStream.readByte();
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
