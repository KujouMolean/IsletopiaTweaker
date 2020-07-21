package com.molean.isletopiatweakers;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class IsletopiaTweakers extends JavaPlugin implements Listener {


    private static IsletopiaTweakers isletopiaTweakers;

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }

    @Override
    public void onEnable() {
        isletopiaTweakers = this;
        ConfigUtils.setupConfig(this);
        ConfigUtils.configOuput("guide.yml");

        new AnimalProtect();
        new AuthReward();
        new ClockMenu();
        new GuideBook();
        new IsletopiaUtils();
        new LavaProtect();
        new NewbieOperation();
        new ObsidianRecovery();
        new PlayerReferHighLight();
        new PreventCreeperBreak();
        new RemoveDisgustingMob();
        new SkinSync();
        new TeleportSign();
        new RegistRecipe();
        new AddMerchant();
        new Neon();
    }
}
