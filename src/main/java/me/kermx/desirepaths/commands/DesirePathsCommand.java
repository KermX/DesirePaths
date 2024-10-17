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

/**
* This class represents the Main, Primary command.
* Here we register sub commands and process the
* arguments of the main command based on user's
* entered information.
*/
public class DesirePathsCommand extends DesirePathsSubManager {

    /**
     * Calling this constructor we register all sub commands.
     *
     * addSubCommand(
     * new SubCommand class,
     * new String[] {"aliases"},
     * new Permission("permission"));
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
     * On entered command, we process it if it's one of our sub commands.
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
                } // Here you can add a message if no permissions found via else. Or you can put it in the getWrapper method.
            }
        }
        sender.sendMessage(ChatColor.RED + "Incorrect Usage! Try: /desirepaths <reload|toggle> [player]");
        return true; // We do not need to return false as we handled the command properly here
    }

    /**
     * On tab complete, we process it if it's our command.
     *
     * @param sender    Sender
     * @param command   Command
     * @param label     Command as a string
     * @param args      Arguments of the command
     * @return          Was the process completed as intended
     */
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
