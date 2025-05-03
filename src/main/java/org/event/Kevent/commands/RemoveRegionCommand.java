package org.event.Kevent.commands;

import org.event.Kevent.Kevent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveRegionCommand implements CommandExecutor {

    private final Kevent plugin;

    public RemoveRegionCommand(Kevent plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;


            if (!player.hasPermission("kevent.region.remove")) {
                player.sendMessage("§cคุณไม่มีสิทธิ์ใช้คำสั่งนี้!");
                return true;
            }


            if (plugin.getConfig().contains("region.start") && plugin.getConfig().contains("region.end")) {
                // ลบข้อมูลพื้นที่ใน config
                plugin.getConfig().set("region.start", null);
                plugin.getConfig().set("region.end", null);
                plugin.saveConfig();

                player.sendMessage("§aพื้นที่ที่กำหนดไว้ได้ถูกลบออกแล้ว!");
            } else {
                player.sendMessage("§cยังไม่มีการกำหนดพื้นที่ใดๆ!");
            }
            return true;
        } else {
            sender.sendMessage("§cคำสั่งนี้สามารถใช้ได้ในเกมเท่านั้น!");
            return false;
        }
    }
}