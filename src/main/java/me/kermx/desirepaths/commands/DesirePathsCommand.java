package me.kermx.desirepaths.commands;

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
     * Calling this constructor we register all sub commands.
     */
    public DesirePathsCommand() {
        addSubCommand(new MaintenanceCommand(), new String[] {"maintenance"}, new Permission("desirepaths.maintenance"));
        addSubCommand(new ReloadCommand(), new String[] {"reload"}, new Permission("desirepaths.reload"));
        addSubCommand(new ToggleCommand(), new String[] {"toggle"}, new Permission("desirepaths.toggle"));
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) { // We handle the sub command, so that's why it is 1.
            final SubCommandWrapper wrapper = getWrapperFromLable(label);

            if (wrapper != null) {
                if (sender.hasPermission(wrapper.getPermission())) {
                    wrapper.getSubCommand().onCommand(sender, args);
                    return true;
                } // Here you can add a message if no permissions found
            }
        }
        sender.sendMessage(ChatColor.RED + "Incorrect Usage! Try: /desirepaths <reload|toggle> [player]");
        return true; // We do not need to return false as we handled the command properly here
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return getFirstAliases(sender); // If this is 1st argument, returns list of avaliable sub commands
        }

        final SubCommandWrapper wrapper = getWrapperFromLable(label);

        if (wrapper != null) { // If this is 2nd argument and it actually exists, returns list of args for this sub command
            return wrapper.getSubCommand().onTabComplete(sender, args);
        }

        return List.of(); // Returns nothing if this is not our command
    }
}
