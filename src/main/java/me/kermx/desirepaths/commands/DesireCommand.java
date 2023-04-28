package me.kermx.desirepaths.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DesireCommand {

    private final List<DesireCommand> subcommands;

    public DesireCommand() {
        this.subcommands = new ArrayList<>();
    }

    public abstract String getName();

    public abstract String getPermission();

    public abstract void execute(CommandSender sender, String[] args);

    public List<DesireCommand> getSubcommands() {
        return this.subcommands;
    }

    public void registerSubcommand(DesireCommand subcommand) {
        this.subcommands.add(subcommand);
    }

    public List<String> getTabComplete(CommandSender sender, String[] args) {
        return getSubcommands().stream().map(DesireCommand::getName).collect(Collectors.toList());
    }
}
