package com.molean.isletopia.infrastructure;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.NoteUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

@Singleton
@CommandAlias("music")
public class Music extends BaseCommand {

    @Default
    public void onDefault(Player player, int interval, String... ser) {
        if (interval > 20 || interval < 1) {
            MessageUtils.notify(player, "间隔无效，请重新输入");
            return;
        }
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(ser));
        NoteUtils.playMulti(player, interval, strings);
    }
}
