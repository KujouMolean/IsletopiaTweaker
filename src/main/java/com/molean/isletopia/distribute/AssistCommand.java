package com.molean.isletopia.assist;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.menu.MailListMenu;
import com.molean.isletopia.modifier.AutoCraft;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("assist")
@Singleton
public class AssistCommand extends BaseCommand {
    private final PlayerPropertyManager playerPropertyManager;

    public AssistCommand(PlayerPropertyManager playerPropertyManager, AutoCraft autoCraft) {
        this.playerPropertyManager = playerPropertyManager;
    }

    @Subcommand("EntityBar")
    public void entityBar(Player player) {
        player.performCommand("EntityBar");
    }

    @Subcommand("ProductionBar")
    public void productionBar(Player player) {
        player.performCommand("ProductionBar");
    }


    @Subcommand("Mailbox")
    public void mailbox(Player player) {
        Tasks.INSTANCE.async(() -> new MailListMenu(player).open());
        MessageUtils.info(player, "已为你打开邮箱");
    }

    @Subcommand("DisableRailWay")
    public void disableRailWay(Player player, boolean disable) {
        playerPropertyManager.setPropertyAsync(player, "DisableChairs", Boolean.toString(disable));
        MessageUtils.info(player, "OK");
    }

    @Subcommand("DisableIronElevator")
    public void disableIronElevator(Player player, boolean disable) {
        playerPropertyManager.setPropertyAsync(player, "DisableIronElevator", Boolean.toString(disable));
        MessageUtils.info(player, "OK");
    }

    @Subcommand("DisablePlayerRide")
    public void disablePlayerRide(Player player, boolean disable) {
        playerPropertyManager.setPropertyAsync(player, "DisablePlayerRide", Boolean.toString(disable));
        MessageUtils.info(player, "OK");
    }

    @Subcommand("DisableLavaProtect")
    public void disableLavaProtect(Player player, boolean disable) {
        playerPropertyManager.setPropertyAsync(player, "DisableLavaProtect", Boolean.toString(disable));
        MessageUtils.info(player, "OK");
    }

    @Subcommand("DisableSingleIslandMenu")
    public void disableSingleIslandMenu(Player player, boolean disable) {
        playerPropertyManager.setPropertyAsync(player, "DisableSingleIslandMenu", Boolean.toString(disable));
        MessageUtils.info(player, "OK");
    }

    @Subcommand("DisablePlayerMob")
    public void disablePlayerMob(Player player, boolean disable) {
        playerPropertyManager.setPropertyAsync(player, "DisablePlayerMob", Boolean.toString(disable));
        MessageUtils.info(player, "OK");
    }

    @Subcommand("DisableKeepInventory")
    public void disableKeepInventory(Player player, boolean disable) {
        playerPropertyManager.setPropertyAsync(player, "DisableKeepInventory", Boolean.toString(disable));
        MessageUtils.info(player, "OK");
    }

    @Subcommand("AutoFloor")
    public void autoFloor(Player player, boolean enable) {
        playerPropertyManager.setPropertyAsync(player, "AutoFloor", Boolean.toString(enable));
        MessageUtils.info(player, "OK");
    }

    @Subcommand("Slime")
    public void slime(Player player) {
        Tasks.INSTANCE.sync(() -> player.performCommand("slime"));
    }


    @Subcommand("AutoCraft")
    public static class AutoCraftCommand {
        private final AutoCraft autoCraft;

        public AutoCraftCommand(AutoCraft autoCraft) {
            this.autoCraft = autoCraft;
        }

        @Subcommand("clear")
        public void clear(Player player) {
            autoCraft.setSelectedMaterialAsync(player, new ArrayList<>(), () -> {
                MessageUtils.success(player, "assist.autoCraft.clear");
            });
        }

        @Subcommand("list")

        public void list(Player player) {
            List<Material> selectedMaterial = autoCraft.getSelectedMaterial(player);
            ArrayList<String> strings1 = new ArrayList<>();
            for (Material material : selectedMaterial) {
                strings1.add(LangUtils.get(player.locale(), material.translationKey()));
            }
            MessageUtils.info(player, String.join(",", strings1));
        }

        public void add(Player player, Material... tobeAdd) {

            ArrayList<Material> materials = new ArrayList<>();
            int size = autoCraft.getSelectedMaterial(player).size();
            for (Material material : tobeAdd) {
                if (material != null && size < 10) {
                    materials.add(material);
                }
            }
            autoCraft.addMaterial(player, () -> {
                for (Material material : materials) {
                    MessageUtils.success(player, MessageUtils.getMessage(player, "assist.autoCraft.add", Pair.of("item", LangUtils.get(player.locale(), material.translationKey()))));
                }
            }, materials.toArray(new Material[]{}));
        }

        public void def(Player player) {
            Material type = player.getInventory().getItemInOffHand().getType();
            if (autoCraft.getSelectedMaterial(player).size() >= 10) {
                MessageUtils.fail(player, "assist.autoCraft.cap");
                return;
            }
            if (type.equals(Material.AIR)) {
                MessageUtils.fail(player, "assist.autoCraft.help");
                return;
            }
            if (!AutoCraft.materials.containsKey(type)) {
                MessageUtils.fail(player, "assist.autoCraft.invalid");
                return;
            }
            autoCraft.addMaterial(player, () -> {
                MessageUtils.success(player, MessageUtils.getMessage(player, "assist.autoCraft.add", Pair.of("item", LangUtils.get(player.locale(), type.translationKey()))));
            }, type);
        }
    }

}
