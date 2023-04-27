package me.kermx.desirepaths.commands.impl;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesireCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class MainCommand extends DesireCommand {

    public MainCommand(DesirePaths plugin) {
        this.registerSubcommand(new ReloadSubCMD(plugin));
    }

    @Override
    public String getName() {
        return "desirepaths";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "Incorrect Usage, please use the available subcommands!");
        for (DesireCommand command : getSubcommands()) {
            sender.sendMessage(ChatColor.RED + "/" + this.getName() + " " + command.getName());
        }
    }
}
