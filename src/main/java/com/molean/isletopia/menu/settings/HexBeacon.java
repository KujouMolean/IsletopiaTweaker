package com.molean.isletopia.menu.settings;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HexBeacon extends ChestMenu {


    private enum BeaconState {
        ENABLED, DISABLED, NOT_ACTIVATED;

        public String getMessage(Player player) {
            switch (this) {
                case ENABLED -> {
                    return MessageUtils.getMessage(player, "menu.hexbeacon.enabled");
                }
                case DISABLED -> {
                    return MessageUtils.getMessage(player, "menu.hexbeacon.disabled");
                }
                case NOT_ACTIVATED -> {
                    return MessageUtils.getMessage(player, "menu.hexbeacon.notActivated");
                }
            }
            return this.toString();
        }
    }
    
    public HexBeacon(Player player) {
        super(player, 1, Component.text(MessageUtils.getMessage(player, "menu.hexbeacon.title")));
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            return;
        }
        BeaconState speed =BeaconState.NOT_ACTIVATED;
        if (currentIsland.containsFlag("EnableHexBeaconSpeed")) {
            speed = BeaconState.ENABLED;
        } else if (currentIsland.containsFlag("DisableHexBeaconSpeed")) {
            speed = BeaconState.DISABLED;
        }
        BeaconState fastDigging =BeaconState.NOT_ACTIVATED;
        if (currentIsland.containsFlag("EnableHexBeaconFastDigging")) {
            fastDigging = BeaconState.ENABLED;
        } else if (currentIsland.containsFlag("DisableHexBeaconFastDigging")) {
            fastDigging = BeaconState.DISABLED;
        }
        BeaconState increaseDamage =BeaconState.NOT_ACTIVATED;
        if (currentIsland.containsFlag("EnableHexBeaconIncreaseDamage")) {
            increaseDamage = BeaconState.ENABLED;
        } else if (currentIsland.containsFlag("DisableHexBeaconIncreaseDamage")) {
            increaseDamage = BeaconState.DISABLED;
        }
        BeaconState jump =BeaconState.NOT_ACTIVATED;
        if (currentIsland.containsFlag("EnableHexBeaconJump")) {
            jump = BeaconState.ENABLED;
        } else if (currentIsland.containsFlag("DisableHexBeaconJump")) {
            jump = BeaconState.DISABLED;
        }
        BeaconState damageResistance =BeaconState.NOT_ACTIVATED;
        if (currentIsland.containsFlag("EnableHexBeaconDamageResistance")) {
            damageResistance = BeaconState.ENABLED;
        } else if (currentIsland.containsFlag("DisableHexBeaconDamageResistance")) {
            damageResistance = BeaconState.DISABLED;
        }
        BeaconState regeneration =BeaconState.NOT_ACTIVATED;
        if (currentIsland.containsFlag("EnableHexBeaconRegeneration")) {
            regeneration = BeaconState.ENABLED;
        } else if (currentIsland.containsFlag("DisableHexBeaconRegeneration")) {
            regeneration = BeaconState.DISABLED;
        }
        ItemStackSheet speedItem = ItemStackSheet.fromString(Material.LEATHER_BOOTS,
                MessageUtils.getMessage(player, "menu.hexbeacon.info",
                        Pair.of("effect", MessageUtils.getMessage(player, "menu.hexbeacon.speed")),
                        Pair.of("state", speed.getMessage(player))
                ));
        if (speed.equals(BeaconState.NOT_ACTIVATED)) {
            speedItem.addLore(MessageUtils.getMessage(player,"menu.hexbeacon.heartNeed"));
        }
        BeaconState finalSpeed = speed;
        this.item(0, speedItem.build(), () -> {
            if (finalSpeed.equals(BeaconState.ENABLED)) {
                player.performCommand("is disableHexBeaconSpeed");

            } else {
                player.performCommand("is enableHexBeaconSpeed");

            }
            Tasks.INSTANCE.async(() -> new HexBeacon(player).open());
        });

        ItemStackSheet fastDiggingItem = ItemStackSheet.fromString(Material.GOLDEN_PICKAXE,  MessageUtils.getMessage(player, "menu.hexbeacon.info",
                Pair.of("effect", MessageUtils.getMessage(player, "menu.hexbeacon.fastDigging")),
                Pair.of("state", fastDigging.getMessage(player))
        ));
        if (fastDigging.equals(BeaconState.NOT_ACTIVATED)) {
            fastDiggingItem.addLore(MessageUtils.getMessage(player,"menu.hexbeacon.heartNeed"));
        }
        BeaconState finalFastDigging = fastDigging;
        this.item(1, fastDiggingItem.build(), () -> {
            if (finalFastDigging.equals(BeaconState.ENABLED)) {

                player.performCommand("is disableHexBeaconFastDigging");
            } else {
                player.performCommand("is enableHexBeaconFastDigging");

            }
             Tasks.INSTANCE.async(() -> new HexBeacon(player).open());
        });

        ItemStackSheet jumpItem = ItemStackSheet.fromString(Material.RABBIT_FOOT,  MessageUtils.getMessage(player, "menu.hexbeacon.info",
                Pair.of("effect", MessageUtils.getMessage(player, "menu.hexbeacon.jump")),
                Pair.of("state", jump.getMessage(player))
        ));
        if (jump.equals(BeaconState.NOT_ACTIVATED)) {
            jumpItem.addLore(MessageUtils.getMessage(player,"menu.hexbeacon.heartNeed"));
        }
        BeaconState finalJump = jump;
        this.item(2, jumpItem.build(), () -> {
            if (finalJump.equals(BeaconState.ENABLED)) {
                player.performCommand("is disableHexBeaconJump");
            } else {
                player.performCommand("is enableHexBeaconJump");

            }
             Tasks.INSTANCE.async(() -> new HexBeacon(player).open());
        });

        ItemStackSheet damageResistanceItem = ItemStackSheet.fromString(Material.SHIELD,  MessageUtils.getMessage(player, "menu.hexbeacon.info",
                Pair.of("effect", MessageUtils.getMessage(player, "menu.hexbeacon.resistance")),
                Pair.of("state", regeneration.getMessage(player))
        ));
        if (damageResistance.equals(BeaconState.NOT_ACTIVATED)) {
            damageResistanceItem.addLore(MessageUtils.getMessage(player,"menu.hexbeacon.heartNeed"));
        }
        BeaconState finalDamageResistance = damageResistance;
        this.item(3, damageResistanceItem.build(), () -> {
            if (finalDamageResistance.equals(BeaconState.ENABLED)) {

                player.performCommand("is disableHexBeaconDamageResistance");
            } else {
                player.performCommand("is enableHexBeaconDamageResistance");

            }
             Tasks.INSTANCE.async(() -> new HexBeacon(player).open());
        });

        ItemStackSheet increaseDamageItem = ItemStackSheet.fromString(Material.IRON_SWORD,  MessageUtils.getMessage(player, "menu.hexbeacon.info",
                Pair.of("effect", MessageUtils.getMessage(player, "menu.hexbeacon.increaseDamage")),
                Pair.of("state", increaseDamage.getMessage(player))
        ));
        if (increaseDamage.equals(BeaconState.NOT_ACTIVATED)) {
            increaseDamageItem.addLore(MessageUtils.getMessage(player,"menu.hexbeacon.heartNeed"));
        }
        BeaconState finalIncreaseDamage = increaseDamage;
        this.item(4, increaseDamageItem.build(), () -> {
            if (finalIncreaseDamage.equals(BeaconState.ENABLED)) {
                player.performCommand("is disableHexBeaconIncreaseDamage");
            } else {
                player.performCommand("is enableHexBeaconIncreaseDamage");
            }
             Tasks.INSTANCE.async(() -> new HexBeacon(player).open());
        });
        ItemStackSheet regenerationItem = ItemStackSheet.fromString(Material.APPLE,  MessageUtils.getMessage(player, "menu.hexbeacon.info",
                Pair.of("effect", MessageUtils.getMessage(player, "menu.hexbeacon.regeneration")),
                Pair.of("state", regeneration.getMessage(player))
        ));
        if (regeneration.equals(BeaconState.NOT_ACTIVATED)) {
            regenerationItem.addLore(MessageUtils.getMessage(player,"menu.hexbeacon.heartNeed"));
        }
        BeaconState finalRegeneration = regeneration;
        this.item(5, regenerationItem.build(), () -> {
            if (finalRegeneration.equals(BeaconState.ENABLED)) {
                player.performCommand("is disableHexBeaconRegeneration");
            } else {
                player.performCommand("is enableHexBeaconRegeneration");
            }
             Tasks.INSTANCE.async(() -> new HexBeacon(player).open());
        });

        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.settings"));

        itemWithAsyncClickEvent(8, father.build(), () -> new SettingsMenu(player).open());
    }
}
