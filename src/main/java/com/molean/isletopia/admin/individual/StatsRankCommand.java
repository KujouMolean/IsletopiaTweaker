package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.VanillaStatisticsDao;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.statistics.vanilla.Stats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StatsRankCommand implements CommandExecutor {
    public StatsRankCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("rank")).setExecutor(this);
    }

    public static List<Pair<String, Integer>> topPlayers(String cat, String item, int n) {
        List<Pair<String, Integer>> list = new ArrayList<>();
        Map<String, Stats> allPlayerStats = VanillaStatisticsDao.getAllPlayerStats();
        assert allPlayerStats != null;
        ArrayList<String> rank = new ArrayList<>(allPlayerStats.keySet());
        rank.sort((o1, o2) -> {
            Integer integer1 = null;
            try {
                integer1 = allPlayerStats.get(o1).getStats().get(cat).get(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (integer1 == null) {
                integer1 = 0;
            }
            Integer integer2 = null;
            try {
                integer2 = allPlayerStats.get(o2).getStats().get(cat).get(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1) {
            sender.sendMessage("参数不足");
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "count" ->{
                if (args.length < 3) {
                    sender.sendMessage("参数不足");
                    return true;
                }

                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    long count = count(args[1], args[2]);
                    sender.sendMessage(count + "");
                });

            }
            case "item"->{

                if (args.length < 3) {
                    sender.sendMessage("参数不足");
                    return true;
                }

                int n;

                try {
                    n = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("参数错误");
                    return true;
                }
                int divide = 1;
                if (args.length >= 4) {
                    try {
                        divide = Integer.parseInt(args[3]);
                    } catch (NumberFormatException ignored) {


                    }
                }


                int finalN = n;
                int finalDivide = divide;
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    List<Pair<String, Integer>> list = topItem(args[2], finalN);
                    for (Pair<String, Integer> stringIntegerPair : list) {
                        sender.sendMessage(stringIntegerPair.getKey() + " " + (stringIntegerPair.getValue() / finalDivide));
                    }
                });

            }
            case "cat"->{
                if (args.length < 3) {
                    sender.sendMessage("参数不足");
                    return true;
                }

                int n = 0;
                try {
                    n = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("参数错误");
                    return true;
                }

                int divide = 1;
                if (args.length >= 4) {
                    try {
                        divide = Integer.parseInt(args[3]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                int finalDivide = divide;
                int finalN = n;
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    List<Pair<String, Integer>> list = topCatPlayer(args[2], finalN);
                    for (Pair<String, Integer> stringIntegerPair : list) {
                        sender.sendMessage(stringIntegerPair.getKey() + " " +  (stringIntegerPair.getValue() / finalDivide));
                    }
                });

            }
            case "player"->{
                if (args.length < 4) {
                    sender.sendMessage("参数不足");
                    return true;
                }

                int n;
                try {
                    n = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("参数错误");
                    return true;
                }
                int divide = 1;
                if (args.length >= 5) {
                    try {
                        divide = Integer.parseInt(args[4]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                int finalDivide = divide;
                int finalN = n;
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    List<Pair<String, Integer>> list = topPlayers(args[2], args[3], finalN);
                    for (Pair<String, Integer> stringIntegerPair : list) {
                        sender.sendMessage(stringIntegerPair.getKey() + " " + (stringIntegerPair.getValue() / finalDivide));

                    }
                });
            }

            default -> {
                throw new IllegalStateException("Unexpected value: " + args[0].toLowerCase(Locale.ROOT));
            }

        }
        return true;
    }
}
