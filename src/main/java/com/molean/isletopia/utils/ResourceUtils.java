package com.molean.isletopia.utils;

import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {
    public static String getResourceAsString(String path) {
        InputStream resourceAsStream = ResourceUtils.class.getClassLoader().getResourceAsStream(path);
        String s = null;
        try {
            assert resourceAsStream != null;
            s = new String(resourceAsStream.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
