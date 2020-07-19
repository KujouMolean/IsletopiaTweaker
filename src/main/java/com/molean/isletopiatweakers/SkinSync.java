package com.molean.isletopiatweakers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.molean.advancedcommand.AdvancedCommand;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineskin.MineskinClient;
import org.mineskin.Model;
import org.mineskin.SkinOptions;
import org.mineskin.Visibility;
import org.mineskin.data.Skin;
import org.mineskin.data.SkinCallback;
import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.bukkit.SkinsRestorerBukkitAPI;
import skinsrestorer.shared.exception.SkinRequestException;

import java.io.*;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class SkinSync implements Listener {
    private MineskinClient skinClient;
    private SkinsRestorer skinsRestorer;
    private SkinsRestorerBukkitAPI skinsAPI;
    public SkinSync(){
        getServer().getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        skinClient = new MineskinClient();
        skinsRestorer = JavaPlugin.getPlugin(SkinsRestorer.class);
        skinsAPI = skinsRestorer.getSkinsRestorerBukkitAPI();
    }
    @EventHandler
    public void onLogin(LoginEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            Model skinModule = getSkinModule(event.getPlayer().getName());
            String urlString = "https://skin.molean.com/skin/" + event.getPlayer().getName() + ".png";
            setSkin(event.getPlayer(), urlString, getSkinModule(event.getPlayer().getName()));
        });
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        event.getHandlers().unregister(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("SkinsRestorer")));
    }

    public static Model getSkinModule(String username) {
        try {
            String urlStr = "https://skin.molean.com/" + username + ".json";
            URL url = new URL(urlStr);
            InputStream inputStream = url.openStream();
            byte[] bytes = readInputStream(inputStream);
            inputStream.read(bytes);
            String s = new String(bytes);
            int slimPos = s.indexOf("\"slim\"");
            int defaultPos = s.indexOf("\"default\"");
            if (slimPos < 0 && defaultPos < 0)
                return Model.DEFAULT;
            if (slimPos < 0)
                return Model.DEFAULT;
            if (defaultPos < 0)
                return Model.SLIM;
            if (slimPos < defaultPos)
                return Model.SLIM;
            else
                return Model.DEFAULT;
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public void setSkin(Player player, String urlString, Model model) {
        try {
            URL url = new URL(urlString);
            Bukkit.getLogger().info(urlString);
            skinClient.generateUrl(url.toString(), SkinOptions.create(player.getName(), model, Visibility.PRIVATE), new SkinCallback() {
                public void noSkin() {
                    try {
                        String skin = SkinsRestorer.getInstance().getSkinStorage().getDefaultSkinNameIfEnabled(player.getName());
                        SkinsRestorer.getInstance().getFactory().applySkin(player, SkinsRestorer.getInstance().getSkinStorage().getOrCreateSkinForPlayer(skin));
                    } catch (SkinRequestException e) {
                        return;
                    }
                    player.sendMessage("§8[§6登录系统§8] §c阁下未设置皮肤, 点击此处前往设置 -> §nskin.molean.com");
                }

                @Override
                public void error(String errorMessage) {
                    Bukkit.getLogger().info(errorMessage);
                    noSkin();
                }

                @Override
                public void exception(Exception exception) {
                    exception.printStackTrace();
                    noSkin();
                }

                @Override
                public void parseException(Exception exception, String body) {
                    noSkin();
                }

                @Override
                public void done(Skin skin) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", skin.data.uuid.toString());
                    jsonObject.addProperty("name", "");
                    JsonObject property = new JsonObject();
                    property.addProperty("name", "textures");
                    property.addProperty("value", skin.data.texture.value);
                    property.addProperty("signature", skin.data.texture.signature);
                    JsonArray propertiesArray = new JsonArray();
                    propertiesArray.add(property);
                    jsonObject.add("properties", propertiesArray);
                    AdvancedCommand.setParameter(player, "skinValue", skin.data.texture.value);
                    UUID uuid = UUID.randomUUID();
                    String filename = "plugins/SkinsRestorer/Skins/" + uuid + ".skin";
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.HOUR, 12);
                    long timestamp = calendar.getTimeInMillis();
                    String data = skin.data.texture.value + "\n" + skin.data.texture.signature + "\n" + timestamp;
                    try {
                        File file = new File(filename);
                        if (!file.exists()) {
                            boolean newFile = file.createNewFile();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(data.getBytes());
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    skinsAPI.applySkin(player, skinsAPI.getSkinData(uuid.toString()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
