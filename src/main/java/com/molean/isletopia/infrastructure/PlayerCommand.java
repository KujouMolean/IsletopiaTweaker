package com.molean.isletopia.infrastructure;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.annotations.Completion;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import org.bukkit.entity.Player;

import java.util.UUID;

@Singleton
@CommandAlias("player")
public class PlayerCommand extends BaseCommand {


    @Default
    @Completion("@players @empty")
    public void onDefault(Player player, String target) {
        Tasks.INSTANCE.async(() -> {
            UUID uuid = UUIDManager.get(target);
            if (uuid != null) {
                new PlayerMenu(player, uuid).open();
            }
        });
    }


}
