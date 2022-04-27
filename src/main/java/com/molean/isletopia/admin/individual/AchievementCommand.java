package com.molean.isletopia.admin.individual;

import com.molean.isletopia.infrastructure.assist.*;
import com.molean.isletopia.shared.database.AchievementDao;
import com.molean.isletopia.shared.model.Achievement;
import com.molean.isletopia.shared.model.PlayerAchievement;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Material;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AchievementCommand extends MultiCommand {

    public AchievementCommand() {

        super("achievement");

        //ach list => menu
        //ach grant <player> <ach>
        //ach set <ach> <key> <value>
        //ach set admin title qwq
        //ach create name
        this.createSubCommand(new SimpleCommand("list"))
                .simpleConsumer(player -> {
                    //achievement menu
                });
        this.createSubCommand(new UnCommand("create"))
                .unConsumer((player, s) -> {
                    Achievement achievement = new Achievement();
                    achievement.setName(s);
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

        this.createSubCommand(new SubCommand("grant"))
                .consumer((player, strings) -> {
                    Tasks.INSTANCE.async(() -> {
                        if (strings.size() < 2) {
                            MessageUtils.fail(player, "参数不足.");
                            return;
                        }
                        String target = strings.get(0);
                        String ach = strings.get(1);

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

                })
                .completer((player, strings) -> {
                    if (strings.size() == 1) {
                        return new ArrayList<>(UUIDManager.INSTANCE.getSnapshot().values());
                    }
                    try {
                        return new ArrayList<>(AchievementDao.getAchievements());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return new ArrayList<>();
                });


        this.createSubCommand(new ObjectPropertyCommand("set"))
                .availableObjects(() -> {
                    try {
                        return new ArrayList<>(AchievementDao.getAchievements());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return new ArrayList<>();
                })
                .availableKeys(s -> List.of("display", "icon", "desc", "score", "access"))
                .ObjectPropertyCommand((player, object, key, value) -> {
                    Tasks.INSTANCE.async(() -> {
                        try {
                            Achievement achievement = AchievementDao.getAchievement(object);
                            if (achievement == null) {
                                MessageUtils.fail(player, "不存在" + object + ".");
                                return;
                            }
                            switch (key.toLowerCase(Locale.ROOT)) {
                                case "display" -> {
                                    achievement.setDisplay(value
                                            .replaceAll("&", "§")
                                            .replaceAll("#", " ")
                                    );
                                }
                                case "icon" -> {
                                    Material material = null;
                                    try {
                                        material = Material.valueOf(value.toUpperCase(Locale.ROOT));
                                    } catch (IllegalArgumentException e) {
                                        MessageUtils.fail(player, value + "不是物品.");
                                        return;
                                    }
                                    achievement.setIcon(material.name());
                                }
                                case "desc" -> {
                                    achievement.setDescription(value
                                            .replaceAll("&","§")
                                            .replaceAll("#", " ")
                                            .replaceAll("\\\\[nl]", "\n")
                                    );
                                }
                                case "score" -> {
                                    int i = 0;
                                    try {
                                        i = Integer.parseInt(value);
                                    } catch (NumberFormatException e) {
                                        MessageUtils.fail(player, i + "不是数字.");
                                        return;
                                    }
                                    achievement.setScore(i);
                                }
                                case "access" -> {
                                    achievement.setAccess(value);
                                }
                            }
                            AchievementDao.updateAchievement(achievement);
                            MessageUtils.success(player, "成功.");
                        } catch (SQLException e) {
                            MessageUtils.success(player, "失败.");
                            e.printStackTrace();
                        }
                    });
                });
    }
}
