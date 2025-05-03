package org.event.Kevent.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.event.Kevent.Kevent;

import java.util.ArrayList;
import java.util.List;

public class DetectionToggleItem implements Listener {

    private final Kevent plugin;
    private ItemStack toggleItem;

    public DetectionToggleItem(Kevent plugin) {
        this.plugin = plugin;
        this.toggleItem = createToggleItem();

        // ลงทะเบียนเป็น event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private ItemStack createToggleItem() {
        boolean isEnabled = plugin.getConfig().getBoolean("detection-enabled", true);
        Material itemMaterial = isEnabled ? Material.LIME_DYE : Material.GRAY_DYE;

        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "ปุ่มเปิด/ปิดการตรวจสอบ");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "คลิกขวาเพื่อสลับการตรวจสอบ");
            lore.add(ChatColor.GRAY + "สถานะปัจจุบัน: " +
                    (isEnabled ? ChatColor.GREEN + "เปิดใช้งาน" : ChatColor.RED + "ปิดใช้งาน"));
            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }

    public void giveToggleItem(Player player) {
        player.getInventory().addItem(toggleItem);
    }

    private void updateItemForAllPlayers() {
        boolean isEnabled = plugin.getConfig().getBoolean("detection-enabled", true);
        Material newMaterial = isEnabled ? Material.LIME_DYE : Material.GRAY_DYE;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && (item.getType() == Material.LIME_DYE || item.getType() == Material.GRAY_DYE)) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null && meta.hasDisplayName() &&
                            meta.getDisplayName().equals(ChatColor.GOLD + "ปุ่มเปิด/ปิดการตรวจสอบ")) {

                        // เปลี่ยนประเภทของไอเทม
                        item.setType(newMaterial);

                        // อัพเดท lore
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GRAY + "คลิกขวาเพื่อสลับการตรวจสอบ");
                        lore.add(ChatColor.GRAY + "สถานะปัจจุบัน: " +
                                (isEnabled ? ChatColor.GREEN + "เปิดใช้งาน" : ChatColor.RED + "ปิดใช้งาน"));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && (item.getType() == Material.LIME_DYE || item.getType() == Material.GRAY_DYE)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName() &&
                    meta.getDisplayName().equals(ChatColor.GOLD + "ปุ่มเปิด/ปิดการตรวจสอบ")) {

                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);

                    // สลับสถานะการตรวจสอบ
                    boolean currentStatus = plugin.getConfig().getBoolean("detection-enabled", true);
                    plugin.getConfig().set("detection-enabled", !currentStatus);
                    plugin.saveConfig();

                    player.sendMessage(ChatColor.YELLOW + "การตรวจสอบการเคลื่อนไหวของผู้เล่นตอนนี้ " +
                            (currentStatus ? ChatColor.RED + "ปิดใช้งาน" : ChatColor.GREEN + "เปิดใช้งาน"));

                    // อัพเดทคำอธิบายไอเทมเพื่อแสดงสถานะใหม่
                    updateItemForAllPlayers();
                }
            }
        }
    }
}
