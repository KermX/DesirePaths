package me.kermx.desirepaths.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void sendMessage(CommandSender sender, String s) {
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(color(s));
            return;
        }
        //Not sure if you want to add PAPI support, but you can do it here.
        sender.sendMessage(color(s));
    }
}
