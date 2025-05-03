package org.event.Kevent.listeners;

import org.event.Kevent.Kevent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private final Kevent plugin;
    // ใช้ Map เก็บเวลาการแสดงข้อความล่าสุดของแต่ละผู้เล่น
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();
    // กำหนดระยะเวลาขั้นต่ำระหว่างการแสดงข้อความ (หน่วย: มิลลิวินาที)
    private static final long MESSAGE_COOLDOWN = 2000; // 2 วินาที

    public PlayerInteractListener(Kevent plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // ตรวจสอบว่าเป็นไม้กำหนดพื้นที่หรือไม่
        if (!isRegionStick(item)) return;

        // คลิกขวา = กำหนดจุดเริ่มต้น
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // ยกเลิกการตีบล็อก (ไม่ให้บล็อกแตก)
            event.setCancelled(true);

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                Location startLocation = clickedBlock.getLocation();
                plugin.getConfig().set("region.start", startLocation);
                plugin.saveConfig();

                // ส่งข้อความแจ้งเตือน - ใช้เมธอดที่มีการตรวจสอบการส่งซ้ำ
                sendMessageWithCooldown(player, "§aกำหนดจุดเริ่มต้นเรียบร้อยแล้ว! §7(" +
                        startLocation.getBlockX() + ", " +
                        startLocation.getBlockY() + ", " +
                        startLocation.getBlockZ() + ")");

                // ตรวจสอบว่ามีการกำหนดจุดสิ้นสุดไว้แล้วหรือไม่
                if (plugin.getConfig().contains("region.end")) {
                    showRegionCompletionMessage(player);
                }
            }
        }
        // คลิกซ้าย = กำหนดจุดสิ้นสุด
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // ยกเลิกการตีบล็อก (ไม่ให้บล็อกแตก)
            event.setCancelled(true);

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                Location endLocation = clickedBlock.getLocation();
                plugin.getConfig().set("region.end", endLocation);
                plugin.saveConfig();

                // ส่งข้อความแจ้งเตือน - ใช้เมธอดที่มีการตรวจสอบการส่งซ้ำ
                sendMessageWithCooldown(player, "§aกำหนดจุดสิ้นสุดเรียบร้อยแล้ว! §7(" +
                        endLocation.getBlockX() + ", " +
                        endLocation.getBlockY() + ", " +
                        endLocation.getBlockZ() + ")");

                // ตรวจสอบว่ามีการกำหนดจุดเริ่มต้นไว้แล้วหรือไม่
                if (plugin.getConfig().contains("region.start")) {
                    showRegionCompletionMessage(player);
                }
            }
        }
    }

    // เมธอดส่งข้อความพร้อมตรวจสอบการส่งซ้ำ
    private void sendMessageWithCooldown(Player player, String message) {
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // ตรวจสอบว่าผ่านระยะเวลา cooldown หรือไม่
        if (!lastMessageTime.containsKey(playerUUID) ||
                currentTime - lastMessageTime.get(playerUUID) > MESSAGE_COOLDOWN) {
            player.sendMessage(message);
            lastMessageTime.put(playerUUID, currentTime);
        }
    }

    // แยกเมธอดสำหรับแสดงข้อความแจ้งเตือนเมื่อกำหนดพื้นที่เสร็จสมบูรณ์
    private void showRegionCompletionMessage(Player player) {
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // ตรวจสอบว่าผ่านระยะเวลา cooldown หรือไม่
        if (!lastMessageTime.containsKey(playerUUID) ||
                currentTime - lastMessageTime.get(playerUUID) > MESSAGE_COOLDOWN) {

            // ดึงข้อมูลพิกัดจากคอนฟิก
            Location startLoc = (Location) plugin.getConfig().get("region.start");
            Location endLoc = (Location) plugin.getConfig().get("region.end");

            if (startLoc != null && endLoc != null) {
                // คำนวณขนาดของพื้นที่
                int width = Math.abs(endLoc.getBlockX() - startLoc.getBlockX()) + 1;
                int height = Math.abs(endLoc.getBlockY() - startLoc.getBlockY()) + 1;
                int depth = Math.abs(endLoc.getBlockZ() - startLoc.getBlockZ()) + 1;
                int volume = width * height * depth;

                // อัพเดทเวลาล่าสุด
                lastMessageTime.put(playerUUID, currentTime);

                // แสดงข้อมูลแบบแยกข้อความ
                player.sendMessage("§e══════ §6❰ §fพื้นที่กำหนดเสร็จสมบูรณ์ §6❱ §e══════");
                player.sendMessage("§aจุดเริ่มต้น: §b(" + startLoc.getBlockX() + ", " + startLoc.getBlockY() + ", " + startLoc.getBlockZ() + ")");
                player.sendMessage("§aจุดสิ้นสุด: §b(" + endLoc.getBlockX() + ", " + endLoc.getBlockY() + ", " + endLoc.getBlockZ() + ")");
                player.sendMessage("§aขนาด: §f" + width + "x" + height + "x" + depth + " §7(" + volume + " บล็อก)");
                player.sendMessage("§e══════════════════════════");
            }
        }
    }

    // ตรวจสอบว่าเป็นไม้กำหนดพื้นที่หรือไม่
    private boolean isRegionStick(ItemStack item) {
        if (item == null || item.getType() != Material.STICK) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }

        return meta.getDisplayName().equals(ChatColor.GOLD + "ไม้กำหนดพื้นที่") &&
                meta.hasCustomModelData() &&
                meta.getCustomModelData() == 12345;
    }
}