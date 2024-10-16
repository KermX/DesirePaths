package me.kermx.desirepaths.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
* This interface represents the command and tab complete
* triggers for a sub command. When you create a new sub
* command, you implement this interface to it.
*
* Be aware that args should be moved by 1 because the
* first argument is our sub command, not the args of
* this sub command.
*/
public interface DesirePathsSub {
    void onCommand(CommandSender sender, String[] args); // On command trigger
    List<String> onTabComplete(CommandSender sender, String[] args); // On Tab Complete trigger
}
