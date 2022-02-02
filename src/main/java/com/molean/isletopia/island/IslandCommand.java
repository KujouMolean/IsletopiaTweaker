package com.molean.isletopia.island;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.VisitorMenu;
import com.molean.isletopia.menu.charge.PlayerChargeMenu;
import com.molean.isletopia.menu.settings.biome.BiomeMenu;
import com.molean.isletopia.menu.visit.VisitMenu;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.other.ConfirmDialog;
import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.service.AccountService;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.MessageUtils;
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
        Player sourcePlayer = (Player) sender;
        String subject = sourcePlayer.getName();
        String verb;
        String object = null;

        if (args.length == 0) {
            home(subject);
            return true;
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
            case "visit":
            case "tp":
                if (args.length < 2) {
                    help(subject);
                    return true;
                }
                visit(subject, object);
                break;
            case "trust":
            case "invite":
                if (args.length < 2) {
                    help(subject);
                    return true;
                }
                trust(subject, object);
                break;
            case "kick":
            case "distrust":
                if (args.length < 2) {
                    help(subject);
                    return true;
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
                    MessageUtils.info(sourcePlayer, "/is claimOffline [密码]");
                    return true;
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
                    return true;
                }
                star(subject, object);
                break;
            case "unstar":
                if (args.length < 2) {
                    help(subject);
                    return true;
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
                break;
            default:
            case "help":
                help(subject);
                break;

        }
        return true;
    }

    private void antiFire(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        if (!currentIsland.containsFlag("AntiFire")) {
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
            if (!itemInOffHand.getType().equals(Material.HEART_OF_THE_SEA)) {
                MessageUtils.fail(player, "副手需要放一个海洋之心，该物品会被消耗掉.");
                return;
            }
            itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
            player.getInventory().setItemInOffHand(itemInOffHand);
            currentIsland.addIslandFlag("AntiFire");
            MessageUtils.success(player, "已开启岛屿防火.");
        } else {
            currentIsland.removeIslandFlag("AntiFire");
            MessageUtils.success(player, "已关闭岛屿防火.");
        }
    }


    private void allowItemPickup(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        if (!currentIsland.containsFlag("AllowItemPickup")) {
            currentIsland.addIslandFlag("AllowItemPickup");
            MessageUtils.success(player, "已允许游客拾起物品.");
        } else {
            currentIsland.removeIslandFlag("AllowItemPickup");
            MessageUtils.success(player, "已禁止游客拾起物品.");
        }
    }

    private void allowItemDrop(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        if (!currentIsland.containsFlag("AllowItemDrop")) {
            currentIsland.addIslandFlag("AllowItemDrop");
            MessageUtils.success(player, "已允许游客丢弃物品.");
        } else {
            currentIsland.removeIslandFlag("AllowItemDrop");
            MessageUtils.success(player, "已禁止游客丢弃物品.");
        }
    }

    private void claimOffline(String subject, String password) {

        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            MessageUtils.fail(player, "该岛屿未被领取.");
            return;
        }
        UUID uuid = currentIsland.getUuid();
        String name = UUIDUtils.get(uuid);
        if (name == null || !name.startsWith("#")) {
            MessageUtils.fail(player, "该岛主不是离线玩家，不能被领取.");
            return;
        }
        if (!AccountService.login(name, password)) {
            MessageUtils.fail(player, "密码不对.");
            return;
        }
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        if (!itemInOffHand.getType().equals(Material.BEACON)) {
            MessageUtils.fail(player, "副手需要放一个信标，该信标会被消耗掉.");
            return;
        }
        itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
        player.getInventory().setItemInOffHand(itemInOffHand);
        currentIsland.setUuid(player.getUniqueId());
        MessageUtils.fail(player, "成功领取岛屿.");

    }

    private void create(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        if (!itemInOffHand.getType().equals(Material.BEACON)) {
            MessageUtils.fail(player, "副手需要放一个信标，该信标会被消耗掉.");
            return;
        }
        itemInOffHand.setAmount(itemInOffHand.getAmount() - 1);
        player.getInventory().setItemInOffHand(itemInOffHand);


        IslandManager.INSTANCE.createNewIsland(player.getUniqueId(), localIsland -> {
            localIsland.tp(player);
            MessageUtils.fail(player, "创建成功.");
        });
    }


    private void spectatorVisitor(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }

        if (currentIsland.containsFlag("SpectatorVisitor")) {
            currentIsland.removeIslandFlag("SpectatorVisitor");
            MessageUtils.success(player, "已取消访客旁观模式.");
        } else {
            currentIsland.addIslandFlag("SpectatorVisitor");
            MessageUtils.success(player, "已设置访客旁观模式.");
        }

    }

    public static void visitors(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            new VisitorMenu(player).open();
        });
    }

    public static void consume(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            new PlayerChargeMenu(player).open();
        });
    }


    public static void preferred(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        if (!currentIsland.containsFlag("Preferred")) {
            currentIsland.addIslandFlag("Preferred");
            MessageUtils.success(player, "成功设置为首选岛屿.");
        } else {
            currentIsland.removeIslandFlag("Preferred");
            MessageUtils.success(player, "成功取消首选岛屿设置.");
        }
    }

    public static void info(String subject) {
        Player player = Bukkit.getPlayerExact(subject);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            player.sendMessage("当前岛屿尚未被领取");
            return;
        }
        player.sendMessage("===========start===========");
        String localServerName = IsletopiaTweakersUtils.getLocalServerName();
        player.sendMessage("岛屿坐标:" + localServerName + ":" + currentIsland.getX() + "," + currentIsland.getZ());
        player.sendMessage("岛屿主人:" + UUIDUtils.get(currentIsland.getUuid()));
        List<UUID> members = new ArrayList<>(currentIsland.getMembers());
        if (members.isEmpty()) {
            player.sendMessage("岛屿成员:" + "无");
        } else {
            player.sendMessage("岛屿成员:");
            for (UUID uuid : members) {
                player.sendMessage(" - " + UUIDUtils.get(uuid));
            }
        }
        Set<String> islandFlags = currentIsland.getIslandFlags();
        if (islandFlags.isEmpty()) {
            player.sendMessage("岛屿标记:无");
        } else {
            player.sendMessage("岛屿标记:");
            for (String islandFlag : islandFlags) {
                player.sendMessage(" - " + islandFlag);
            }
        }
        Timestamp creation = currentIsland.getCreation();
        LocalDateTime localDateTime = creation.toLocalDateTime();
        String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
        player.sendMessage("创建时间:" + format);
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
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        currentIsland.setName(object);
        MessageUtils.success(player, "设置成功.");
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
                MessageUtils.fail(player, "副手需要持有一个物品");
                return;
            }
        }
        new ConfirmDialog(Component.text("""
                设置图标需要献祭你副手的这个物品，直接消耗掉。
                """)).accept(player1 -> {
            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player1);

            if (currentIsland == null || !(currentIsland.getUuid().equals(player1.getUniqueId()) || player1.isOp())) {
                MessageUtils.fail(player1, "阁下只能对自己的岛屿进行设置.");
                return;
            }
            ItemStack itemInOffHand = player1.getInventory().getItemInOffHand();
            Material type = itemInOffHand.getType();
            if (itemInOffHand.getType().isAir()) {
                MessageUtils.fail(player1, "副手需要持有一个物品");
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
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }

        Location bottomLocation = currentIsland.getBottomLocation();
        currentIsland.setSpawnX(player.getLocation().getX() - bottomLocation.getX());
        currentIsland.setSpawnY(player.getLocation().getY() - bottomLocation.getY());
        currentIsland.setSpawnZ(player.getLocation().getZ() - bottomLocation.getZ());
        currentIsland.setYaw(player.getLocation().getYaw());
        currentIsland.setPitch(player.getLocation().getPitch());
        MessageUtils.success(player, "成功更改重生位置.");
    }

    public static void resetHome(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;

        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);

        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }

        currentIsland.setSpawnX(256);
        currentIsland.setSpawnZ(256);
        currentIsland.setSpawnY(128);
        currentIsland.setYaw(0);
        currentIsland.setPitch(0);
        MessageUtils.success(player, "成功更改重生位置.");
    }

    public static void setBiome(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(),
                () -> new BiomeMenu(player).open());
    }

    private static void visit(String source, String target) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        UUID targetUUID = UUIDUtils.get(target);
        List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(targetUUID);
        if (playerIslands.size() == 0) {
            MessageUtils.fail(player, "对方没有岛屿。");
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
        UUID targetUUID = UUIDUtils.get(target);
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        if (targetUUID == null) {
            MessageUtils.success(player, "无效, 对方未在梦幻之屿注册.");
            return;
        }
        if (currentIsland.getMembers().contains(targetUUID) || currentIsland.getUuid().equals(targetUUID)) {
            MessageUtils.success(player, "无效,对方已经在你的信任列表中.");
            return;
        }
        if (!target.matches("[#a-zA-Z0-9_]{3,16}")) {
            MessageUtils.success(player, "无效,该用户名不合法.");
            return;
        }

        int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(targetUUID);
        if (playerIslandCount == 0) {
            MessageUtils.success(player, "无效, 对方未在梦幻之屿注册.");
            return;
        }

        new ConfirmDialog(Component.text("""
                添加岛屿成员后，你的岛员将能够随意破坏你的岛屿。
                请不要随意乱加岛员，如果因为乱给权限导致岛屿被破坏，所有后果自行承担，服务器将不给予任何帮助。
                """)).accept(player1 -> {
            UUID uuid = UUIDUtils.get(target);
            if (uuid == null) {
                MessageUtils.fail(player1, "添加失败，对方从未加入梦幻之屿.");
                return;
            }
            currentIsland.addMember(uuid);
            MessageUtils.success(player1, "已经添加 " + target + " 为信任.");
        }).open(player);

    }

    public static void distrust(String source, String target) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        UUID uuid = UUIDUtils.get(target);
        if (uuid == null) {
            MessageUtils.fail(player, "失败，数据库找不到该用户信息.");
            return;
        }
        if (currentIsland.getMembers().contains(UUIDUtils.get(target))) {
            currentIsland.removeMember(uuid);
            MessageUtils.success(player, "已经取消对 " + target + " 的信任.");

        } else {
            MessageUtils.success(player, "你没有添加 " + target + " 为信任.");
        }
    }

    public static void lock(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        currentIsland.addIslandFlag("Lock");
        MessageUtils.success(player, "已将岛屿设置为§c锁定§6, 非成员玩家将无法访问.");
    }

    public static void unlock(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行设置.");
            return;
        }
        currentIsland.removeIslandFlag("Lock");
        MessageUtils.success(player, "已将岛屿设置为§c开放§6, 所有玩家都可以访问.");
    }


    public static void trusts(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null || !(currentIsland.getUuid().equals(player.getUniqueId()) || player.isOp())) {
            MessageUtils.fail(player, "阁下只能对自己的岛屿进行操作.");
            return;
        }
        List<UUID> members = new ArrayList<>(currentIsland.getMembers());

        if (members.isEmpty()) {
            MessageUtils.info(player, "你的岛屿没有成员");
        } else {
            MessageUtils.info(player, "你的岛屿成员列表如下:");
            for (UUID uuid : members) {
                MessageUtils.info(player, " - " + UUIDUtils.get(uuid));
            }
        }
    }

    public static void visits(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            new VisitMenu(player).open();
        });
    }

    public static void cacheCollections(Player player) {
        ArrayList<String> strings = new ArrayList<>();
        for (UUID playerCollection : CollectionDao.getPlayerCollections(player.getUniqueId())) {
            strings.add(UUIDUtils.get(playerCollection));
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
        UUID targetUUID = UUIDUtils.get(target);
        int playerIslandCount = IslandManager.INSTANCE.getPlayerIslandCount(targetUUID);
        if (playerIslandCount == 0) {
            MessageUtils.fail(player, "失败, " + target + " 未在梦幻之屿注册!");
            return;
        }
        Set<UUID> collection = CollectionDao.getPlayerCollections(player.getUniqueId());
        if (collection.contains(targetUUID)) {

            MessageUtils.fail(player, "失败, " + target + " 已在你的收藏列表中了!");
            return;
        }

        CollectionDao.addCollection(player.getUniqueId(), targetUUID);
        cacheCollections(player);
        MessageUtils.success(player, "成功, " + target + " 已添加到你收藏列表!");
    }

    public static void unstar(String source, String target) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        UUID targetUUID = UUIDUtils.get(target);
        Set<UUID> collection = CollectionDao.getPlayerCollections(player.getUniqueId());
        if (!collection.contains(targetUUID)) {
            MessageUtils.fail(player, "失败, " + target + " 不在你的收藏列表中!");
            return;
        }

        CollectionDao.removeCollection(player.getUniqueId(), targetUUID);
        cacheCollections(player);
        MessageUtils.success(player, "成功, " + target + " 已从你的收藏列表中删除!");
    }

    public static void stars(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        Set<UUID> collection = CollectionDao.getPlayerCollections(player.getUniqueId());
        if (collection.isEmpty()) {
            MessageUtils.info(player, "你的收藏夹是空的!");
        } else {
            MessageUtils.info(player, "你的收藏夹里有这些玩家:");
            for (UUID member : collection) {
                MessageUtils.info(player, " - " + UUIDUtils.get(member));
            }
        }
        cacheCollections(player);
    }

    public void help(String source) {
        Player player = Bukkit.getPlayerExact(source);
        assert player != null;
        player.sendMessage("§7§m§l----------§b梦幻之屿§7§m§l----------");
        player.sendMessage("§e> 快速回家 /is home");
        player.sendMessage("§e> 查看岛屿信息 /is info");
        player.sendMessage("§e> 修改锁岛状态 /is lock|unlock");
        player.sendMessage("§e> 修改生物群系 /is setBiome");
        player.sendMessage("§e> 打开访问菜单 /is visits|visit (玩家)");
        player.sendMessage("§e> 查看近期访客 /is visitors");
        player.sendMessage("§e> 添加删除成员 /is trust|distrust|trusts (玩家)");
        player.sendMessage("§e> 缴纳水电费用 /is consume");
        player.sendMessage("§e> 修改复活位置 /is setHome|resetHome");
        player.sendMessage("§e> 收藏岛屿列表 /is star|unstar|stars (玩家)");
        player.sendMessage(Component.text("§e§n点击此处查看梦幻之屿服务器指南(WiKi)")
                .clickEvent(ClickEvent.openUrl("http://wiki.islet.world")));
        player.sendMessage("§7§m§l--------------------------");

    }

    private static final List<String> subCommand = List.of("home", "visit", "trust", "distrust", "help", "invite",
            "kick", "lock", "unlock", "setHome", "resetHome",
            "visits", "trusts", "visitors", "consume", "stars", "star", "unstar", "spectatorVisitor", "setBiome",
            "setIcon", "name", "preferred", "create", "claimOffline",
            "allowFirework","allowItemPickup","allowItemDrop");

    private static final List<String> playerCommand = List.of("trust", "distrust",
            "kick", "invite", "visit", "star", "unstar");

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
                List<String> onlinePlayers = ServerInfoUpdater.getOnlinePlayers();
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
