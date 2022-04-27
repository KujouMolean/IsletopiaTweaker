package com.molean.isletopia.island;

import com.molean.isletopia.dialog.ConfirmDialog;
import com.molean.isletopia.menu.charge.PlayerChargeMenu;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.menu.visit.VisitMenu;
import com.molean.isletopia.menu.visit.VisitorMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.shared.database.UUIDDao;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.service.AccountService;
import com.molean.isletopia.shared.utils.RedisUtils;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class IslandCommand implements CommandExecutor, TabCompleter {
    public IslandCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("is")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("is")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("island")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("island")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("islet")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("islet")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("isletopia")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("isletopia")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Tasks.INSTANCE.async(() -> {
            Player sourcePlayer = (Player) sender;
            String subject = sourcePlayer.getName();
            String verb;
            String object = null;

            if (args.length == 0) {
                home(subject);
                return;
            }

            verb = args[0].toLowerCase();

            if (args.length > 1) {
                object = args[1];
            }
            switch (verb) {
                case "home":
                    home(subject);
                    break;
                case "preferred":
                    preferred(subject);
                    break;
                case "create":
                    create(subject);
                    break;
                case "visits":
                    visits(subject);
                    break;
                case "name":
                    if (args.length < 2) {
                        name(subject);
                    } else {
                        name(subject, object);
                    }
                    break;

                case "priority":
                    if (args.length < 2) {
                        priority(subject);
                    } else {
                        priority(subject, object);
                    }
                    break;
                case "visit":
                case "tp":
                    if (args.length < 2) {
                        help(subject);
                        return;
                    }
                    visit(subject, object);
                    break;
                case "trust":
                case "invite":
                    if (args.length < 2) {
                        help(subject);
                        return;
                    }
                    trust(subject, object);
                    break;
                case "kick":
                case "distrust":
                    if (args.length < 2) {
                        help(subject);
                        return;
                    }
                    distrust(subject, object);
                    break;
                case "lock":
                    lock(subject);
                    break;
                case "unlock":
                case "open":
                    unlock(subject);
                    break;
                case "spectatorvisitor":
                    spectatorVisitor(subject);
                    break;
                case "seticon":
                    setIcon(subject);
                    break;
                case "sethome":
                    setHome(subject);
                    break;
                case "resethome":
                    resetHome(subject);
                    break;
                case "setbiome":
                    setBiome(subject);
                    break;
                case "claimoffline":
                    if (args.length < 2) {
                        MessageUtils.info(sourcePlayer, "/is claimOffline [password]");
                        return;
                    }
                    claimOffline(subject, object);
                    break;
                case "info":
                    info(subject);
                    break;
                case "consume":
                    consume(subject);
                    break;
                case "trusts":
                    trusts(subject);
                    break;
                case "visitors":
                    visitors(subject);
                    break;
                case "stars":
                    stars(subject);
                    break;
                case "star":
                    if (args.length < 2) {
                        help(subject);
                        return;
                    }
                    star(subject, object);
                    break;
                case "unstar":
                    if (args.length < 2) {
                        help(subject);
                        return;
                    }
                    unstar(subject, object);
                    break;
                case "allowitempickup":
                    allowItemPickup(subject);
                    break;
                case "allowitemdrop":
                    allowItemDrop(subject);
                    break;
                case "antifire":
                    antiFire(subject);
                case "enablehexbeaconspeed":
                    enableHexBeaconSpeed(subject);
                    break;
                case "enablehexbeaconfastdigging":
                    enableHexBeaconFastDigging(subject);
                    break;
                case "enablehexbeaconincreasedamage":
                    enableHexBeaconIncreaseDamage(subject);
                    break;
                case "enablehexbeaconjump":
                    enableHexBeaconJump(subject);
                    break;
                case "enablehexbeacondamageresistance":
                    enableHexBeaconDamageResistance(subject);
                    break;
                case "enablehexbeaconregeneration":
                    enableHexBeaconRegeneration(subject);
                    break;
                case "disablehexbeaconspeed":
                    disableHexBeaconSpeed(subject);
                    break;
                case "disablehexbeaconfastdigging":
                    disableHexBeaconFastDigging(subject);
                    break;
                case "disablehexbeaconincreasedamage":
                    disableHexBeaconIncreaseDamage(subject);
                    break;
                case "disablehexbeaconjump":
                    disableHexBeaconJump(subject);
                    break;
                case "disablehexbeacondamageresistance":
                    disableHexBeaconDamageResistance(subject);
                    break;
                case "disablehexbeaconregeneration":
                    disableHexBeaconRegeneration(subject);
                    break;
                default:
                case "help":
                    help(subject);
                    break;

            }
        });
        return true;
    }

    private void priority(String subject, String object) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        int i;
        try {
            i = Integer.parseInt(object);
        } catch (NumberFormatException e) {
            MessageUtils.fail(player, object + "不是有效数字!");
            return;
        }
        currentIsland.removeIslandFlag("Priority");
        currentIsland.addIslandFlag("Priority#" + i);
        if (i == 0) {
            currentIsland.removeIslandFlag("Priority");
        }
        MessageUtils.success(player, "island.command.ok");
    }

    private void priority(String subject) {
        priority(subject, "0");
    }

    private void enableHexBeaconSpeed(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
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

    private void disableHexBeaconSpeed(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        if (currentIsland.containsFlag("EnableHexBeaconSpeed")) {
            currentIsland.removeIslandFlag("EnableHexBeaconSpeed");
            currentIsland.addIslandFlag("DisableHexBeaconSpeed");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    private void enableHexBeaconFastDigging(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
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

    private void disableHexBeaconFastDigging(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        if (currentIsland.containsFlag("EnableHexBeaconFastDigging")) {
            currentIsland.removeIslandFlag("EnableHexBeaconFastDigging");
            currentIsland.addIslandFlag("DisableHexBeaconFastDigging");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    private void enableHexBeaconIncreaseDamage(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
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

    private void disableHexBeaconIncreaseDamage(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }

        if (currentIsland.containsFlag("EnableHexBeaconIncreaseDamage")) {
            currentIsland.removeIslandFlag("EnableHexBeaconIncreaseDamage");
            currentIsland.addIslandFlag("DisableHexBeaconIncreaseDamage");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    private void enableHexBeaconJump(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
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

    private void disableHexBeaconJump(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        if (currentIsland.containsFlag("EnableHexBeaconJump")) {
            currentIsland.removeIslandFlag("EnableHexBeaconJump");
            currentIsland.addIslandFlag("DisableHexBeaconJump");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    private void enableHexBeaconDamageResistance(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
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

    private void disableHexBeaconDamageResistance(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        if (currentIsland.containsFlag("EnableHexBeaconDamageResistance")) {
            currentIsland.removeIslandFlag("EnableHexBeaconDamageResistance");
            currentIsland.addIslandFlag("DisableHexBeaconDamageResistance");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    private void enableHexBeaconRegeneration(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
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

    private void disableHexBeaconRegeneration(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        if (currentIsland.containsFlag("EnableHexBeaconRegeneration")) {
            currentIsland.removeIslandFlag("EnableHexBeaconRegeneration");
            currentIsland.addIslandFlag("DisableHexBeaconRegeneration");
            MessageUtils.success(player, "island.command.ok");
        } else {
            MessageUtils.fail(player, "island.command.failed");
        }
    }

    private static final List<String> subCommand = List.of("home", "visit", "trust", "distrust", "help", "invite",
            "kick", "lock", "unlock", "setHome", "resetHome",
            "visits", "trusts", "visitors", "consume", "stars", "star", "unstar", "spectatorVisitor", "setBiome",
            "setIcon", "name", "preferred", "create", "claimOffline",
            "allowFirework", "allowItemPickup", "allowItemDrop", "priority",
            "enableHexBeaconSpeed", "enableHexBeaconFastDigging", "enableHexBeaconIncreaseDamage",
            "enableHexBeaconJump", "enableHexBeaconDamageResistance", "enableHexBeaconRegeneration",
            "disableHexBeaconSpeed", "disableHexBeaconFastDigging", "disableHexBeaconIncreaseDamage",
            "disableHexBeaconJump", "disableHexBeaconDamageResistance", "disableHexBeaconRegeneration");

    private static final List<String> playerCommand = List.of("trust", "distrust",
            "kick", "invite", "visit", "star", "unstar");

    private void antiFire(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
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


    private void allowItemPickup(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        if (!currentIsland.containsFlag("AllowItemPickup")) {
            currentIsland.addIslandFlag("AllowItemPickup");
            MessageUtils.success(player, "island.command.enabled");
        } else {
            currentIsland.removeIslandFlag("AllowItemPickup");
            MessageUtils.success(player, "island.command.disabled");
        }
    }

    private void allowItemDrop(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;


        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        if (!currentIsland.containsFlag("AllowItemDrop")) {
            currentIsland.addIslandFlag("AllowItemDrop");
            MessageUtils.success(player, "island.command.enabled");
        } else {
            currentIsland.removeIslandFlag("AllowItemDrop");
            MessageUtils.success(player, "island.command.disabled");
        }
    }

    private void claimOffline(String subject, String password) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            MessageUtils.fail(player, "island.claim.failed.empty");
            return;
        }
        UUID uuid = currentIsland.getUuid();
        String name = UUIDManager.get(uuid);
        if (name == null || !name.startsWith("#")) {
            MessageUtils.fail(player, "island.claim.failed.notOffline");
            return;
        }
        if (!AccountService.login(name, password)) {
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

    private void create(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;


        String key = "IslandClaim-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        boolean freeClaimed = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, key);

        if (!freeClaimed) {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, key, "true", () -> {
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


    private void spectatorVisitor(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }

        if (currentIsland.containsFlag("SpectatorVisitor")) {
            currentIsland.removeIslandFlag("SpectatorVisitor");
            MessageUtils.success(player, "island.command.enabled.");
        } else {
            currentIsland.addIslandFlag("SpectatorVisitor");
            MessageUtils.success(player, "island.command.disabled");
        }

    }

    public static void visitors(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        new VisitorMenu(player).open();
    }

    public static void consume(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        new PlayerChargeMenu(player).open();
    }


    public static void preferred(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        if (!currentIsland.containsFlag("Preferred")) {
            currentIsland.addIslandFlag("Preferred");
            MessageUtils.success(player, "island.command.ok");
        } else {
            currentIsland.removeIslandFlag("Preferred");
            MessageUtils.success(player, "island.command.ok");
        }
    }

    public static void info(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            player.sendMessage("not claimed");
            return;
        }
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


    private static void name(String subject) {
        name(subject, null);
    }

    private static void name(String subject, String object) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        currentIsland.setName(object);
        MessageUtils.success(player, "island.command.ok");
    }


    public static void home(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        visit(source, source);
    }


    public static void setIcon(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (itemInOffHand.getType().isAir()) {
                MessageUtils.fail(player, "island.command.itemNeeded");
                return;
            }
        }
        new ConfirmDialog(Component.text(MessageUtils.getMessage(player, "island.setIcon.rules"))).accept(player1 -> {
            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player1);

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
        }).open(player);

    }

    public static void setHome(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }

        Location bottomLocation = currentIsland.getBottomLocation();
        currentIsland.setSpawnX(player.getLocation().getX() - bottomLocation.getX());
        currentIsland.setSpawnY(player.getLocation().getY() - bottomLocation.getY());
        currentIsland.setSpawnZ(player.getLocation().getZ() - bottomLocation.getZ());
        currentIsland.setYaw(player.getLocation().getYaw());
        currentIsland.setPitch(player.getLocation().getPitch());
        MessageUtils.success(player, "island.command.ok");
    }

    public static void resetHome(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }

        currentIsland.setSpawnX(256);
        currentIsland.setSpawnZ(256);
        currentIsland.setSpawnY(128);
        currentIsland.setYaw(0);
        currentIsland.setPitch(0);
        MessageUtils.success(player, "island.command.ok");
    }

    public static void setBiome(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }

        new BiomeMenu(player).open();
    }

    private static void visit(String source, String target) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
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
    }

    public static void trust(String source, String target) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        UUID targetUUID = UUIDManager.get(target);
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        if (targetUUID == null) {
            MessageUtils.success(player, "island.command.notReg");
            return;
        }
        if (currentIsland.getMembers().contains(targetUUID) || currentIsland.getUuid().equals(targetUUID)) {
            MessageUtils.success(player, "island.command.alreadyMember");
            return;
        }
        if (!target.matches("[#a-zA-Z0-9_]{3,16}")) {
            MessageUtils.success(player, "island.command.invalidName");
            return;
        }

        int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(targetUUID);
        if (playerIslandCount == 0) {
            MessageUtils.success(player, "island.command.notReg");
            return;
        }

        new ConfirmDialog(Component.text(MessageUtils.getMessage(player, "island.command.memberInfo"))).accept(player1 -> {
            UUID uuid = UUIDManager.get(target);
            if (uuid == null) {
                MessageUtils.fail(player1, "island.command.notReg");
                return;
            }
            currentIsland.addMember(uuid);
            MessageUtils.success(player1, "island.command.ok");
        }).open(player);

    }

    public static void distrust(String source, String target) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
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

    public static void lock(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        currentIsland.addIslandFlag("Lock");
        MessageUtils.success(player, "island.command.ok");
    }

    public static void unlock(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
        currentIsland.removeIslandFlag("Lock");
        MessageUtils.success(player, "island.command.ok");
    }


    public static void trusts(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "island.command.noPerm");
            return;
        }
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

    public static void visits(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        new VisitMenu(player).open();
    }

    public static void cacheCollections(Player player) {
        ArrayList<String> strings = new ArrayList<>();
        for (UUID playerCollection : CollectionDao.getPlayerCollections(player.getUniqueId())) {
            strings.add(UUIDManager.get(playerCollection));
        }
        String collection = String.join(",", strings);
        if (collection.isEmpty()) {
            return;
        }
        RedisUtils.asyncSet("Collection-" + player.getName(), collection);
    }


    public static void star(String source, String target) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        //check player exist
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

    public static void unstar(String source, String target) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
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

    public static void stars(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
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

    public void help(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
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


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String
            alias, String[] args) {
        List<String> strings = new ArrayList<>();
        if (args.length == 1) {
            for (String s : subCommand) {
                if (s.startsWith(args[0])) {
                    strings.add(s);
                }
            }
        } else if (args.length == 2) {
            if (playerCommand.contains(args[0])) {
                List<String> onlinePlayers = new ArrayList<>(UUIDManager.INSTANCE.getSnapshot().values());
                for (String onlinePlayer : onlinePlayers) {
                    if (onlinePlayer.toLowerCase().startsWith(args[1].toLowerCase())) {
                        strings.add(onlinePlayer);
                    }
                }
            }
        }
        return strings;
    }
}
