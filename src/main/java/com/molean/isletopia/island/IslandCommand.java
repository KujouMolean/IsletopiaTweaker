package com.molean.isletopia.island;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.dialog.ConfirmDialog;
import com.molean.isletopia.menu.charge.PlayerChargeMenu;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.menu.visit.VisitMenu;
import com.molean.isletopia.menu.visit.VisitorMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.service.AccountService;
import com.molean.isletopia.shared.service.RedisService;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@CommandAlias("island|is|isletopia")
@Singleton
public class IslandCommand extends BaseCommand {

    @AutoInject
    private PlayerPropertyManager playerPropertyManager;
    @AutoInject
    private SidebarManager sidebarManager;
    @AutoInject
    private ChargeCommitter chargeCommitter;
    @AutoInject
    private AccountService accountService;
    @AutoInject
    private RedisService redisService;


    @Subcommand("priority")
    public void priority(Player player, @Flags("owner") LocalIsland currentIsland, int priority) {
        currentIsland.removeIslandFlag("Priority");
        currentIsland.addIslandFlag("Priority#" + priority);
        if (priority == 0) {
            currentIsland.removeIslandFlag("Priority");
        }
        MessageUtils.success(player, "island.command.ok");
    }

    @Subcommand("enableHexBeaconSpeed")
    public void enableHexBeaconSpeed(Player player, @Flags("owner") LocalIsland currentIsland) {
        if (currentIsland.containsFlag("EnableHexBeaconSpeed")) {
            MessageUtils.fail(player, "island.command.alreadyEnabled");
            return;
        }
        if (currentIsland.containsFlag("DisableHexBeaconSpeed")) {
            currentIsland.removeIslandFlag("DisableHexBeaconSpeed");
            currentIsland.addIslandFlag("EnableHexBeaconSpeed");
            MessageUtils.success(player, "island.command.ok");
        } else {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (!itemInOffHand.getType().equals(Material.HEART_OF_THE_SEA)) {
                MessageUtils.fail(player, "island.command.heartOfTheSeaNeeded");
                return;
            }
            itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
            player.getInventory().setItemInOffHand(itemInOffHand);
            currentIsland.addIslandFlag("EnableHexBeaconSpeed");
            MessageUtils.success(player, "island.command.ok");
        }
    }

