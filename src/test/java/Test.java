import com.molean.isletopia.database.VanillaStatisticsDao;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.statistics.vanilla.Stats;
import com.molean.isletopia.statistics.vanilla.VanillaStatistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {


    public static List<Pair<String, Integer>> topPlayers(String cat, String item, int n) {
        List<Pair<String, Integer>> list = new ArrayList<>();
        Map<String, Stats> allPlayerStats = VanillaStatisticsDao.getAllPlayerStats();
        assert allPlayerStats != null;
        ArrayList<String> rank = new ArrayList<>(allPlayerStats.keySet());
        rank.sort((o1, o2) -> {
            Integer integer1 = allPlayerStats.get(o1).getStats().get(cat).get(item);
            if (integer1 == null) {
                integer1 = 0;
            }
            Integer integer2 = allPlayerStats.get(o2).getStats().get(cat).get(item);
            if (integer2 == null) {
                integer2 = 0;
            }
            return integer2 - integer1;
        });
        for (int i = 0; i < n && i < rank.size(); i++) {
            list.add(new Pair<>(rank.get(i), allPlayerStats.get(rank.get(i)).getStats().get(cat).get(item)));
        }
        return list;
    }

    public static List<Pair<String, Integer>> topItem(String cat, int n) {
        List<Pair<String, Integer>> list = new ArrayList<>();
        Map<String, Stats> allPlayerStats = VanillaStatisticsDao.getAllPlayerStats();
        Map<String, Integer> data = new HashMap<>();
        assert allPlayerStats != null;
        for (Stats value : allPlayerStats.values()) {
            data.putAll(value.getStats().get(cat));
        }

        ArrayList<String> rank = new ArrayList<>(data.keySet());
        rank.sort((o1, o2) -> {
            Integer integer1 = data.get(o1);
            if (integer1 == null) {
                integer1 = 0;
            }
            Integer integer2 = data.get(o2);
            if (integer2 == null) {
                integer2 = 0;
            }
            return integer2 - integer1;
        });


        for (int i = 0; i < n && i < rank.size(); i++) {
            list.add(new Pair<>(rank.get(i), data.get(rank.get(i))));
        }
        return list;
    }

    public static long count(String cat, String item) {
        long cnt = 0;
        Map<String, Stats> allPlayerStats = VanillaStatisticsDao.getAllPlayerStats();
        assert allPlayerStats != null;
        for (Stats value : allPlayerStats.values()) {
            cnt += value.getStats().get(cat).get(item);
        }
        return cnt;
    }

    public static  List<Pair<String, Integer>> topCatPlayer(String cat, int n) {
        List<Pair<String, Integer>> list = new ArrayList<>();
        Map<String, Stats> allPlayerStats = VanillaStatisticsDao.getAllPlayerStats();
        assert allPlayerStats != null;
        ArrayList<String> rank = new ArrayList<>(allPlayerStats.keySet());
        rank.sort((o1, o2) -> {
            int integer1 = 0;
            Map<String, Integer> map1 = allPlayerStats.get(o1).getStats().get(cat);
            if (map1 != null) {
                for (Integer value : map1.values()) {
                    if (value != null) {
                        integer1 += value;
                    }
                }
            }

            int integer2 = 0;
            Map<String, Integer> map2= allPlayerStats.get(o2).getStats().get(cat);
            if (map2 != null) {
                for (Integer value : map2.values()) {
                    if (value != null) {
                        integer2 += value;
                    }
                }
            }
            return integer2 - integer1;
        });
        for (int i = 0; i < n && i < rank.size(); i++) {

            int cnt = 0;
            for (Integer value : allPlayerStats.get(rank.get(i)).getStats().get(cat).values()) {
                if (value != null) {
                    cnt += value;
                }
            }
            list.add(new Pair<>(rank.get(i), cnt));
        }
        return list;
    }


    public static void main(String[] args) {
        List<Pair<String, Integer>> list = topCatPlayer("minecraft:broken", 10);

        for (Pair<String, Integer> stringIntegerPair : list) {
            System.out.println(stringIntegerPair.getKey() + " " + stringIntegerPair.getValue());
        }

    }
}
