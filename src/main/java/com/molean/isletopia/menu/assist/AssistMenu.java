package com.molean.isletopia.menu.assist;

import com.molean.isletopia.infrastructure.individual.bars.SidebarManager;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AssistMenu extends ChestMenu {
    public AssistMenu(Player player) {
        super(player, 3, Component.text("辅助功能"));

        //entity bar

        String sidebar = SidebarManager.INSTANCE.getSidebar(player.getUniqueId());

        ItemStackSheet entityBar = ItemStackSheet.fromString(Material.EGG, """
                §f实体统计计分板
                §7在屏幕右侧显示当前岛屿的生物统计
                §7该统计每秒刷新一次，精确统计。
                §7当前状态：%s
                """.formatted("EntityBar".equalsIgnoreCase(sidebar) ? "开启" : "关闭"));
        this.item(0, entityBar.build(), () -> {
            player.performCommand("EntityBar");
            MessageUtils.success(player,"搞定!");
            close();
        });
        ItemStackSheet productionBar = ItemStackSheet.fromString(Material.BOOK, """
                §f产量统计计分板
                §7在屏幕右侧显示当前岛屿的掉落物品产量
                §7该统计会记录最近1分钟的掉落物品
                §7当前状态：%s
                """.formatted("ProductionBar".equalsIgnoreCase(sidebar) ? "开启" : "关闭"));
        this.item(1, productionBar.build(), () -> {
            player.performCommand("ProductionBar");
            MessageUtils.success(player,"搞定!");
            close();
        });
        ItemStackSheet inbox = ItemStackSheet.fromString(Material.CHEST, """
                §f邮箱
                §7系统奖励可能会发放到邮箱
                """);
        this.itemWithAsyncClickEvent(2, inbox.build(), () -> new InboxMenu(player).open());


//        ItemStackSheet mspt = ItemStackSheet.fromString(Material.CHEST, """
//                §f服务器MSPT条
//                §7在顶部显示服务器MSPT状态条
//                §7当前：关闭
//                """);
//        this.item(1, productionBar.build(),() -> {
//            player.performCommand("ProductionBar");
//            close();
//        });

        ItemStackSheet create = ItemStackSheet.fromString(Material.GRASS_BLOCK, """
                §f创建新岛屿
                §7消耗一个信标，即可创建一个新岛屿。
                §7请将信标放置在副手。
                """);
        this.item(3, create.build(), () -> {
            player.performCommand("is create");
            close();
        });

        ItemStackSheet claim = ItemStackSheet.fromString(Material.STONE, """
                §f领取离线小号
                §7站在你的离线小号岛屿上，输入/is claimOffline [密码]
                §7副手持一个信标，消耗该信标即可领取此岛屿。
                """);

        this.item(4, claim.build(), () -> {
            MessageUtils.info(player, "手动使用: /is claimOffline [密码]");
            close();
        });



        boolean disablePlayerRide = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisablePlayerRide");
        boolean disableRailWay = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableRailWay");
        boolean disableIronElevator = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableIronElevator");
        boolean disableChairs = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableChairs");
        boolean disableLavaProtect = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableLavaProtect");
        boolean disableSingleIslandMenu = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisableSingleIslandMenu");
        boolean disablePlayerMob = PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(player, "DisablePlayerMob");

        //DisableChairs
        ItemStackSheet sit = ItemStackSheet.fromString(Material.BRICK_STAIRS, """
                §f随地坐下
                §7启用后空手看地板长按Shift1秒并松开可以坐下
                §7当前: %s
                """.formatted(disableChairs ? "禁用" : "开启"));
        this.item(5, sit.build(), () -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableChairs", (!disableChairs) + "");
            close();
        });

        //DisableRailWay
        ItemStackSheet rail = ItemStackSheet.fromString(Material.RAIL, """
                §f铁轨传送
                §7在铁轨上按下Shift传送到另一端
                §7当前: %s
                """.formatted(disableRailWay ? "禁用" : "开启"));

        this.item(6, rail.build(), () -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableRailWay", (!disableRailWay) + "");
            MessageUtils.success(player,"搞定!");
            close();
        });

        //DisableIronElevator
        ItemStackSheet elevator = ItemStackSheet.fromString(Material.IRON_BLOCK, """
                §f铁块电梯
                §7在铁块上按下Shift/Space传送到另一个铁块
                §7当前: %s
                """.formatted(disableIronElevator ? "禁用" : "开启"));

        this.item(7, elevator.build(), () -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableIronElevator", (!disableIronElevator) + "");
            MessageUtils.success(player,"搞定!");
            close();
        });

        //DisablePlayerRide
        ItemStackSheet ride = ItemStackSheet.fromString(Material.LEAD, """
                §f抱起生物
                §7按下Shift+右键举起生物
                §7当前: %s
                """.formatted(disablePlayerRide ? "禁用" : "开启"));
        this.item(8, ride.build(), () -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisablePlayerRide", (!disablePlayerRide) + "");
            MessageUtils.success(player,"搞定!");
            close();
        });


        //DisableLavaProtect
        ItemStackSheet lava = ItemStackSheet.fromString(Material.LAVA_BUCKET, """
                §f岩浆保护
                §7阻止使用方块填充岩浆防止误填岩浆
                §7并且可以使用空桶将黑曜石还原岩浆
                §7当前：%s
                """.formatted(disableLavaProtect ? "禁用" : "开启"));

        this.item(9, lava.build(), () -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableLavaProtect", (!disableLavaProtect) + "");
            MessageUtils.success(player,"搞定!");
            close();
        });

        ItemStackSheet save = ItemStackSheet.fromString(Material.BRICKS, """
                §f下载存档
                §7下载你当前岛屿的存档
                """);
        this.item(10, save.build(), () -> {
            player.performCommand("download");
            close();
        });

        ItemStackSheet skip = ItemStackSheet.fromString(Material.FEATHER, """
                §f跳过引导
                §7跳过新手引导
                """);
        this.item(11, skip.build(), () -> {
            player.performCommand("skiptutor");
        });

        ItemStackSheet visit = ItemStackSheet.fromString(Material.CLOCK, """
                §f快捷访问
                §7如果对方只有一个岛屿则直接访问
                §7不再弹出岛屿选择窗口
                §7当前：%s
                """.formatted(disableSingleIslandMenu ? "开启" : "禁用"));
        this.item(12, visit.build(),() -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisableSingleIslandMenu", (!disableSingleIslandMenu) + "");
            MessageUtils.success(player,"搞定!");
            close();
        });

        ItemStackSheet death = ItemStackSheet.fromString(Material.PLAYER_HEAD, """
                §f死后亡灵
                §7玩家死亡后根据环境生成一个怪物
                §7可能是以下生物:
                §7僵尸、骷髅、凋零骷髅、烈焰人、末影人
                §7当前：%s
                """.formatted(!disablePlayerMob ? "开启" : "禁用"));
        this.item(13, death.build(),() -> {
            PlayerPropertyManager.INSTANCE.setPropertyAsync(player, "DisablePlayerMob", (!disablePlayerMob) + "");
            MessageUtils.success(player,"搞定!");
            close();
        });

        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f返回主菜单");
        itemWithAsyncClickEvent(26, father.build(), () -> new PlayerMenu(player).open());
    }
}
