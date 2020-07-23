package com.molean.isletopia.prompter.util;

public class StringUtils {
    /**
     * Get the most inner string parts by left "${" and right "}".
     */


    public static String getSurroundedString(String source, String left, String right) {
        int j = source.indexOf(right);
        if (j < 0)
            return null;
        int i = source.substring(0, j).lastIndexOf(left);
        if (i < 0)
            return null;
        return source.substring(i + left.length(), j);
    }
}
