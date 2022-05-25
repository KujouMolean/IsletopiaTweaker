package com.molean.isletopia.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import com.molean.isletopia.annotations.Completion;
import com.molean.isletopia.shared.database.AchievementDao;
import com.molean.isletopia.shared.model.Achievement;

import java.sql.SQLException;
import java.util.Collection;

@Completion("achievements")
public class AchievementCompletion implements CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {
    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        Collection<String> achievements = null;
        try {
            achievements = AchievementDao.getAchievements();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return achievements;

    }
}
