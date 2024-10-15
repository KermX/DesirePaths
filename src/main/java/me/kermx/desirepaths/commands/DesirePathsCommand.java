package me.kermx.desirepaths.commands;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.subcommands.MaintenanceCommand;
import me.kermx.desirepaths.commands.subcommands.ReloadCommand;
import me.kermx.desirepaths.commands.subcommands.ToggleCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.List;

public class DesirePathsCommand extends DesirePathsSubManager {

    /**
     * Calling this constructor we register all sub commands. Modify it as you wish,
     * but do not forget to register the sub commands, at the end of day.
     */
    public DesirePathsCommand(final DesirePaths plugin) {
        addSubCommand(
                new MaintenanceCommand(plugin),
                new String[] {"maintenance"},
                new Permission("desirepaths.maintenance"));
        addSubCommand(
                new ReloadCommand(plugin),
                new String[] {"reload"},
                new Permission("desirepaths.reload"));
        addSubCommand(
                new ToggleCommand(plugin),
                new String[] {"toggle"},
                new Permission("desirepaths.toggle"));
    }

    /**
     * On entered command, we process it if it is one of our sub commands here.
     *
     * @param sender    Sender
     * @param command   Command
     * @param label     Command as a string
     * @param args      Arguments of the command
     * @return          Was the process completed as intended
     */
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length >= 1) {
            final SubCommandWrapper wrapper = getWrapper(args[0]);

            if (wrapper != null) {
                if (sender.hasPermission(wrapper.getPermission())) {
                    wrapper.getSubCommand().onCommand(sender, args);
                    return true;
                } // Here you can add a message if no permissions found via else
            }
        }
        sender.sendMessage(ChatColor.RED + "Incorrect Usage! Try: /desirepaths <reload|toggle> [player]");
        return true; // We do not need to return false as we handled the command properly here
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 1) {
            return getFirstAliases(sender); // If first argument, returns the list of aliases of sub commands
        }

        final SubCommandWrapper wrapper = getWrapper(args[0]);

        if (wrapper != null) { // If second argument, returns tab list of the specific sub command wrapper
            return wrapper.getSubCommand().onTabComplete(sender, args);
        }

        return List.of(); // If not our command - return
    }
}