    @Subcommand("disableHexBeaconSpeed")
    public void disableHexBeaconSpeed(Player player, @Flags("owner") LocalIsland currentIsland) {
        if (currentIsland.containsFlag("EnableHexBeaconSpeed")) {
            currentIsland.removeIslandFlag("EnableHexBeaconSpeed");
            currentIsland.addIslandFlag("DisableHexBeaconSpeed");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    @Subcommand("enableHexBeaconFastDigging")
    public void enableHexBeaconFastDigging(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (currentIsland.containsFlag("EnableHexBeaconFastDigging")) {
            MessageUtils.fail(player, "island.command.alreadyEnabled");
            return;
        }
        if (currentIsland.containsFlag("DisableHexBeaconFastDigging")) {
            currentIsland.removeIslandFlag("DisableHexBeaconFastDigging");
            currentIsland.addIslandFlag("EnableHexBeaconFastDigging");
            MessageUtils.success(player, "island.command.ok");
        } else {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (!itemInOffHand.getType().equals(Material.HEART_OF_THE_SEA)) {
                MessageUtils.fail(player, "island.command.heartOfTheSeaNeeded");
                return;
            }
            itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
            player.getInventory().setItemInOffHand(itemInOffHand);
            currentIsland.addIslandFlag("EnableHexBeaconFastDigging");
            MessageUtils.success(player, "island.command.ok");
        }
    }

    @Subcommand("disableHexBeaconFastDigging")
    public void disableHexBeaconFastDigging(Player player, @Flags("owner") LocalIsland currentIsland) {


        if (currentIsland.containsFlag("EnableHexBeaconFastDigging")) {
            currentIsland.removeIslandFlag("EnableHexBeaconFastDigging");
            currentIsland.addIslandFlag("DisableHexBeaconFastDigging");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    @Subcommand("enableHexBeaconIncreaseDamage")
    public void enableHexBeaconIncreaseDamage(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (currentIsland.containsFlag("EnableHexBeaconIncreaseDamage")) {
            MessageUtils.fail(player, "island.command.alreadyEnabled");
            return;
        }
        if (currentIsland.containsFlag("DisableHexBeaconIncreaseDamage")) {
            currentIsland.removeIslandFlag("DisableHexBeaconIncreaseDamage");
            currentIsland.addIslandFlag("EnableHexBeaconIncreaseDamage");
            MessageUtils.success(player, "island.command.ok");
        } else {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (!itemInOffHand.getType().equals(Material.HEART_OF_THE_SEA)) {
                MessageUtils.fail(player, "island.command.heartOfTheSeaNeeded");
                return;
            }
            itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
            player.getInventory().setItemInOffHand(itemInOffHand);
            currentIsland.addIslandFlag("EnableHexBeaconIncreaseDamage");
            MessageUtils.success(player, "island.command.ok");
        }
    }

    @Subcommand("disableHexBeaconIncreaseDamage")
    public void disableHexBeaconIncreaseDamage(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (currentIsland.containsFlag("EnableHexBeaconIncreaseDamage")) {
            currentIsland.removeIslandFlag("EnableHexBeaconIncreaseDamage");
            currentIsland.addIslandFlag("DisableHexBeaconIncreaseDamage");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    @Subcommand("enableHexBeaconJump")
    public void enableHexBeaconJump(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (currentIsland.containsFlag("EnableHexBeaconJump")) {
            MessageUtils.fail(player, "island.command.alreadyEnabled");
            return;
        }
        if (currentIsland.containsFlag("DisableHexBeaconJump")) {
            currentIsland.removeIslandFlag("DisableHexBeaconJump");
            currentIsland.addIslandFlag("EnableHexBeaconJump");
            MessageUtils.success(player, "island.command.ok");
        } else {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (!itemInOffHand.getType().equals(Material.HEART_OF_THE_SEA)) {
                MessageUtils.fail(player, "island.command.heartOfTheSeaNeeded");
                return;
            }
            itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
            player.getInventory().setItemInOffHand(itemInOffHand);
            currentIsland.addIslandFlag("EnableHexBeaconJump");
            MessageUtils.success(player, "island.command.ok");
        }
    }

    @Subcommand("disableHexBeaconJump")
    public void disableHexBeaconJump(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (currentIsland.containsFlag("EnableHexBeaconJump")) {
            currentIsland.removeIslandFlag("EnableHexBeaconJump");
            currentIsland.addIslandFlag("DisableHexBeaconJump");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    @Subcommand("enableHexBeaconDamageResistance")
    public void enableHexBeaconDamageResistance(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (currentIsland.containsFlag("EnableHexBeaconDamageResistance")) {
            MessageUtils.fail(player, "island.command.alreadyEnabled");
            return;
        }
        if (currentIsland.containsFlag("DisableHexBeaconDamageResistance")) {
            currentIsland.removeIslandFlag("DisableHexBeaconDamageResistance");
            currentIsland.addIslandFlag("EnableHexBeaconDamageResistance");
            MessageUtils.success(player, "island.command.ok");
        } else {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (!itemInOffHand.getType().equals(Material.HEART_OF_THE_SEA)) {
                MessageUtils.fail(player, "island.command.heartOfTheSeaNeeded");
                return;
            }
            itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
            player.getInventory().setItemInOffHand(itemInOffHand);
            currentIsland.addIslandFlag("EnableHexBeaconDamageResistance");
            MessageUtils.success(player, "island.command.ok");
        }
    }

    @Subcommand("disableHexBeaconDamageResistance")
    public void disableHexBeaconDamageResistance(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (currentIsland.containsFlag("EnableHexBeaconDamageResistance")) {
            currentIsland.removeIslandFlag("EnableHexBeaconDamageResistance");
            currentIsland.addIslandFlag("DisableHexBeaconDamageResistance");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    @Subcommand("EnableHexBeaconRegeneration")
    public void enableHexBeaconRegeneration(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (currentIsland.containsFlag("EnableHexBeaconRegeneration")) {
            MessageUtils.fail(player, "island.command.alreadyEnabled");
            return;
        }
        if (currentIsland.containsFlag("DisableHexBeaconRegeneration")) {
            currentIsland.removeIslandFlag("DisableHexBeaconRegeneration");
            currentIsland.addIslandFlag("EnableHexBeaconRegeneration");
            MessageUtils.success(player, "island.command.ok");
        } else {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (!itemInOffHand.getType().equals(Material.HEART_OF_THE_SEA)) {
                MessageUtils.fail(player, "island.command.heartOfTheSeaNeeded");
                return;
            }
            itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
            player.getInventory().setItemInOffHand(itemInOffHand);
            currentIsland.addIslandFlag("EnableHexBeaconRegeneration");
            MessageUtils.success(player, "island.command.ok");
        }
    }

    @Subcommand("DisableHexBeaconRegeneration")
    public void disableHexBeaconRegeneration(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (currentIsland.containsFlag("EnableHexBeaconRegeneration")) {
            currentIsland.removeIslandFlag("EnableHexBeaconRegeneration");
            currentIsland.addIslandFlag("DisableHexBeaconRegeneration");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    @Subcommand("anitFire")
    public void antiFire(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (!currentIsland.containsFlag("AntiFire")) {
            if (currentIsland.containsFlag("DisableAntiFire")) {
                currentIsland.removeIslandFlag("DisableAntiFire");
                currentIsland.addIslandFlag("AntiFire");

            } else {
                ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
                if (!itemInOffHand.getType().equals(Material.HEART_OF_THE_SEA)) {
                    MessageUtils.fail(player, "island.command.heartOfTheSeaNeeded");
                    return;
                }
                itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
                player.getInventory().setItemInOffHand(itemInOffHand);
                currentIsland.addIslandFlag("AntiFire");
            }
            MessageUtils.success(player, "island.command.enabled");
        } else {
            currentIsland.addIslandFlag("DisableAntiFire");
            currentIsland.removeIslandFlag("AntiFire");
            MessageUtils.success(player, "island.command.disabled");
        }
    }


    @Subcommand("allowItemPickup")
    public void allowItemPickup(Player player, @Flags("owner") LocalIsland currentIsland) {

        if (!currentIsland.containsFlag("AllowItemPickup")) {
            currentIsland.addIslandFlag("AllowItemPickup");
            MessageUtils.success(player, "island.command.enabled");
        } else {
            currentIsland.removeIslandFlag("AllowItemPickup");
            MessageUtils.success(player, "island.command.disabled");
        }
    }

    @Subcommand("allowItemDrop")
    public void allowItemDrop(Player player, @Flags("owner") LocalIsland currentIsland) {
        if (!currentIsland.containsFlag("AllowItemDrop")) {
            currentIsland.addIslandFlag("AllowItemDrop");
            MessageUtils.success(player, "island.command.enabled");
        } else {
            currentIsland.removeIslandFlag("AllowItemDrop");
            MessageUtils.success(player, "island.command.disabled");
        }
    }



    @Subcommand("claimOffline")
    public void claimOffline(Player player, @Flags("owner") LocalIsland currentIsland,String password) {

        UUID uuid = currentIsland.getUuid();
        String name = UUIDManager.get(uuid);
        if (name == null || !name.startsWith("#")) {
            MessageUtils.fail(player, "island.claim.failed.notOffline");
            return;
        }
        if (!accountService.login(name, password)) {
            MessageUtils.fail(player, "island.claim.failed.passwordWrong");
            return;
        }
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        if (!itemInOffHand.getType().equals(Material.BEACON)) {
            MessageUtils.fail(player, "island.command.beaconNeeded");
            return;
        }
        itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
        player.getInventory().setItemInOffHand(itemInOffHand);
        currentIsland.setUuid(player.getUniqueId());
        MessageUtils.success(player, "island.command.ok");

    }

    @Subcommand("create")
    public void create(Player player) {

        String key = "IslandClaim-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        boolean freeClaimed = playerPropertyManager.getPropertyAsBoolean(player, key);

        if (!freeClaimed) {
            playerPropertyManager.setPropertyAsync(player, key, "true", () -> {
                PluginUtils.getLogger().info(player.getName() + " claimed a free island!");
                IslandManager.INSTANCE.createNewIsland(player.getUniqueId(), localIsland -> {
                    localIsland.tp(player);
                    MessageUtils.fail(player, "island.command.ok");
                });
            });
            return;
        }


        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        if (!itemInOffHand.getType().equals(Material.BEACON)) {
            MessageUtils.fail(player, "island.command.beaconNeeded");
            return;
        }
        itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
        player.getInventory().setItemInOffHand(itemInOffHand);

        PluginUtils.getLogger().info(player.getName() + " claimed a island by beacon!");
        IslandManager.INSTANCE.createNewIsland(player.getUniqueId(), localIsland -> {
            localIsland.tp(player);
            MessageUtils.fail(player, "island.command.ok");
        });
    }


    @Subcommand("spectatorVisitor")
    public void spectatorVisitor(Player player, @Flags("owner") LocalIsland currentIsland) {
        if (currentIsland.containsFlag("SpectatorVisitor")) {
            currentIsland.removeIslandFlag("SpectatorVisitor");
            MessageUtils.success(player, "island.command.enabled.");
        } else {
            currentIsland.addIslandFlag("SpectatorVisitor");
            MessageUtils.success(player, "island.command.disabled");
        }

    }

    @Subcommand("visitors")
    public void visitors(Player player, @Flags("owner") LocalIsland currentIsland) {
        new VisitorMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open();
    }

    @Subcommand("consume")
    public void consume(Player player, @Flags("owner") LocalIsland currentIsland) {
        new PlayerChargeMenu(chargeCommitter, playerPropertyManager, sidebarManager, player).open();
    }


    @Subcommand("preferred")
    public void preferred(Player player, @Flags("owner") LocalIsland currentIsland) {
        if (!currentIsland.containsFlag("Preferred")) {
            currentIsland.addIslandFlag("Preferred");
            MessageUtils.success(player, "island.command.ok");
        } else {
            currentIsland.removeIslandFlag("Preferred");
            MessageUtils.success(player, "island.command.ok");
        }
    }


    @Subcommand("info")
    public void info(Player player, LocalIsland currentIsland) {
        player.sendMessage("===========start===========");
        String localServerName = IsletopiaTweakersUtils.getLocalServerName();
        player.sendMessage("pos:" + localServerName + ":" + currentIsland.getX() + "," + currentIsland.getZ());
        player.sendMessage("owner:" + UUIDManager.get(currentIsland.getUuid()));
        List<UUID> members = new ArrayList<>(currentIsland.getMembers());
        if (members.isEmpty()) {
            player.sendMessage("member:" + "none");
        } else {
            player.sendMessage("member:");
            for (UUID uuid : members) {
                player.sendMessage(" - " + UUIDManager.get(uuid));
            }
        }
        Set<String> islandFlags = currentIsland.getIslandFlags();
        if (islandFlags.isEmpty()) {
            player.sendMessage("flag: none");
        } else {
            player.sendMessage("flag:");
            for (String islandFlag : islandFlags) {
                player.sendMessage(" - " + islandFlag);
            }
        }
        Timestamp creation = currentIsland.getCreation();
        LocalDateTime localDateTime = creation.toLocalDateTime();
        String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
        player.sendMessage("creation:" + format);
        player.sendMessage("===========end===========");
    }


    @Subcommand("name")
    public void name(Player player, @Flags("owner") LocalIsland currentIsland, String name) {
        currentIsland.setName(name);
        MessageUtils.success(player, "island.command.ok");
    }



    @Subcommand("home")
    public void home(Player player) {
        visit(player, player.getName());
    }


    @Subcommand("setIcon")
    public void setIcon(Player player, @Flags("owner") LocalIsland currentIsland) {
        {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (itemInOffHand.getType().isAir()) {
                MessageUtils.fail(player, "island.command.itemNeeded");
                return;
            }
        }
        ConfirmDialog confirmDialog = new ConfirmDialog(player, Component.text(MessageUtils.getMessage(player, "island.setIcon.rules")));
        confirmDialog.onConfirm(player1 -> {
            if (currentIsland == null || !(currentIsland.getUuid().equals(player1.getUniqueId()) || player1.isOp())) {
                MessageUtils.fail(player1, "island.command.noPerm");
                return;
            }
            ItemStack itemInOffHand = player1.getInventory().getItemInOffHand();
            Material type = itemInOffHand.getType();
            if (itemInOffHand.getType().isAir()) {
                MessageUtils.fail(player1, "island.command.itemNeeded");
                return;
            }
            itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
            player1.getInventory().setItemInOffHand(itemInOffHand);
            currentIsland.setIcon(type.name());
        });
        confirmDialog.open();

    }

    @Subcommand("setHome")
    public void setHome(Player player, @Flags("owner") LocalIsland currentIsland) {
        Location bottomLocation = currentIsland.getBottomLocation(player.getLocation().getWorld());
        currentIsland.setSpawnWorld(player.getLocation().getWorld().getName());
        currentIsland.setSpawnX(player.getLocation().getX() - bottomLocation.getX());
        currentIsland.setSpawnY(player.getLocation().getY());
        currentIsland.setSpawnZ(player.getLocation().getZ() - bottomLocation.getZ());
        currentIsland.setYaw(player.getLocation().getYaw());
        currentIsland.setPitch(player.getLocation().getPitch());
        MessageUtils.success(player, "island.command.ok");
    }

    @Subcommand("resetHome")
    public void resetHome(Player player, @Flags("owner") LocalIsland currentIsland) {
        currentIsland.setSpawnWorld("SkyWorld");
        currentIsland.setSpawnX(256);
        currentIsland.setSpawnZ(256);
        currentIsland.setSpawnY(128);
        currentIsland.setYaw(0);
        currentIsland.setPitch(0);
        MessageUtils.success(player, "island.command.ok");
    }

    @Subcommand("setBiome")
    public void setBiome(Player player) {
        new BiomeMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open();
    }

    @Subcommand("visit")
    public void visit(Player player, String target) {
        Tasks.INSTANCE.async(() -> {
            UUID targetUUID = UUIDManager.get(target);
            List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(targetUUID);
            if (playerIslands.size() == 0) {
                MessageUtils.fail(player, "island.visit.noIsland");
                return;
            }
            for (Island playerIsland : playerIslands) {
                if (playerIsland.containsFlag("Preferred")) {
                    IsletopiaTweakersUtils.universalPlotVisitByMessage(player, playerIsland.getIslandId());
                    return;
                }
            }
            IsletopiaTweakersUtils.universalPlotVisitByMessage(player, playerIslands.get(0).getIslandId());
        });

    }

    @Subcommand("trust")
    public void trust(Player player, @Flags("owner") LocalIsland currentIsland,String target) {
        UUID targetUUID = UUIDManager.get(target);
        if (targetUUID == null) {
            MessageUtils.success(player, "island.command.notReg");
            return;
        }
        if (currentIsland.getMembers().contains(targetUUID) || currentIsland.getUuid().equals(targetUUID)) {
            MessageUtils.success(player, "island.command.alreadyMember");
            return;
        }
        if (!target.matches("[a-zA-Z0-9_]{3,16}")) {
            MessageUtils.success(player, "island.command.invalidName");
            return;
        }

        int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(targetUUID);
        if (playerIslandCount == 0) {
            MessageUtils.success(player, "island.command.notReg");
            return;
        }

        ConfirmDialog confirmDialog = new ConfirmDialog(player, Component.text(MessageUtils.getMessage(player, "island.command.memberInfo")));
        confirmDialog.onConfirm(player1 -> {
            UUID uuid = UUIDManager.get(target);
            if (uuid == null) {
                MessageUtils.fail(player1, "island.command.notReg");
                return;
            }
            currentIsland.addMember(uuid);
            MessageUtils.success(player1, "island.command.ok");
        });
        confirmDialog.open();

    }

    @Subcommand("distrust")
    public void distrust(Player player, @Flags("owner") LocalIsland currentIsland,String target) {
        UUID uuid = UUIDManager.get(target);
        if (uuid == null) {
            MessageUtils.fail(player, "island.command.notReg");
            return;
        }
        if (currentIsland.getMembers().contains(UUIDManager.get(target))) {
            currentIsland.removeMember(uuid);
            MessageUtils.success(player, "island.command.ok");

        } else {
            MessageUtils.success(player, "island.command.failed");
        }
    }

    @Subcommand("lock")
    public void lock(Player player, @Flags("owner") LocalIsland currentIsland) {
        currentIsland.addIslandFlag("Lock");
        MessageUtils.success(player, "island.command.ok");
    }

    @Subcommand("unlock")
    public void unlock(Player player, @Flags("owner") LocalIsland currentIsland) {
        currentIsland.removeIslandFlag("Lock");
        MessageUtils.success(player, "island.command.ok");
    }


    @Subcommand("trusts")
    public void trusts(Player player, @Flags("owner") LocalIsland currentIsland) {
        List<UUID> members = new ArrayList<>(currentIsland.getMembers());
        if (members.isEmpty()) {
            MessageUtils.info(player, "island.command.empty");
        } else {
            MessageUtils.info(player, "island.command.memberTitle");
            for (UUID uuid : members) {
                MessageUtils.info(player, " - " + UUIDManager.get(uuid));
            }
        }
    }

    @Subcommand("visits")
    public void visits(Player player) {
        new VisitMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open();

    }


    private void cacheCollections(Player player) {
        ArrayList<String> strings = new ArrayList<>();
        for (UUID playerCollection : CollectionDao.getPlayerCollections(player.getUniqueId())) {
            strings.add(UUIDManager.get(playerCollection));
        }
        String collection = String.join(",", strings);
        if (collection.isEmpty()) {
            return;
        }
        redisService.getCommand().set("Collection-" + player.getName(), collection);
    }


    @Subcommand("star")
    public void star(Player player, String target) {
        UUID targetUUID = UUIDManager.get(target);
        int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(targetUUID);
        if (playerIslandCount == 0) {
            MessageUtils.fail(player, "island.command.notReg");
            return;
        }
        Set<UUID> collection = CollectionDao.getPlayerCollections(player.getUniqueId());
        if (collection.contains(targetUUID)) {

            MessageUtils.fail(player, "island.command.alreadySubscribe");
            return;
        }

        CollectionDao.addCollection(player.getUniqueId(), targetUUID);
        cacheCollections(player);
        MessageUtils.success(player, "island.command.ok");
    }

    @Subcommand("unstar")
    public void unstar(Player player, String target) {
        UUID targetUUID = UUIDManager.get(target);
        Set<UUID> collection = CollectionDao.getPlayerCollections(player.getUniqueId());
        if (!collection.contains(targetUUID)) {
            MessageUtils.fail(player, "island.command.failed");
            return;
        }

        CollectionDao.removeCollection(player.getUniqueId(), targetUUID);
        cacheCollections(player);
        MessageUtils.success(player, "island.command.ok");
    }

    @Subcommand("stats")
    public void stars(Player player) {
        Set<UUID> collection = CollectionDao.getPlayerCollections(player.getUniqueId());
        if (collection.isEmpty()) {
            MessageUtils.info(player, "island.command.empty");
        } else {
            MessageUtils.info(player, "island.command.subscribeTitle");
            for (UUID member : collection) {
                MessageUtils.info(player, " - " + UUIDManager.get(member));
            }
        }
        cacheCollections(player);
    }

    @Default
    public void help(Player player) {
        player.sendMessage("§7§m§l----------§bIsletopia§7§m§l----------");
        player.sendMessage("§e>  /is home");
        player.sendMessage("§e>  /is info");
        player.sendMessage("§e>  /is lock|unlock");
        player.sendMessage("§e>  /is setBiome");
        player.sendMessage("§e>  /is visits|visit (player)");
        player.sendMessage("§e>  /is visitors");
        player.sendMessage("§e>  /is trust|distrust|trusts (player)");
        player.sendMessage("§e>  /is consume");
        player.sendMessage("§e>  /is setHome|resetHome");
        player.sendMessage("§e>  /is star|unstar|stars (player)");
        player.sendMessage(Component.text(MessageUtils.getMessage(player, "island.command.wiki"))
                .clickEvent(ClickEvent.openUrl("http://wiki.islet.world")));
        player.sendMessage("§7§m§l--------------------------");

    }


}
