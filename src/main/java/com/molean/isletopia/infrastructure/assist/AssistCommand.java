package com.molean.isletopia.infrastructure.assist;

import com.molean.isletopia.menu.MailListMenu;
import com.molean.isletopia.modifier.individual.AutoCraft;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class AssistCommand extends MultiCommand {
    public AssistCommand() {
        super("assist");

        this.addSubCommand(new SimpleCommand("EntityBar", (player) -> {
            Tasks.INSTANCE.sync(() -> {
                player.performCommand("entitybar");
            });
        }));
        this.addSubCommand(new SimpleCommand("ProductionBar", (player) -> {
            Tasks.INSTANCE.sync(() -> {
                player.performCommand("productionbar");
            });
        }));
        this.addSubCommand(new SimpleCommand("mailbox", (player) -> {
            Tasks.INSTANCE.async(() -> new MailListMenu(player).open());
            MessageUtils.info(player, "已为你打开邮箱");

        }));
        this.addSubCommand(new BooleanCommand("DisableChairs", (player, aBoolean) -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableChairs", aBoolean.toString());
            MessageUtils.info(player, "OK");
        }));

        this.addSubCommand(new BooleanCommand("DisableRailway", (player, aBoolean) -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableRailWay", aBoolean.toString());
            MessageUtils.info(player, "OK");
        }));

        this.addSubCommand(new BooleanCommand("DisableIronElevator", (player, aBoolean) -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableIronElevator", aBoolean.toString());
            MessageUtils.info(player, "OK");
        }));

        this.addSubCommand(new BooleanCommand("DisablePlayerRide", (player, aBoolean) -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisablePlayerRide", aBoolean.toString());
            MessageUtils.info(player, "OK");
        }));
        this.addSubCommand(new BooleanCommand("DisableLavaProtect", (player, aBoolean) -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableLavaProtect", aBoolean.toString());
            MessageUtils.info(player, "OK");
        }));
        this.addSubCommand(new BooleanCommand("DisableSingleIslandMenu", (player, aBoolean) -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableSingleIslandMenu", aBoolean.toString());
            MessageUtils.info(player, "OK");
        }));
        this.addSubCommand(new BooleanCommand("DisablePlayerMob", (player, aBoolean) -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisablePlayerMob", aBoolean.toString());
            MessageUtils.info(player, "OK");
        }));
        this.addSubCommand(new BooleanCommand("DisableKeepInventory", (player, aBoolean) -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableKeepInventory", aBoolean.toString());
            MessageUtils.info(player, "OK");
        }));
        this.addSubCommand(new BooleanCommand("AutoFloor", (player, aBoolean) -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "AutoFloor", aBoolean.toString());
            MessageUtils.info(player, "OK");
        }));
        this.addSubCommand(new SubCommand("AutoCraft", (player, strings) -> {
            if (strings.size() == 1 && strings.get(0).equals("clear")) {
                AutoCraft.setSelectedMaterialAsync(player, new ArrayList<>(), () -> {
                    MessageUtils.success(player, "assist.autocraft.clear");
                });
            } if (strings.size() == 1 && strings.get(0).equals("list")) {
                List<Material> selectedMaterial = AutoCraft.getSelectedMaterial(player);
                ArrayList<String> strings1 = new ArrayList<>();
                for (Material material : selectedMaterial) {
                    strings1.add(LangUtils.get(player.locale(), material.translationKey()));
                }
                MessageUtils.info(player, String.join(",", strings1));

            } else if (strings.size() > 0) {
                ArrayList<Material> materials = new ArrayList<>();
                int size = AutoCraft.getSelectedMaterial(player).size();
                for (String string : strings) {
                    Material material = null;
                    try {
                        material = Material.valueOf(string);
                    } catch (IllegalArgumentException ignored) {
                    }
                    if (material != null && size < 10) {
                        materials.add(material);
                    }
                }
                AutoCraft.addMaterial(player, () -> {
                    for (Material material : materials) {
                        MessageUtils.success(player, MessageUtils.getMessage(player, "assist.autocraft.add", Pair.of("item", LangUtils.get(player.locale(), material.translationKey()))));
                    }
                }, materials.toArray(new Material[]{}));
            } else {
                Material type = player.getInventory().getItemInOffHand().getType();
                if (AutoCraft.getSelectedMaterial(player).size() >= 10) {
                    MessageUtils.fail(player, "assist.autocraft.cap");
                    return;
                }
                if (type.equals(Material.AIR)) {
                    MessageUtils.fail(player, "assist.autocraft.help");
                    return;
                }
                if (!AutoCraft.materials.containsKey(type)) {
                    MessageUtils.fail(player, "assist.autocraft.invalid");
                    return;
                }
                AutoCraft.addMaterial(player, () -> {
                    MessageUtils.success(player, MessageUtils.getMessage(player, "assist.autocraft.add", Pair.of("item", LangUtils.get(player.locale(), type.translationKey()))));
                }, type);
            }
        }, (player, strings) -> new ArrayList<>()));
        this.addSubCommand(new SimpleCommand("slime", (player) -> {
            Tasks.INSTANCE.sync(() -> player.performCommand("slime"));
        }));
    }


}
