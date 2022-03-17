package com.molean.isletopia.menu.assist;

import com.molean.isletopia.infrastructure.individual.bars.SidebarManager;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.utils.ChatChannelUtils;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class AssistMenu extends ChestMenu {

    public AssistMenu(Player player) {
        super(player, 3, Component.text(  MessageUtils.getMessage(player, "menu.assist.title")));

        String messageOn = MessageUtils.getMessage(player, "menu.assist.on");
        String messageOff = MessageUtils.getMessage(player, "menu.assist.off");

        String sidebar = SidebarManager.INSTANCE.getSidebar(player.getUniqueId());
        {
            String message = MessageUtils.getMessage(player, "menu.assist.entityBar",
                    Pair.of("status", "EntityBar".equalsIgnoreCase(sidebar) ? messageOn : messageOff));
            ItemStackSheet itemStackSheet = ItemStackSheet.fromString(Material.EGG, message);
            this.item(0, itemStackSheet.build(), () -> {
                player.performCommand("assist EntityBar");
                close();
            });
        }

        {
            String message = MessageUtils.getMessage(player, "menu.assist.productionBar",
                    Pair.of("status", "ProductionBar".equalsIgnoreCase(sidebar) ? messageOn : messageOff));
            ItemStackSheet itemStackSheet = ItemStackSheet.fromString(Material.BOOK, message);

            this.item(1, itemStackSheet.build(), () -> {
                player.performCommand("assist ProductionBar");
                close();
            });
        }
        {
            ItemStackSheet inbox = ItemStackSheet.fromString(Material.CHEST, MessageUtils.getMessage(player,"menu.assist.mailbox" ));

            this.item(2, inbox.build(), () -> {
                player.performCommand("assist mailbox ");
                close();
            });

        }

        {
            String messageClaimed = MessageUtils.getMessage(player, "menu.assist.claimed");
            String messageNotClaimed = MessageUtils.getMessage(player, "menu.assist.notClaimed");
            String key = "IslandClaim-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            boolean freeClaimed = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, key);
            String message = MessageUtils.getMessage(player, "menu.assist.create",
                    Pair.of("status", freeClaimed ? messageClaimed : messageNotClaimed));
            ItemStackSheet create = ItemStackSheet.fromString(Material.GRASS_BLOCK, message);
            this.item(3, create.build(), () -> {
                player.performCommand("is create");
                close();
            });
        }


        {
            ItemStackSheet claim = ItemStackSheet.fromString(Material.STONE, MessageUtils.getMessage(player, "menu.assist.claimOffline"));
            this.item(4, claim.build(), this::close);
        }


        boolean disablePlayerRide = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisablePlayerRide");
        boolean disableRailWay = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableRailWay");
        boolean disableIronElevator = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableIronElevator");
        boolean disableChairs = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableChairs");
        boolean disableLavaProtect = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableLavaProtect");
        boolean disableSingleIslandMenu = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableSingleIslandMenu");
        boolean disablePlayerMob = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisablePlayerMob");
        boolean disableKeepInventory = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableKeepInventory");
        boolean autoFloor = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "AutoFloor");

        {
            //DisableChairs
            String message = MessageUtils.getMessage(player, "menu.assist.sit",
                    Pair.of("status", disableChairs ? messageOff : messageOn));
            ItemStackSheet sit = ItemStackSheet.fromString(Material.BRICK_STAIRS, message);
            this.item(5, sit.build(), () -> {
                player.performCommand("assist DisableChairs " + !disableChairs);
                close();
            });
        }

        {

            //DisableRailWay
            String message = MessageUtils.getMessage(player, "menu.assist.rail",
                    Pair.of("status", disableRailWay ? messageOff : messageOn));

            ItemStackSheet rail = ItemStackSheet.fromString(Material.RAIL, message);

            this.item(6, rail.build(), () -> {
                player.performCommand("assist DisableRailWay " + !disableRailWay);
                close();
            });
        }

        {
            //DisableIronElevator
            String message = MessageUtils.getMessage(player, "menu.assist.elevator", Pair.of("status", disableIronElevator ?
                    messageOff : messageOn));
            ItemStackSheet elevator = ItemStackSheet.fromString(Material.IRON_BLOCK, message);

            this.item(7, elevator.build(), () -> {
                player.performCommand("assist DisableIronElevator" + !disableIronElevator);
                close();
            });

        }


        {
            //DisablePlayerRide
            String message = MessageUtils.getMessage(player, "menu.assist.ride",
                    Pair.of("status", disablePlayerRide ? messageOff : messageOn));
            ItemStackSheet ride = ItemStackSheet.fromString(Material.LEAD, message);
            this.item(8, ride.build(), () -> {
                player.performCommand("assist DisablePlayerRider" + !disablePlayerRide);
                close();
            });
        }


        {
            //DisableLavaProtect
            String message = MessageUtils.getMessage(player, "menu.assist.lava",
                    Pair.of("status", disableLavaProtect ? messageOff : messageOn));

            ItemStackSheet lava = ItemStackSheet.fromString(Material.LAVA_BUCKET, message);

            this.item(9, lava.build(), () -> {
                player.performCommand("assist DisableLavaProtect" + !disableLavaProtect);

                close();
            });

        }
        {

            String message = MessageUtils.getMessage(player, "menu.assist.download");
            ItemStackSheet save = ItemStackSheet.fromString(Material.BRICKS, message);
            this.item(10, save.build(), () -> {
                player.performCommand("download");
                close();
            });

        }

        {

            String message = MessageUtils.getMessage(player, "menu.assist.tutor");
            ItemStackSheet skip = ItemStackSheet.fromString(Material.FEATHER, message);
            this.item(11, skip.build(), () -> {
                player.performCommand("skiptutor");
            });
        }


        {
            String message = MessageUtils.getMessage(player, "menu.assist.visit",
                    Pair.of("status", disableSingleIslandMenu ? messageOn : messageOff));
            ItemStackSheet visit = ItemStackSheet.fromString(Material.CLOCK, message);
            this.item(12, visit.build(), () -> {
                player.performCommand("assist DisableSingleIslandMenu " + !disableSingleIslandMenu);
                close();
            });
        }

        {
            String message = MessageUtils.getMessage(player, "menu.assist.death",
                    Pair.of("status", disablePlayerMob ? messageOff : messageOn));
            ItemStackSheet death = ItemStackSheet.fromString(Material.PLAYER_HEAD, message);
            this.item(13, death.build(), () -> {
                player.performCommand("assist DisablePlayerMob " + !disablePlayerMob);
                close();
            });
        }

        {
            String message = MessageUtils.getMessage(player, "menu.assist.inventory",
                    Pair.of("status", disableKeepInventory ? messageOff : messageOn));
            ItemStackSheet keep = ItemStackSheet.fromString(Material.BUNDLE, message);
            this.item(14, keep.build(), () -> {
                player.performCommand("assist DisableKeepInventory " + !disableKeepInventory);
                close();
            });
        }


        {
            Set<String> channels = ChatChannelUtils.getChannels(player.getUniqueId());
            String message = MessageUtils.getMessage(player, "menu.assist.channel", Pair.of("channels", String.join(",", channels)));
            ItemStackSheet channel = ItemStackSheet.fromString(Material.SUNFLOWER, message);
            this.item(15, channel.build(), () -> {
//                new ChatChannel(player).open();
                player.performCommand("assist ChatChannel");
            });

        }

        {
            String message = MessageUtils.getMessage(player, "menu.assist.floor",
                    Pair.of("status", autoFloor ? messageOn : messageOff));
            ItemStackSheet floor = ItemStackSheet.fromString(Material.STONE_SLAB, message);

            this.item(16, floor.build(), () -> {
                player.performCommand("assist AutoFloor " + !autoFloor);
                close();
            });
        }

        {
            String autoCraft = PlayerPropertyManager.INSTANCE.getProperty(player, "AutoCraft");
            if (autoCraft == null) {
                autoCraft = "";
            }
            String[] split = autoCraft.split(",");
            for (int i = 0; i < split.length; i++) {
                Material material = null;
                try {
                    material = Material.valueOf(split[i]);
                    String s = LangUtils.get(player.locale(), material.translationKey());
                    split[i] = s;
                } catch (IllegalArgumentException ignored) {
                }
            }
            ItemStackSheet craft = ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK,
                    MessageUtils.getMessage(player, "menu.assist.craft", Pair.of("status", String.join(",", split))));
            this.item(17, craft.build());

            this.clickEventSync(ClickType.LEFT, 17, () -> {
                player.performCommand("assist AutoCraft");
                close();
            });
            this.clickEventSync(ClickType.RIGHT, 17, () -> {
                player.performCommand("assist AutoCraft clear");
            });
        }
        {
            ItemStackSheet slime = ItemStackSheet.fromString(Material.SLIME_BLOCK, MessageUtils.getMessage(player, "menu.assist.slime"));
            this.item(18, slime.build(), () -> {
                player.performCommand("assist slime");
            });
        }

        {
            ItemStackSheet modification = ItemStackSheet.fromString(Material.WRITABLE_BOOK, MessageUtils.getMessage(player, "menu.assist.modification"));
            this.itemWithAsyncClickEvent(19, modification.build(), () -> new ModificationMenu(player).open());

        }

        {
            ItemStackSheet father = new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main"));
            itemWithAsyncClickEvent(26, father.build(), () -> new MainMenu(player).open());
        }

    }
}
