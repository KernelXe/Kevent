package org.event.Kevent.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.SkullMeta;
import org.event.Kevent.gui.UserGUIManager;

public class GUIListener implements Listener {

    private final UserGUIManager guiManager;

    public GUIListener(UserGUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTitle().startsWith("User Manage")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            // จัดการปุ่ม BARRIER เพื่อปิด GUI
            if (event.getCurrentItem().getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }

            if (event.getCurrentItem().getType() == Material.ARROW) {
                String name = event.getCurrentItem().getItemMeta().getDisplayName();
                int currentPage = Integer.parseInt(event.getView().getTitle().split("Page ")[1].replace(")", "")) - 1;
                if (name.contains("Previous")) {
                    guiManager.openPage(player, currentPage - 1);
                } else if (name.contains("Next")) {
                    guiManager.openPage(player, currentPage + 1);
                }
                return;
            }

            if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();
                if (meta == null || meta.getOwningPlayer() == null) return;
                Player target = Bukkit.getPlayer(meta.getOwningPlayer().getUniqueId());
                if (target == null) return;

                switch (event.getClick()) {
                    case DOUBLE_CLICK:
                        if (player.hasPermission("usermanage.kill")) {
                            target.setHealth(0);
                            player.sendMessage("§aผู้เล่น §e" + target.getName() + " §aถูกกำจัด §e");
                            Bukkit.broadcastMessage(" §aผู้เล่น §e" + target.getName() + " §aถูกผู้คุมกำจัดแล้ว!" );

                        } else {
                            player.sendMessage("§cYou don't have permission to kill players.");
                        }
                        break;
                    case SHIFT_RIGHT:
                        if (player.hasPermission("usermanage.tphere")) {
                            target.teleport(player.getLocation());
                            player.sendMessage("§aเคลื่อนย้าย §e" + target.getName() + " §aมาหาคุณ.");
                        } else {
                            player.sendMessage("§cYou don't have permission to teleport players to you.");
                        }
                        break;
                    case RIGHT:
                        if (player.hasPermission("usermanage.tp")) {
                            player.teleport(target.getLocation());
                            player.sendMessage("§aเคลื่อนย้ายไปยัง §e" + target.getName());
                        } else {
                            player.sendMessage("§cYou don't have permission to teleport to players.");
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}