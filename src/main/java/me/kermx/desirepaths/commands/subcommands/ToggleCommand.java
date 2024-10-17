package me.kermx.desirepaths.commands.subcommands;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesirePathsSub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
* A class for Toggle sub command.
*/
public class ToggleCommand implements DesirePathsSub {
    private final DesirePaths plugin;

    /**
    * Constructs this class on initialization.
    */
    public ToggleCommand(final DesirePaths plugin) {
        this.plugin = plugin;
    }

    /**
    * If the method was triggered, we process it.
    * Based on who is the sender we either handle
    * player or console method.
    *
    * @param sender The sender (unknown type yet)
    * @param args   Args of the command. Remember that the 1st argument is our sub command
    */
    @Override
    public void onCommand(final CommandSender sender, final String[] args) {
        if (args.length <= 1) {
            toggle(sender);
        } else if (sender.hasPermission("desirepaths.toggle.others")) {
            toggleOther(sender, args);
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to toggle other players.");
        }
    }

    /**
     * To process if the sender is player.
     *
     * @param sender The player
     */
    private void toggle(final CommandSender sender) {
        final Player player = (Player) sender;
        final UUID playerId = player.getUniqueId();

        final boolean newToggle = plugin.getToggleManager().toggle(playerId);
        final String toggleStatus = newToggle ? "on" : "off";

        player.sendMessage("IsToggleMode? " + toggleStatus);
        player.sendMessage(ChatColor.GREEN + "DesirePaths toggled " + toggleStatus + "!");
     }

    /**
     * To process if the sender is console.
     *
     * @param sender The console
     * @param args   Args of the command. Remember that the 1st argument is our sub command
     */
    private void toggleOther(final CommandSender sender, final String[] args) {
        if (args.length >= 2) {
            final String playerName = args[1];
            final Player player = Bukkit.getPlayer(playerName);

            if (player != null) {
                final UUID playerId = player.getUniqueId();

                final boolean newToggle = plugin.getToggleManager().toggle(playerId);
                final String toggleStatus = newToggle ? "on" : "off";

                sender.sendMessage(ChatColor.GREEN + "DesirePaths toggled " + toggleStatus + "!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Unable to find online player with name: " + playerName);
            }
        } else {
            sender.sendMessage(ChatColor.GREEN + "Usage: /desirepaths toggle [player]");
        }
    }

    /**
    * When tab complete is triggered and it's triggered by console,
    * we return list of players.
    *
    * @param sender The sender (unknown yet)
    * @param args   Args of the command. Remember that the 1st argument is our sub command
    * @return List of players
    */
    @Override
    public List<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (!sender.hasPermission("desirepaths.toggle.others")) {
            return List.of();
        } else {
            return playerList();
        }
    }

    /**
     * If the tab complete event is coming from console,
     * we return players list.
     *
     * @return List of players
     */
    private List<String> playerList() {
        final List<String> onlinePlayers = new ArrayList<>();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player.getName());
        }

        return onlinePlayers;
    }
}
