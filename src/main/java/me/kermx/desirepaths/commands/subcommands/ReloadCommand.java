package me.kermx.desirepaths.commands.subcommands;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesirePathsSub;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
* A class for Reload sub command.
*/
public class ReloadCommand implements DesirePathsSub {
    private final DesirePaths plugin;

    /**
    * Constructs this class on initialization.
    */
    public ReloadCommand(final DesirePaths plugin) {
        this.plugin = plugin;
    }

    /**
    * If the method was triggered, we process it.
    *
    * @param sender The sender
    * @param args   Args of the command
    */
    @Override
    public void onCommand(final CommandSender sender, final String[] args) {
        plugin.reloadConfig();
        plugin.loadConfig();
        sender.sendMessage(ChatColor.GREEN + "DesirePaths configuration reloaded!");
    }

    /**
    * When tab complete is triggered, we process it.
    *
    * @param sender The sender
    * @param args   Args of the command
    * @return Nothing
    */
    @Override
    public List<String> onTabComplete(final CommandSender sender, final String[] args) {
        return List.of();
    }
}
