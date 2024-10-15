package me.kermx.desirepaths.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface DesirePathsSub {
    boolean onCommand(CommandSender sender, String[] args);
    List<String> onTabComplete(CommandSender sender, String[] args);
}
