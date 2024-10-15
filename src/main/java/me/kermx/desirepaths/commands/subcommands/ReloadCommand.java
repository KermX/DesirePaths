package me.kermx.desirepaths.commands.subcommands;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesirePathsSub;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements DesirePathsSub {
    private final DesirePaths plugin;

    public ReloadCommand(final DesirePaths plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args) {
        plugin.reloadConfig();
        plugin.loadConfig();
        sender.sendMessage(ChatColor.GREEN + "DesirePaths configuration reloaded!");
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final String[] args) {
        return List.of();
    }
}
