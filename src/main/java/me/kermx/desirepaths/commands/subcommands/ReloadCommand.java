package me.kermx.desirepaths.commands.subcommands;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesirePathsSub;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements DesirePathsSub {

    private final DesirePaths plugin;

    public ReloadCommand(DesirePaths plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        plugin.loadConfig();
        sender.sendMessage(ChatColor.GREEN + "DesirePaths configuration reloaded!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
