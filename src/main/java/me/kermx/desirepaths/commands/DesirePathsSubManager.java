package me.kermx.desirepaths.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.Permission;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class serves for extension of SubCommands.
 * It registers all sub commands and provides
 * managing features over them such as getting
 * needed values, registering new subCommands, etc.
 */
public abstract class DesirePathsSubManager implements TabExecutor {
    private final List<SubCommandWrapper> subCommands = new ArrayList<>();

    /**
     * This method we use to add a new sub command
     *
     * @param subCommand  The sub command we want to register
     * @param aliases     The aliases of the command
     * @param permission  The permission for the command
     */
    protected void addSubCommand(DesirePathsSub subCommand, String[] aliases, Permission permission) {
        subCommands.add(new SubCommandWrapper(subCommand, aliases, permission));
    }

    /**
     * Returns the needed wrapper to handle on command enter.
     *
     * @param label The command as a string
     * @return      The wrapper. Null if none
     */
    protected SubCommandWrapper getWrapper(String label) {
        for (SubCommandWrapper wrapper : subCommands) {
            for (String alias : wrapper.aliases) {
                if (alias.equalsIgnoreCase(label)) {
                    return wrapper;
                }
            }
        }
        return null;
    }

    /**
     * Returns aliases for the first argument, aka sub commands.
     * Additionally, checks for the permission before returning.
     * You can add more checks here in the future.
     *
     * @param sender The sender
     * @return The list of first aliases (sub commands)
     */
    protected List<String> getFirstAliases(CommandSender sender) {
        final List<String> result = new ArrayList<>();
        for (SubCommandWrapper wrapper : subCommands) {
            if (sender.hasPermission(wrapper.getPermission())) {
                String alias = wrapper.aliases[0];
                result.add(alias);
            }
        }
        return result;
    }

    /**
     * This class we use as a wrapper for our sub commands,
     * storing all needed values here for each sub command.
     */
    protected static class SubCommandWrapper {
        private final DesirePathsSub subCommand;
        private final String[] aliases;
        private final Permission permission;

        /**
         * Constructs the sub command.
         *
         * @param subCommand  The sub command we want to register
         * @param aliases     The aliases of the command
         * @param permission  The permission for the command
         */
        protected SubCommandWrapper(DesirePathsSub subCommand, String[] aliases, Permission permission) {
            this.subCommand = subCommand;
            this.aliases = aliases;
            this.permission = permission;
        }

        // Getters - nothing to explain.

        protected DesirePathsSub getSubCommand() {
            return subCommand;
        }

        protected String[] getAliases() {
            return aliases;
        }

        protected Permission getPermission() {
            return permission;
        }
    }
}
