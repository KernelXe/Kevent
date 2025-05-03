package org.event.Kevent.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.event.Kevent.Kevent;

import java.util.ArrayList;
import java.util.List;

public class UserGUIManager implements Listener {

    private final Kevent plugin;
    private final int ITEMS_PER_PAGE = 45; // เปลี่ยนจาก 45 เป็น 3 เพื่อทดสอบ
    private final boolean TEST_MODE = false; // เพิ่มตัวแปรโหมดทดสอบ

    public UserGUIManager(Kevent plugin) {
        this.plugin = plugin;
        // ลงทะเบียน Listener ในคอนสตรัคเตอร์
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openGUI(Player viewer) {
        openPage(viewer, 0);
    }

    public void openPage(Player viewer, int page) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        int totalPages = (int) Math.ceil((double) players.size() / ITEMS_PER_PAGE);

        if (totalPages == 0) totalPages = 1; // ป้องกันกรณีไม่มีผู้เล่นออนไลน์

        // ถ้าอยู่ในโหมดทดสอบและมีหน้าเดียว ให้กำหนดเป็น 3 หน้า
        if (TEST_MODE && totalPages <= 1) {
            totalPages = 3; // บังคับให้มี 3 หน้าสำหรับทดสอบ
        }

        Inventory inv = Bukkit.createInventory(null, 54, "User Manage (Page " + (page + 1) + ")");

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, players.size());

        for (int i = start; i < end; i++) {
            Player target = players.get(i);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.setDisplayName("§a" + target.getName());
                List<String> lore = new ArrayList<>();
                lore.add("§7DOUBLE_CLICK-Click: Kill");
                lore.add("§7Right-Click: TP to Player");
                lore.add("§7SHIFT+RIGHT-Click: TP Here");

                meta.setLore(lore);
                skull.setItemMeta(meta);
            }
            inv.addItem(skull);
        }

        // ถ้าอยู่ในโหมดทดสอบ เพิ่มการแสดงข้อมูลทดสอบ
        if (TEST_MODE) {
            viewer.sendMessage("§bTEST MODE: หน้า " + (page + 1) + "/" + totalPages);
            viewer.sendMessage("§bจำนวนผู้เล่นทั้งหมด: " + players.size());
        }

        // ปุ่มย้อนกลับ
        if (page > 0) {
            ItemStack previousPage = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = previousPage.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§ePrevious Page");
                List<String> lore = new ArrayList<>();
                lore.add("§7Click to go back");
                meta.setLore(lore);
                previousPage.setItemMeta(meta);
            }
            inv.setItem(45, previousPage);

            if (TEST_MODE) {
                viewer.sendMessage("§aสร้างปุ่มย้อนกลับที่ช่อง 45");
            }
        }

        // ปุ่มหน้าถัดไป
        if (page + 1 < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = nextPage.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§eNext Page");
                List<String> lore = new ArrayList<>();
                lore.add("§7Click to go forward");
                meta.setLore(lore);
                nextPage.setItemMeta(meta);
            }
            inv.setItem(53, nextPage);

            if (TEST_MODE) {
                viewer.sendMessage("§aสร้างปุ่มหน้าถัดไปที่ช่อง 53");
            }
        }

        // เพิ่มปุ่มกลับที่ชัดเจน (ช่วยให้มองเห็นได้ง่าย)
        ItemStack backButton = new ItemStack(Material.BARRIER, 1);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§cClose");
            backButton.setItemMeta(backMeta);
        }
        inv.setItem(49, backButton);

        viewer.openInventory(inv);
    }

    // Event Listener ที่มีอยู่เดิม ไม่เปลี่ยนแปลง
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // โค้ดเดิมทั้งหมดอยู่ตรงนี้ ไม่มีการเปลี่ยนแปลง
        String title = event.getView().getTitle();

        // ตรวจสอบว่าเป็นอินเวนทอรี่ของเรา
        if (!title.startsWith("User Manage (Page ")) {
            return;
        }

        // ยกเลิกการกดค่าเริ่มต้น
        event.setCancelled(true);

        // ต้องเป็นผู้เล่นเท่านั้น
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        // ตรวจสอบว่ามีการคลิกที่ไอเทม
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // ดึงเลขหน้าปัจจุบันจากชื่อ
        int currentPage = Integer.parseInt(title.split("\\(Page ")[1].split("\\)")[0]) - 1;

        // ตรวจสอบปุ่มที่คลิก
        if (event.getSlot() == 49 && clickedItem.getType() == Material.BARRIER) {
            // ปุ่มปิด
            player.closeInventory();
            return;
        } else if (event.getSlot() == 45 && clickedItem.getType() == Material.ARROW) {
            // ปุ่มย้อนกลับ
            if (currentPage > 0) {
                openPage(player, currentPage - 1);
            }
            return;
        } else if (event.getSlot() == 53 && clickedItem.getType() == Material.ARROW) {
            // ปุ่มหน้าถัดไป
            openPage(player, currentPage + 1);
            return;
        }

        // จัดการคลิกที่หัวผู้เล่น
        if (clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.hasItemMeta()) {
            String playerName = clickedItem.getItemMeta().getDisplayName().substring(2); // ตัด §a ออก
            Player target = Bukkit.getPlayer(playerName);

            if (target != null) {



                if (event.isRightClick() && !event.isShiftClick()) {
                    player.teleport(target.getLocation());
                    player.sendMessage("§aคุณได้เทเลพอร์ตไปหา §e" + target.getName());
                    player.closeInventory();
                }

                else if (event.isRightClick() && event.isShiftClick()) {
                    target.teleport(player.getLocation());
                    player.sendMessage("§aคุณได้เทเลพอร์ต §e" + target.getName() + " §aมาหาคุณ");
                    target.sendMessage("§aคุณถูกเทเลพอร์ตไปหา §e" + player.getName());
                    player.closeInventory();
                }
            }
        }
    }
}