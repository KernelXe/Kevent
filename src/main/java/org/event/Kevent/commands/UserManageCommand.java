package org.event.Kevent.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.event.Kevent.Kevent;

public class UserManageCommand implements CommandExecutor {

    private final Kevent plugin;

    public UserManageCommand(Kevent plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // ตรวจสอบว่า sender เป็น Player หรือไม่
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }


        if (!player.hasPermission("usermanage.open")) {
            player.sendMessage("§cYou don't have permission to open the user management GUI.");
            return true;
        }


        plugin.getGuiManager().openGUI(player);
        return true;
    }
}
