package com.molean.isletopiatweakers;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class AuthReward implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§c格式错误, 请输入/account <邮箱> <密码>.");
            return true;
        }
        Player player = (Player) sender;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (authen(player.getName(), args[0], args[1])) {
                sender.sendMessage("§2验证成功, 已发放奖励. 为保证阁下正版账号安全, 请立刻修改密码!!");
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "aach add 1 Custom.genuine " + player.getName());
                });

            } else {
                sender.sendMessage("§c验证失败, 请检查邮箱和密码.");
            }
        });
        return true;
    }

    public static boolean authen(String playername, String email, String pass) {
        String url_post = "https://authserver.mojang.com/authenticate";
        try {
            URL url = new URL(url_post);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            JSONObject obj = new JSONObject();
            obj.put("username", email);
            obj.put("password", pass);
            JSONObject obj2 = new JSONObject();
            obj2.put("name", "Minecraft");
            obj2.put("version", 1);
            obj.put("agent", obj2);
            out.writeBytes(obj.toString());
            out.flush();
            out.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer stbu = new StringBuffer();
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                stbu.append(lines);
            }

            String i = stbu.toString();
            JSONObject x = (JSONObject) (new JSONParser()).parse(i);
            String m = x.get("selectedProfile").toString();
            JSONObject y = (JSONObject) (new JSONParser()).parse(m);
            reader.close();
            connection.disconnect();
            if (playername.equals(y.get("name"))) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
