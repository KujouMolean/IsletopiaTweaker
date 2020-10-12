package com.molean.isletopia.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {

    //todo...
    public static String getMessage(String key, Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle("message", locale);
        return messages.getString(key);
    }
}
