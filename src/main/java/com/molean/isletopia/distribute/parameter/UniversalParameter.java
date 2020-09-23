package com.molean.isletopia.distribute.parameter;

import com.molean.isletopia.database.ParameterDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UniversalParameter {
    public static String getParameter(String player, String key) {
        return ParameterDao.get(player, key);
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
        ParameterDao.set(player, key, value);
    }

    public static void addParameter(String player, String key, String value) {
        String before = ParameterDao.get(player, key);
        if (before == null || before.trim().equals("")) {
            ParameterDao.set(player, key, value);
        } else {
            List<String> strings = Arrays.asList(before.split(","));
            List<String> newStrings = new ArrayList<>(strings);
            newStrings.add(value);
            String join = String.join(",", newStrings);
            ParameterDao.set(player, key, join);
        }
    }

    public static void removeParameter(String player, String key, String value) {
        String before = ParameterDao.get(player, key);
        if (before == null || before.trim().equals("")) {
            ParameterDao.set(player, key, value);
        } else {
            List<String> strings = Arrays.asList(before.split(","));
            List<String> newStrings = new ArrayList<>(strings);
            newStrings.remove(value);
            if (newStrings.size() == 0) {
                ParameterDao.set(player, key, null);
            } else {
                String join = String.join(",", newStrings);
                ParameterDao.set(player, key, join);
            }

        }
    }

    public static void unsetParameter(String player, String key) {
        ParameterDao.set(player, key, null);
    }

}
