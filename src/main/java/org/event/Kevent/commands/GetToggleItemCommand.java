package org.event.Kevent.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.event.Kevent.Kevent;
import org.event.Kevent.items.DetectionToggleItem;

public class GetToggleItemCommand implements CommandExecutor {

    private final Kevent plugin;
    private final DetectionToggleItem toggleItem;

    public GetToggleItemCommand(Kevent plugin, DetectionToggleItem toggleItem) {
        this.plugin = plugin;
        this.toggleItem = toggleItem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("kevent.toggle")) {
                toggleItem.giveToggleItem(player);
                player.sendMessage("§7คุณได้รับไอเทมเปิด/ปิดการตรวจสอบแล้ว!");
                return true;
            } else {
                player.sendMessage("คุณไม่มีสิทธิ์ใช้คำสั่งนี้");
                return true;
            }
        }

        return false;
    }
}