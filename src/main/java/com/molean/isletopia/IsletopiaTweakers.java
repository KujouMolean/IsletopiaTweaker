package com.molean.isletopia;

import com.molean.isletopia.bungee.IsletopiaBungee;
import com.molean.isletopia.distribute.IsletopiaDistributeSystem;
import com.molean.isletopia.distribute.parameter.IsletopiaParamter;
import com.molean.isletopia.infrastructure.IsletopiaInfrastructure;
import com.molean.isletopia.menu.MenuCommand;
import com.molean.isletopia.modifier.IsletopiaModifier;
import com.molean.isletopia.protect.IsletopiaProtect;
import com.molean.isletopia.utils.ConfigUtils;
import com.molean.isletopia.infrastructure.individual.I18n;
import org.bukkit.plugin.java.JavaPlugin;

public final class IsletopiaTweakers extends JavaPlugin {

    private static IsletopiaTweakers isletopiaTweakers;

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }

    @Override
    public void onEnable() {
        isletopiaTweakers = this;
        ConfigUtils.setupConfig(this);
        new IsletopiaInfrastructure();
        new IsletopiaModifier();
        new IsletopiaDistributeSystem();
        new IsletopiaParamter();
        new IsletopiaProtect();
        new MenuCommand();
        new IsletopiaBungee();
//        new IsletopiaStory();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }
}
