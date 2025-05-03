package org.event.Kevent.utils;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class RegionChecker {

    public static boolean isPlayerInRegion(Player player, FileConfiguration config) {
        // ดึงจุดเริ่มต้นและจุดปลายจาก config ส่วนกลาง
        Location start = config.getLocation("region.start");
        Location end = config.getLocation("region.end");

        // ถ้ายังไม่มีการกำหนดพื้นที่ ไม่ต้องตรวจสอบ
        if (start == null || end == null) {
            return false;
        }

        // คำนวณพื้นที่ของมุมที่ 1 และ 2
        double minX = Math.min(start.getX(), end.getX());
        double maxX = Math.max(start.getX(), end.getX());
        double minY = Math.min(start.getY(), end.getY());
        double maxY = Math.max(start.getY(), end.getY());
        double minZ = Math.min(start.getZ(), end.getZ());
        double maxZ = Math.max(start.getZ(), end.getZ());

        // ตรวจสอบว่า player อยู่ในพื้นที่หรือไม่
        Location playerLoc = player.getLocation();
        return playerLoc.getX() >= minX && playerLoc.getX() <= maxX &&
                playerLoc.getY() >= minY && playerLoc.getY() <= maxY &&
                playerLoc.getZ() >= minZ && playerLoc.getZ() <= maxZ;
    }
}
