package com.molean.isletopia.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;

public class LangUtils {
    private static JsonObject jsonObject = null;

    static {
        JsonParser jsonParser = new JsonParser();
        InputStream inputStream = LangUtils.class.getClassLoader().getResourceAsStream("zh_cn.json");
        if (inputStream != null) {
            JsonElement parse = jsonParser.parse(new InputStreamReader(inputStream));
            jsonObject = parse.getAsJsonObject();
        }
    }

    public static String get(String key) {
        String str = null;
        try {
            JsonElement jsonElement = jsonObject.get(key);
            str = jsonElement.getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            str = key;
        }
        return str;
    }
}
