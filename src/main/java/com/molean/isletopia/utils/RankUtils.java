package com.molean.isletopia.utils;

import com.molean.isletopia.database.VanillaStatisticsDao;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.statistics.individual.vanilla.Stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankUtils {

    public static void collectionRank(){
        List<String> players = VanillaStatisticsDao.players();
        Map<String,Integer> map = new HashMap<>();
        for (String player : players) {
            List<String> collection = UniversalParameter.getParameterAsList(player, "collection");
            for (String s : collection) {
                map.put(s,map.getOrDefault(s,0)+1);
            }
        }
        ArrayList<String> keys = new ArrayList<>(map.keySet());
        keys.sort((o1, o2) -> {
            return map.get(o2) - map.get(o1);
        });

        for (int i = 0; i < 20; i++) {
            String s = keys.get(i);
            System.out.println(s + " " + map.get(s));
        }
    }

    public static List<Pair<String,Integer>> customRanking(String item, int n){
        List<String> players = VanillaStatisticsDao.players();

        Map<String, Stats> map = new HashMap<>();
        for (String player : players) {
            Stats statistics = VanillaStatisticsDao.getStatistics(player);
            map.put(player, statistics);
        }
        ArrayList<String> strings = new ArrayList<>(players);

        Map<String, Integer> topMap = new HashMap<>();
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            Map<String, Integer> stringIntegerMap = map.get(s).getStats().get("minecraft:custom");
            if (stringIntegerMap == null) {
                continue;
            }
            Integer orDefault = stringIntegerMap.getOrDefault("minecraft:"+item, 0);
            topMap.put(s, orDefault);
        }
        ArrayList<String> keys = new ArrayList<>(topMap.keySet());
        keys.sort((o1, o2) -> topMap.get(o2) - topMap.get(o1));
        ArrayList<Pair<String,Integer>> rank = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (i>=keys.size()){
                break;
            }
            String s = keys.get(i);
            rank.add(new Pair<>(s , topMap.get(s)));
        }
        return  rank;
    }

    public static List<Pair<String,Integer>> ranking(String type,String item, int n){
        List<String> players = VanillaStatisticsDao.players();

        Map<String, Stats> playerStatsMap = new HashMap<>();
        for (String player : players) {
            Stats statistics = VanillaStatisticsDao.getStatistics(player);
            playerStatsMap.put(player, statistics);
        }
        ArrayList<String> playerNames = new ArrayList<>(players);

        Map<String, Integer> topMap = new HashMap<>();
        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            Map<String, Integer> stringIntegerMap = playerStatsMap.get(playerName).getStats().get("minecraft:"+type);
            if (stringIntegerMap == null) {
                continue;
            }
            topMap.put(playerName, stringIntegerMap.getOrDefault("minecraft:"+item, 0));
        }
        ArrayList<String> keys = new ArrayList<>(topMap.keySet());
        keys.sort((o1, o2) -> topMap.get(o2) - topMap.get(o1));
        ArrayList<Pair<String,Integer>> rank = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (i>=keys.size()){
                break;
            }
            String s = keys.get(i);
            rank.add(new Pair<>(s , topMap.get(s)));
        }
        return rank;
    }


    public static ArrayList<Pair<String, Integer>> togetherRanking(String item, int n){
        List<String> players = VanillaStatisticsDao.players();

        Map<String, Stats> map = new HashMap<>();
        for (String player : players) {
            Stats statistics = VanillaStatisticsDao.getStatistics(player);
            map.put(player, statistics);
        }
        ArrayList<String> strings = new ArrayList<>(players);

        Map<String, Integer> topMap = new HashMap<>();
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            Map<String, Integer> stringIntegerMap = map.get(s).getStats().get("minecraft:"+item);
            if (stringIntegerMap == null) {
                continue;
            }
            for (String s1 : stringIntegerMap.keySet()) {
                topMap.put(s1, stringIntegerMap.getOrDefault(s1, 0)+topMap.getOrDefault(s1,0));
            }
        }


        ArrayList<String> keys = new ArrayList<>(topMap.keySet());
        keys.sort((o1, o2) -> {
            return topMap.get(o2) - topMap.get(o1);
        });
        ArrayList<Pair<String,Integer>> rank = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (i>=keys.size()){
                break;
            }
            String s = keys.get(i);
            rank.add(new Pair<>(s , topMap.get(s)));
        }
        return rank;
    }

    public static void main(String[] args) {
        ArrayList<Pair<String, Integer>> mined = togetherRanking("mined", 1000);
        int sum = 0;
        for (Pair<String, Integer> stringIntegerPair : mined) {
            sum+=stringIntegerPair.getValue();
        }
        System.out.println(sum);
    }
}