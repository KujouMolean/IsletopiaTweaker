package com.molean.isletopia.distribute;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.shared.utils.UUIDManager;
import org.bukkit.command.CommandSender;

import java.util.Objects;

@Singleton
@CommandAlias("parameter")
@CommandPermission("isletopia.parameter")
public class ParameterCommand extends BaseCommand {

    @AutoInject
    private UniversalParameter universalParameter;

    @Subcommand("set")
    public void set(String target, String key, String value) {
        universalParameter.setParameter(UUIDManager.get(target), key, value);
    }

    @Subcommand("view|get")
    public void view(CommandSender commandSender, String target, String key) {
        String s = universalParameter.getParameter(UUIDManager.get(target), key);
        assert s != null;
        commandSender.sendMessage(s);
    }


    @Subcommand("add")
    public void add(String target, String key, String value) {
        universalParameter.addParameter(Objects.requireNonNull(UUIDManager.get(target)), key, value);
    }

    @Subcommand("remove")
    public void remove(String target, String key, String value) {
        universalParameter.removeParameter(UUIDManager.get(target), key, value);
    }

    @Subcommand("unset")
    public void unset(String target, String key) {
        universalParameter.unsetParameter(UUIDManager.get(target), key);
    }
}

