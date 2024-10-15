package me.kermx.desirepaths.commands.subcommands;

import me.kermx.desirepaths.commands.DesirePathsSub;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ToggleCommand implements DesirePathsSub {
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
