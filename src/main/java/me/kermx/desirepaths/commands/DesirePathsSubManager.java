package me.kermx.desirepaths.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * This class serves for extension of SubCommands.
 * It registers all sub commands and provides
 * managing features over them such as getting
 * needed values, registering new subCommands, etc.
 */
public abstract class DesirePathsSubManager implements TabExecutor {
    private final List<SubCommandWrapper> subCommands = new ArrayList<>(); // List of sub commands

    /**
     * This method we use to add a new sub command
     *
     * @param subCommand  The sub command we want to register
     * @param aliases     The aliases of the command
     * @param permission  The permission for the command
     */
    protected void addSubCommand(final DesirePathsSub subCommand, final String[] aliases, final Permission permission) {
        subCommands.add(new SubCommandWrapper(subCommand, aliases, permission));
    }

    /**
     * Returns the needed wrapper to handle on command enter.
     *
     * @param arg   The argument that is expected to be a sub command
     * @return      The wrapper. Null if none
     */
    protected SubCommandWrapper getWrapper(final String arg) {
        for (final SubCommandWrapper wrapper : subCommands) {
            for (final String alias : wrapper.aliases) {
                if (alias.equalsIgnoreCase(arg)) {
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
    protected List<String> getFirstAliases(final CommandSender sender) {
        final List<String> result = new ArrayList<>();

        for (final SubCommandWrapper wrapper : subCommands) {
            if (sender.hasPermission(wrapper.getPermission())) {
                final String alias = wrapper.aliases[0];
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

        // Getters- used to get the sub command wrapper values.

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
