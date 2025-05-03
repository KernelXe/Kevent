package org.event.Kevent.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.event.Kevent.Kevent;

import java.util.ArrayList;
import java.util.List;

public class RegionStickItem implements CommandExecutor, Listener {

    private final Kevent plugin;
    private final ItemStack regionStick;

    public RegionStickItem(Kevent plugin) {
        this.plugin = plugin;
        this.regionStick = createRegionStick();
    }

    private ItemStack createRegionStick() {
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "ไม้กำหนดพื้นที่");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "คลิกขวาเพื่อกำหนดจุดเริ่มต้น");
            lore.add(ChatColor.YELLOW + "คลิกซ้ายเพื่อกำหนดจุดสิ้นสุด");
            meta.setLore(lore);

            // เพิ่ม CustomModelData เพื่อให้ไม้นี้มีความพิเศษ
            meta.setCustomModelData(12345);

            stick.setItemMeta(meta);
        }

        return stick;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "คำสั่งนี้ใช้ได้เฉพาะผู้เล่นเท่านั้น");
            return true;
        }

        Player player = (Player) sender;

        // ตรวจสอบสิทธิ์ให้ชัดเจน
        if (!player.hasPermission("kevent.regionstick")) {
            player.sendMessage(ChatColor.RED + "คุณไม่มีสิทธิ์ใช้คำสั่งนี้ (ต้องการสิทธิ์: kevent.regionstick)");
            return true;
        }

        // มอบไม้กำหนดพื้นที่ให้ผู้เล่น
        player.getInventory().addItem(regionStick);
        player.sendMessage(ChatColor.GREEN + "คุณได้รับไม้กำหนดพื้นที่แล้ว");

        return true;
    }
}