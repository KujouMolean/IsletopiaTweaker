package com.molean.isletopia.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.shared.database.AchievementDao;
import com.molean.isletopia.shared.model.Achievement;
import com.molean.isletopia.shared.model.PlayerAchievement;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@Singleton
@CommandAlias("achievement")
@CommandPermission("achievement.admin")
public class AchievementCommand extends BaseCommand {

    @Subcommand("list")
    public void list() {

    }

    @Subcommand("create")
    @Syntax("<Achievement>")
    public void create(Player player, String name) {
        Tasks.INSTANCE.async(() -> {
            Achievement achievement = new Achievement();
            achievement.setName(name);
            achievement.setAccess("todo");
            achievement.setScore(0);
            achievement.setDisplay("todo");
            achievement.setIcon("DIRT");
            achievement.setDescription("todo");
            try {
                AchievementDao.addAchievement(achievement);
                MessageUtils.success(player, "成功");
            } catch (SQLException e) {
                MessageUtils.fail(player, "失败");
                e.printStackTrace();
            }
        });
    }

    @Subcommand("grant")
    @Syntax("<Player> <Achievement>")
    public void grant(Player player, String target, String ach) {
        Tasks.INSTANCE.async(() -> {
            Achievement achievement = null;
            try {
                achievement = AchievementDao.getAchievement(ach);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (achievement == null) {
                MessageUtils.fail(player, "不存在名为" + ach + "的成就.");
                return;
            }

            UUID targetUUID = UUIDManager.get(target);
            PlayerAchievement playerAchievement = new PlayerAchievement();
            playerAchievement.setOwner(targetUUID);
            playerAchievement.setAchievement(ach);
            playerAchievement.setLocalDateTime(LocalDateTime.now());

            try {
                AchievementDao.addPlayerAchievement(playerAchievement);
                MessageUtils.success(player, "成功.");
            } catch (SQLException e) {
                MessageUtils.fail(player, "失败.");
                e.printStackTrace();
            }
        });
    }

    @Subcommand("setIcon")
    @Syntax("<Achievement> <Material>")
    public void setIcon(Player player, String ach, Material material) {
        Tasks.INSTANCE.async(() -> {
            Achievement achievement = null;
            try {
                achievement = AchievementDao.getAchievement(ach);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (achievement == null) {
                MessageUtils.fail(player, "不存在名为" + ach + "的成就.");
                return;
            }
            achievement.setIcon(material.name());
            try {
                AchievementDao.updateAchievement(achievement);
                MessageUtils.success(player, "成功.");
            } catch (SQLException e) {
                MessageUtils.fail(player, "失败.");
                throw new RuntimeException(e);
            }

        });

    }

    @Subcommand("setDisplay")
    @Syntax("<Achievement> <Access>")
    public void setDisplay(Player player, String ach, String access) throws SQLException {
        Tasks.INSTANCE.async(() -> {
            Achievement achievement = null;
            try {
                achievement = AchievementDao.getAchievement(ach);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (achievement == null) {
                MessageUtils.fail(player, "不存在名为" + ach + "的成就.");
                return;
            }
            achievement.setDisplay(access
                    .replaceAll("&", "§")
                    .replaceAll("#", " ")
            );
            try {
                AchievementDao.updateAchievement(achievement);
                MessageUtils.success(player, "成功.");
            } catch (SQLException e) {
                MessageUtils.fail(player, "失败.");
                throw new RuntimeException(e);
            }

        });
    }

    @Subcommand("setDesc")
    @Syntax("<Achievement> <Desc>")
    public void setDesc(Player player, String ach, String value) throws SQLException {
        Tasks.INSTANCE.async(() -> {
            Achievement achievement;
            try {
                achievement = AchievementDao.getAchievement(ach);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (achievement == null) {
                MessageUtils.fail(player, "不存在名为" + ach + "的成就.");
                return;
            }
            achievement.setDescription(value
                    .replaceAll("&", "§")
                    .replaceAll("#", " ")
                    .replaceAll("\\\\[nl]", "\n")
            );
            try {
                AchievementDao.updateAchievement(achievement);
                MessageUtils.success(player, "成功.");
            } catch (SQLException e) {
                MessageUtils.fail(player, "失败.");
                throw new RuntimeException(e);
            }
        });


    }

    @Subcommand("setScore")
    @Syntax("<Achievement> <Score>")
    public void setScore(Player player, String ach, int score) throws SQLException {
        Tasks.INSTANCE.async(() -> {
            Achievement achievement;
            try {
                achievement = AchievementDao.getAchievement(ach);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (achievement == null) {
                MessageUtils.fail(player, "不存在名为" + ach + "的成就.");
                return;
            }
            achievement.setScore(score);
            try {
                AchievementDao.updateAchievement(achievement);
                MessageUtils.success(player, "成功.");
            } catch (SQLException e) {
                MessageUtils.fail(player, "失败.");
                throw new RuntimeException(e);
            }
        });

    }

    @Subcommand("setAccess")
    @Syntax("<Achievement> <Access>")
    public void setAccess(Player player, String ach, String access) {
        Tasks.INSTANCE.async(() -> {
            Achievement achievement;
            try {
                achievement = AchievementDao.getAchievement(ach);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (achievement == null) {
                MessageUtils.fail(player, "不存在名为" + ach + "的成就.");
                return;
            }
            achievement.setAccess(access);
            try {
                AchievementDao.updateAchievement(achievement);
                MessageUtils.success(player, "成功.");
            } catch (SQLException e) {
                MessageUtils.fail(player, "失败.");
                throw new RuntimeException(e);
            }
        });

    }
}
