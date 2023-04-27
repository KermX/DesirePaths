package me.kermx.desirepaths.managers;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesireCommand;
import me.kermx.desirepaths.commands.impl.MainCommand;
import me.kermx.desirepaths.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class CommandManager extends Manager implements TabExecutor {

    private final List<DesireCommand> commands;

    public CommandManager(DesirePaths plugin) {
        super(plugin);
        this.commands = Arrays.asList(new MainCommand(plugin));
    }

    @Override
    public void setup() {
        this.commands.forEach(x -> {
            PluginCommand command = plugin.getCommand(x.getName());
            if (command == null) {
                Utils.sendMessage(null, "&6>>&4This is a command error, the command " + x.getName() + " has not been registered in the plugin.yml!");
                return;
            }
            command.setExecutor(this);
        });
    }

    @Override
    public void load() {

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Predicate<DesireCommand> checkPermission = x -> {
            if (x.getPermission() != null && !commandSender.hasPermission(x.getPermission())) {
                Utils.sendMessage(commandSender, "&6>>&4You do not have permission to use this command!");
                return true;
            }
            return false;
        };
        this.commands.stream().filter(x -> x.getName().equalsIgnoreCase(s)).findFirst().ifPresent(x -> {
            if (checkPermission.test(x)) return;

            if (strings.length == 0 || x.getSubcommands().isEmpty()) {
                x.execute(commandSender, strings);
                return;
            }
            executeSubCommand(x.getSubcommands(), 0, commandSender, strings);
        });
        return true;
    }

    private void executeSubCommand(List<DesireCommand> commands, int index, CommandSender commandSender, String[] strings) {
        for (DesireCommand command : commands) {
            if (!command.getName().equalsIgnoreCase(strings[index])) continue;

            if (strings.length == index + 1 || command.getSubcommands().isEmpty()) {
                command.execute(commandSender, strings);
                return;
            }
            executeSubCommand(command.getSubcommands(), index + 1, commandSender, strings);
            return;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> options = new ArrayList<>();
        Predicate<DesireCommand> checkPermission = x -> x.getPermission() != null && !commandSender.hasPermission(x.getPermission());
        for (DesireCommand desireCommand : this.commands) {
            if (!desireCommand.getName().equalsIgnoreCase(s) || checkPermission.test(desireCommand)) continue;

            if (strings.length == 1 || desireCommand.getSubcommands().isEmpty()) {
                options.addAll(desireCommand.getTabComplete(commandSender, strings));
                break;
            }
            tabSubCommand(desireCommand.getSubcommands(), 1, commandSender, strings, options);
        }
        return options;
    }

    private void tabSubCommand(List<DesireCommand> commands, int index, CommandSender commandSender, String[] args, List<String> options) {
        for (DesireCommand command : commands) {
            if (!command.getName().equalsIgnoreCase(args[index])) continue;

            if (args.length == index + 1 || command.getSubcommands().isEmpty()) {
                options.addAll(command.getTabComplete(commandSender, args));
                return;
            }
            tabSubCommand(command.getSubcommands(), index + 1, commandSender, args, options);
            return;
        }
    }
}
