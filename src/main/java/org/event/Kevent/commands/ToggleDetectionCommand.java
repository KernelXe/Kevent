package org.event.Kevent.commands;

import org.event.Kevent.Kevent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleDetectionCommand implements CommandExecutor {

    private final Kevent plugin;

    public ToggleDetectionCommand(Kevent plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Toggle detection status
            boolean currentStatus = plugin.getConfig().getBoolean("detection-enabled", true);
            plugin.getConfig().set("detection-enabled", !currentStatus);
            plugin.saveConfig();

            player.sendMessage("Player movement detection is now " + (currentStatus ? "disabled" : "enabled"));
            return true;
        }
        return false;
    }
}
