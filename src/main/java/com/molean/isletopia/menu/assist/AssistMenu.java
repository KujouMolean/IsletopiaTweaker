package com.molean.isletopia.menu.assist;

import com.molean.isletopia.infrastructure.individual.bars.SidebarManager;
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

        ItemStackSheet entityBar = ItemStackSheet.fromString(Material.BOOK, """
                §f实体统计计分板
                §7在屏幕右侧显示当前岛屿的生物统计
                §7该统计每秒刷新一次，精确统计。
                §7当前状态：%s
                """.formatted("EntityBar".equalsIgnoreCase(sidebar) ? "开启" : "关闭"));
        this.item(0, entityBar.build(),() -> {
            player.performCommand("EntityBar");
            close();
        });
        ItemStackSheet productionBar = ItemStackSheet.fromString(Material.BOOK, """
                §f产量统计计分板
                §7在屏幕右侧显示当前岛屿的掉落物品产量
                §7该统计会记录最近1分钟的掉落物品
                §7当前状态：%s
                """.formatted("ProductionBar".equalsIgnoreCase(sidebar) ? "开启" : "关闭"));
        this.item(1, productionBar.build(),() -> {
            player.performCommand("ProductionBar");
            close();
        });
        ItemStackSheet inbox = ItemStackSheet.fromString(Material.CHEST, """
                §f邮箱
                §7系统奖励可能会发放到邮箱
                """);
        this.itemWithAsyncClickEvent(2, inbox.build(),() -> new InboxMenu(player).open());


//        ItemStackSheet mspt = ItemStackSheet.fromString(Material.CHEST, """
//                §f服务器MSPT条
//                §7在顶部显示服务器MSPT状态条
//                §7当前：关闭
//                """);
//        this.item(1, productionBar.build(),() -> {
//            player.performCommand("ProductionBar");
//            close();
//        });

        ItemStackSheet create = ItemStackSheet.fromString(Material.CHEST, """
                §f创建新岛屿
                §7消耗一个信标，即可创建一个新岛屿。
                §7请将信标放置在副手。
                """);
        this.item(3, create.build(),() -> {
            player.performCommand("is create");
            close();
        });

        ItemStackSheet claim = ItemStackSheet.fromString(Material.CHEST, """
                §f领取离线小号
                §7站在你的离线小号岛屿上，输入/is claimOffline [密码]
                §7副手持一个信标，消耗该信标即可领取此岛屿。
                """);

        this.item(4, claim.build(),() -> {
            MessageUtils.info(player, "手动使用: /is claimOffline [密码]");
            close();
        });

//        ItemStackSheet visit = ItemStackSheet.fromString(Material.CHEST, """
//                §f快捷访问
//                §7如果对方只有一个岛屿则直接访问
//                §7当前：关闭
//                """);
//        this.item(4, productionBar.build(),() -> {
//            MessageUtils.info(player, "请手动使用: /is claimOffline [密码]");
//            close();
//        });

        ItemStackSheet sit = ItemStackSheet.fromString(Material.CHEST, """
                §f随地坐下
                §7长按Shift坐下
                §7当前: 开启
                """);

        ItemStackSheet rail = ItemStackSheet.fromString(Material.CHEST, """
                §f铁轨传送
                §7在铁轨上按下Shift传送到另一端
                §7当前: 开启
                """);

        ItemStackSheet ride = ItemStackSheet.fromString(Material.CHEST, """
                §f抱起生物
                §7按下Shift+右键举起生物
                §7当前: 开启
                """);

//        ItemStackSheet save = ItemStackSheet.fromString(Material.CHEST, """
//                §f下载存档
//                §7下载你当前岛屿的存档
//                """);

//        ItemStackSheet skip = ItemStackSheet.fromString(Material.CHEST, """
//                §f跳过引导
//                §7跳过新手引导
//                §7当前：已跳过/已完成/未跳过
//                """);


//        ItemStackSheet protect = ItemStackSheet.fromString(Material.CHEST, """
//                §f岩浆保护
//                §7阻止使用方块填充岩浆
//                §7当前：开启
//                """);

//        ItemStackSheet confirm = ItemStackSheet.fromString(Material.CHEST, """
//                §f自动确认对话框
//                §7自动确认所有弹出的对话框
//                §7正常玩家请不要触碰此项
//                §c此选项很危险，开启后造成的一切后果自负
//                §c此选项很危险，开启后造成的一切后果自负
//                §c此选项很危险，开启后造成的一切后果自负
//                §7当前：关闭
//                """);

        this.itemWithAsyncClickEvent(2, inbox.build(),() -> new InboxMenu(player).open());
    }
}
