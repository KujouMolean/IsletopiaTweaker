package com.molean.isletopia.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import com.molean.isletopia.annotations.Completion;
import com.molean.isletopia.shared.utils.UUIDManager;

import java.util.Collection;

@Completion("players")
public class PlayerCompletion implements CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return UUIDManager.INSTANCE.getSnapshot().values();
    }
}
