package com.molean.isletopia.parameter;

import com.molean.isletopia.database.PDBUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UniversalParameter {
    public static String getParameter(String player, String key) {
        return PDBUtils.get(player, key);
    }

    public static List<String> getParameterAsList(String player, String key) {
        ArrayList<String> list = new ArrayList<>();
        String parameter = getParameter(player, key);
        if (parameter != null && !parameter.trim().equals("")) {
            list.addAll(Arrays.asList(parameter.split(",")));
        }
        return list;
    }

    public static void setParameter(String player, String key, String value) {
        PDBUtils.set(player, key, value);
    }

    public static void addParameter(String player, String key, String value) {
        String before = PDBUtils.get(player, key);
        if (before == null || before.trim().equals("")) {
            PDBUtils.set(player, key, value);
        } else {
            List<String> strings = Arrays.asList(before.split(","));
            List<String> newStrings = new ArrayList<>(strings);
            newStrings.add(value);
            String join = String.join(",", newStrings);
            PDBUtils.set(player, key, join);
        }
    }

    public static void removeParameter(String player, String key, String value) {
        String before = PDBUtils.get(player, key);
        if (before == null || before.trim().equals("")) {
            PDBUtils.set(player, key, value);
        } else {
            List<String> strings = Arrays.asList(before.split(","));
            List<String> newStrings = new ArrayList<>(strings);
            newStrings.remove(value);
            if (newStrings.size() == 0) {
                PDBUtils.set(player, key, null);
            } else {
                String join = String.join(",", newStrings);
                PDBUtils.set(player, key, join);
            }

        }
    }

    public static void unsetParameter(String player, String key) {
        PDBUtils.set(player, key, null);
    }

}
